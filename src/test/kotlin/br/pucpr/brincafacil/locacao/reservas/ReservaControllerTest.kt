package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.oneClient
import br.pucpr.brincafacil.locacao.oneReserva
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
import java.time.LocalDate

class ReservaControllerTest {
    private val service = mockk<ReservaService>()
    private val controller = ReservaController(service)

    private val cliente = oneClient(id = 1L)
    private val inicio = LocalDate.of(2030, 6, 10)
    private val fim = inicio.plusDays(2)
    private val reserva = oneReserva(id = 10L, cliente = cliente, dataInicio = inicio, dataFim = fim)
    private val response = ReservaResponse(reserva)

    @BeforeEach
    fun setup() {
        clearMocks(service)
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(service)
    }

    @Test
    fun `list deve retornar a lista de reservas`() {
        every { service.list() } returns listOf(response)

        val result = controller.list()

        verify(exactly = 1) { service.list() }
        result shouldBe listOf(response)
    }

    @Test
    fun `get deve retornar a reserva do ID informado`() {
        every { service.get(10L) } returns response

        val result = controller.get(10L)

        verify(exactly = 1) { service.get(10L) }
        result shouldBe response
    }

    @Test
    fun `insert deve encaminhar o pedido e retornar a reserva criada`() {
        val request = ReservaRequest(
            clienteId = cliente.id,
            dataInicio = inicio,
            dataFim = fim,
            brinquedoIds = listOf(2L),
            decoracaoIds = listOf(3L)
        )
        val created = response.copy(brinquedoIds = listOf(2L), decoracaoIds = listOf(3L))
        every { service.insert(request) } returns created

        val result = controller.insert(request)

        verify(exactly = 1) { service.insert(request) }
        result shouldBe created
    }

    @Test
    fun `adicionarBrinquedo deve encaminhar os IDs`() {
        every { service.adicionarBrinquedo(10L, 2L) } just Runs

        controller.adicionarBrinquedo(10L, 2L)

        verify(exactly = 1) { service.adicionarBrinquedo(10L, 2L) }
    }

    @Test
    fun `adicionarDecoracao deve encaminhar os IDs`() {
        every { service.adicionarDecoracao(10L, 3L) } just Runs

        controller.adicionarDecoracao(10L, 3L)

        verify(exactly = 1) { service.adicionarDecoracao(10L, 3L) }
    }

    @Test
    fun `removerBrinquedo deve encaminhar os IDs`() {
        every { service.removerBrinquedo(10L, 2L) } just Runs

        controller.removerBrinquedo(10L, 2L)

        verify(exactly = 1) { service.removerBrinquedo(10L, 2L) }
    }

    @Test
    fun `removerDecoracao deve encaminhar os IDs`() {
        every { service.removerDecoracao(10L, 3L) } just Runs

        controller.removerDecoracao(10L, 3L)

        verify(exactly = 1) { service.removerDecoracao(10L, 3L) }
    }

    @Test
    fun `cancelar deve encaminhar o ID da reserva`() {
        every { service.cancelar(10L) } just Runs

        controller.cancelar(10L)

        verify(exactly = 1) { service.cancelar(10L) }
    }
}
