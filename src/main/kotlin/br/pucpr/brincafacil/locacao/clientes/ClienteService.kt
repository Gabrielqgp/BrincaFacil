package br.pucpr.brincafacil.locacao.clientes

import br.pucpr.brincafacil.locacao.naoEncontrado
import br.pucpr.brincafacil.locacao.ordenarPorNome
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClienteService(private val repository: ClienteRepository) {
    fun list() = repository.findAll(ordenarPorNome("ASC")).map(::ClienteResponse)
    fun get(id: Long) = ClienteResponse(repository.findByIdOrNull(id) ?: naoEncontrado("Cliente $id"))

    @Transactional
    fun insert(request: ClienteRequest): ClienteResponse {
        val cliente = repository.save(
            Cliente(nome = request.nome!!.trim(), email = request.email!!.trim(), telefone = request.telefone)
        )
        log.info("Cliente {} cadastrado", cliente.id)
        return ClienteResponse(cliente)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ClienteService::class.java)
    }
}
