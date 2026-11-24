package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.oneClient
import br.pucpr.brincafacil.locacao.brinquedos.BrinquedoRepository
import br.pucpr.brincafacil.locacao.clientes.ClienteRepository
import br.pucpr.brincafacil.locacao.decoracoes.DecoracaoRepository
import br.pucpr.brincafacil.locacao.oneBrinquedo
import br.pucpr.brincafacil.locacao.oneDecoracao
import br.pucpr.brincafacil.locacao.oneReserva
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.Optional

class ReservaServiceTest {
    private val repository = mockk<ReservaRepository>()
    private val clientes = mockk<ClienteRepository>()
    private val brinquedos = mockk<BrinquedoRepository>()
    private val decoracoes = mockk<DecoracaoRepository>()
    private val service = ReservaService(repository, clientes, brinquedos, decoracoes)

    private val inicio = LocalDate.now().plusDays(5)
    private val fim = inicio.plusDays(2)
    private val cliente = oneClient(id = 1L)
    private val brinquedo = oneBrinquedo(id = 2L, nome = "Escorregador", descricao = null)
    private val decoracao = oneDecoracao(id = 3L, nome = "Painel", descricao = null)
    private val reserva = oneReserva(id = 10L, cliente = cliente, dataInicio = inicio, dataFim = fim)
    private val response = ReservaResponse(10L, 1L, inicio, fim, false, emptyList(), emptyList())

    @BeforeEach
    fun setup() {
        clearMocks(repository, clientes, brinquedos, decoracoes)
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(repository, clientes, brinquedos, decoracoes)
    }

    @Test
    fun `list ordena por data de inicio e ID`() {
        val sort = Sort.by("dataInicio", "id")
        every { repository.findAll(sort) } returns listOf(reserva)

        service.list() shouldBe listOf(response)

        verify(exactly = 1) { repository.findAll(sort) }
    }

    @Test
    fun `get retorna a reserva solicitada`() {
        every { repository.findById(10L) } returns Optional.of(reserva)

        service.get(10L) shouldBe response
    }

    @Test
    fun `get retorna 404 para reserva inexistente`() {
        every { repository.findById(10L) } returns Optional.empty()

        val error = shouldThrow<ResponseStatusException> { service.get(10L) }

        error.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    @Test
    fun `insert salva a reserva e vincula os itens sem duplicar IDs`() {
        val request = ReservaRequest(1L, inicio, fim, listOf(2L, 2L), listOf(3L, 3L))
        every { clientes.findById(1L) } returns Optional.of(cliente)
        every { repository.save(any<Reserva>()) } returns reserva
        every { repository.findForUpdate(10L) } returns reserva
        every { brinquedos.findForUpdate(2L) } returns brinquedo
        every { decoracoes.findForUpdate(3L) } returns decoracao
        every { repository.contarConflitosBrinquedo(2L, 10L, inicio, fim) } returns 0L
        every { repository.contarConflitosDecoracao(3L, 10L, inicio, fim) } returns 0L

        val result = service.insert(request)

        result shouldBe response.copy(brinquedoIds = listOf(2L), decoracaoIds = listOf(3L))
        verify(exactly = 1) {
            repository.save(match<Reserva> {
                it.id == null && it.cliente == cliente && it.dataInicio == inicio &&
                    it.dataFim == fim && !it.cancelada
            })
        }
        verify(exactly = 1) { brinquedos.findForUpdate(2L) }
        verify(exactly = 1) { decoracoes.findForUpdate(3L) }
    }

    @Test
    fun `insert retorna 400 quando a data final vem antes da inicial`() {
        val request = ReservaRequest(1L, inicio, inicio.minusDays(1))

        val error = shouldThrow<ResponseStatusException> { service.insert(request) }

        error.statusCode shouldBe HttpStatus.BAD_REQUEST
        verify(exactly = 0) { repository.save(any<Reserva>()) }
    }

    @Test
    fun `insert retorna 400 para ID de item invalido`() {
        val request = ReservaRequest(1L, inicio, fim, decoracaoIds = listOf(0L))
        every { clientes.findById(1L) } returns Optional.of(cliente)

        val error = shouldThrow<ResponseStatusException> { service.insert(request) }

        error.statusCode shouldBe HttpStatus.BAD_REQUEST
        verify(exactly = 0) { repository.save(any<Reserva>()) }
    }

    @Test
    fun `adicionarBrinquedo retorna 409 se houver conflito no periodo`() {
        every { repository.findForUpdate(10L) } returns reserva
        every { brinquedos.findForUpdate(2L) } returns brinquedo
        every { repository.contarConflitosBrinquedo(2L, 10L, inicio, fim) } returns 1L

        val error = shouldThrow<ResponseStatusException> { service.adicionarBrinquedo(10L, 2L) }

        error.statusCode shouldBe HttpStatus.CONFLICT
        reserva.brinquedos shouldBe emptySet()
    }

    @Test
    fun `adicionarDecoracao retorna 409 quando a decoracao esta inativa`() {
        decoracao.ativo = false
        every { repository.findForUpdate(10L) } returns reserva
        every { decoracoes.findForUpdate(3L) } returns decoracao

        val error = shouldThrow<ResponseStatusException> { service.adicionarDecoracao(10L, 3L) }

        error.statusCode shouldBe HttpStatus.CONFLICT
        reserva.decoracoes shouldBe emptySet()
    }

    @Test
    fun `removerBrinquedo desvincula o brinquedo da reserva`() {
        reserva.brinquedos.add(brinquedo)
        every { repository.findForUpdate(10L) } returns reserva

        service.removerBrinquedo(10L, 2L)

        reserva.brinquedos shouldBe emptySet()
    }

    @Test
    fun `removerDecoracao desvincula a decoracao da reserva`() {
        reserva.decoracoes.add(decoracao)
        every { repository.findForUpdate(10L) } returns reserva

        service.removerDecoracao(10L, 3L)

        reserva.decoracoes shouldBe emptySet()
    }

    @Test
    fun `cancelar marca a reserva como cancelada`() {
        every { repository.findForUpdate(10L) } returns reserva

        service.cancelar(10L)

        reserva.cancelada shouldBe true
    }

    @Test
    fun `cancelar retorna 409 quando o periodo ja terminou`() {
        val encerrada = oneReserva(11L, cliente, inicio.minusDays(10), inicio.minusDays(7))
        every { repository.findForUpdate(11L) } returns encerrada

        val error = shouldThrow<ResponseStatusException> { service.cancelar(11L) }

        error.statusCode shouldBe HttpStatus.CONFLICT
        encerrada.cancelada shouldBe false
    }
}
