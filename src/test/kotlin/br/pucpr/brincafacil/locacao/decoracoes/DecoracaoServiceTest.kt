package br.pucpr.brincafacil.locacao.decoracoes

import br.pucpr.brincafacil.locacao.ItemRequest
import br.pucpr.brincafacil.locacao.ItemResponse
import br.pucpr.brincafacil.locacao.oneDecoracao
import br.pucpr.brincafacil.locacao.reservas.ReservaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.checkUnnecessaryStub
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

class DecoracaoServiceTest {
    private val repository = mockk<DecoracaoRepository>()
    private val reservas = mockk<ReservaRepository>()
    private val service = DecoracaoService(repository, reservas)
    private val item = oneDecoracao(id = 1L)
    private val response = ItemResponse(1L, "Painel de balões", "Tema infantil", true)

    @BeforeEach
    fun setup() {
        clearMocks(repository, reservas)
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(repository, reservas)
    }

    @Test
    fun `list limpa o filtro e ordena por nome e ID`() {
        val sort = Sort.by(Sort.Order.desc("nome"), Sort.Order.asc("id"))
        every { repository.findByNomeContainingIgnoreCase("Painel de balões", sort) } returns listOf(item)

        service.list("  Painel de balões  ", "DESC") shouldBe listOf(response)

        verify(exactly = 1) { repository.findByNomeContainingIgnoreCase("Painel de balões", sort) }
    }

    @Test
    fun `get retorna a resposta com os dados do item`() {
        every { repository.findById(1L) } returns Optional.of(item)

        service.get(1L) shouldBe response
    }

    @Test
    fun `get retorna 404 quando o item nao existe`() {
        every { repository.findById(2L) } returns Optional.empty()

        val error = shouldThrow<ResponseStatusException> { service.get(2L) }

        error.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    @Test
    fun `insert salva a entidade com o nome limpo e retorna a resposta`() {
        val request = ItemRequest("  Painel de balões  ", item.descricao, item.ativo)
        every { repository.save(any<Decoracao>()) } returns item

        val result = service.insert(request)

        verify(exactly = 1) {
            repository.save(match<Decoracao> {
                it.id == null && it.nome == item.nome &&
                    it.descricao == item.descricao && it.ativo == item.ativo
            })
        }
        result shouldBe response
    }

    @Test
    fun `update altera nome descricao e situacao da entidade`() {
        val request = ItemRequest("  Nome editado  ", "Descrição nova", false)
        every { repository.findForUpdate(1L) } returns item

        val result = service.update(1L, request)

        item.nome shouldBe "Nome editado"
        item.descricao shouldBe "Descrição nova"
        item.ativo shouldBe false
        result shouldBe ItemResponse(1L, "Nome editado", "Descrição nova", false)
    }

    @Test
    fun `delete exclui item sem vinculo com reservas`() {
        every { repository.findForUpdate(1L) } returns item
        every { reservas.existsByDecoracoes_Id(1L) } returns false
        every { repository.delete(item) } just Runs

        service.delete(1L)

        verify(exactly = 1) { repository.delete(item) }
    }

    @Test
    fun `delete retorna 409 e preserva item vinculado a reserva`() {
        every { repository.findForUpdate(1L) } returns item
        every { reservas.existsByDecoracoes_Id(1L) } returns true

        val error = shouldThrow<ResponseStatusException> { service.delete(1L) }

        error.statusCode shouldBe HttpStatus.CONFLICT
        verify(exactly = 0) { repository.delete(any()) }
    }
}
