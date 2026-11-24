package br.pucpr.brincafacil.locacao.clientes

import br.pucpr.brincafacil.locacao.oneClient
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClienteControllerTest {
    private val service = mockk<ClienteService>()
    private val controller = ClienteController(service)

    @BeforeEach
    fun setup() {
        clearMocks(service)
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(service)
    }

    @Test
    fun `list deve retornar uma lista vazia quando nao ha clientes`() {
        every { service.list() } returns emptyList()

        val result = controller.list()

        verify(exactly = 1) { service.list() }
        result shouldBe emptyList<ClienteResponse>()
    }

    @Test
    fun `get deve retornar o cliente do ID informado`() {
        val response = ClienteResponse(oneClient(id = 1L))
        every { service.get(1L) } returns response

        val result = controller.get(1L)

        verify(exactly = 1) { service.get(1L) }
        result shouldBe response
    }

    @Test
    fun `insert deve encaminhar o pedido e retornar o cliente cadastrado`() {
        val saved = oneClient(id = 1L)
        val request = ClienteRequest(
            nome = saved.nome,
            email = saved.email,
            telefone = saved.telefone
        )
        val response = ClienteResponse(saved)
        every { service.insert(request) } returns response

        val result = controller.insert(request)

        verify(exactly = 1) { service.insert(request) }
        result shouldBe response
    }
}
