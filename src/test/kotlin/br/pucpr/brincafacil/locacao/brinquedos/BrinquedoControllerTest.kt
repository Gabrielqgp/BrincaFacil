package br.pucpr.brincafacil.locacao.brinquedos

import br.pucpr.brincafacil.locacao.ItemRequest
import br.pucpr.brincafacil.locacao.ItemResponse
import br.pucpr.brincafacil.locacao.oneBrinquedo
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

class BrinquedoControllerTest {
    private val service = mockk<BrinquedoService>()
    private val controller = BrinquedoController(service)
    private val item = oneBrinquedo(id = 1L)
    private val request = ItemRequest(nome = item.nome, descricao = item.descricao, ativo = item.ativo)
    private val response = ItemResponse(1L, item.nome, item.descricao, item.ativo)

    @BeforeEach
    fun setup() {
        clearMocks(service)
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(service)
    }

    @Test
    fun `list deve retornar uma lista vazia`() {
        every { service.list(null, "ASC") } returns emptyList()

        val result = controller.list(null, "ASC")

        verify(exactly = 1) { service.list(null, "ASC") }
        result shouldBe emptyList<ItemResponse>()
    }

    @Test
    fun `list deve encaminhar o filtro e a ordenacao`() {
        every { service.list(response.nome, "DESC") } returns listOf(response)

        val result = controller.list(response.nome, "DESC")

        verify(exactly = 1) { service.list(response.nome, "DESC") }
        result shouldBe listOf(response)
    }

    @Test
    fun `get deve retornar o item do ID informado`() {
        every { service.get(1L) } returns response

        val result = controller.get(1L)

        verify(exactly = 1) { service.get(1L) }
        result shouldBe response
    }

    @Test
    fun `insert deve encaminhar o pedido e retornar o item criado`() {
        every { service.insert(request) } returns response

        val result = controller.insert(request)

        verify(exactly = 1) { service.insert(request) }
        result shouldBe response
    }

    @Test
    fun `update deve encaminhar o ID e o pedido e retornar o item atualizado`() {
        val updateRequest = request.copy(ativo = false)
        val updated = response.copy(ativo = false)
        every { service.update(1L, updateRequest) } returns updated

        val result = controller.update(1L, updateRequest)

        verify(exactly = 1) { service.update(1L, updateRequest) }
        result shouldBe updated
    }

    @Test
    fun `delete deve encaminhar o ID ao service`() {
        every { service.delete(1L) } just Runs

        controller.delete(1L)

        verify(exactly = 1) { service.delete(1L) }
    }
}
