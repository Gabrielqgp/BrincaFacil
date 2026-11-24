package br.pucpr.brincafacil.locacao.cliente

import br.pucpr.brincafacil.locacao.clientes.Cliente
import br.pucpr.brincafacil.locacao.clientes.ClienteRepository
import br.pucpr.brincafacil.locacao.clientes.ClienteRequest
import br.pucpr.brincafacil.locacao.clientes.ClienteResponse
import br.pucpr.brincafacil.locacao.clientes.ClienteService
import br.pucpr.brincafacil.locacao.oneClient
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClientServiceTest {
    private val repository = mockk<ClienteRepository>()
    private val service = ClienteService(repository)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        checkUnnecessaryStub(repository)
    }

    @Test
    fun `insert deve salvar o cliente e retornar a resposta`() {
        val saved = oneClient(id = 1L)
        val request = ClienteRequest(
            nome = "  ${saved.nome}  ",
            email = saved.email,
            telefone = saved.telefone
        )

        every { repository.save(any<Cliente>()) } returns saved

        val result = service.insert(request)

        verify(exactly = 1) {
            repository.save(match<Cliente> {
                it.id == null &&
                        it.nome == saved.nome &&
                        it.email == saved.email &&
                        it.telefone == saved.telefone
            })
        }

        result shouldBe ClienteResponse(saved)
    }
}