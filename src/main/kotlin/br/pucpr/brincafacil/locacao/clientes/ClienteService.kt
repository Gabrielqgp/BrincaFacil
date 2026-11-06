package br.pucpr.brincafacil.locacao.clientes

import br.pucpr.brincafacil.locacao.naoEncontrado
import br.pucpr.brincafacil.locacao.ordenarPorNome
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ClienteService(private val repository: ClienteRepository) {
    fun list() = repository.findAll(ordenarPorNome("ASC"))

    fun get(id: Long): Cliente =
        repository.findByIdOrNull(id) ?: naoEncontrado("Cliente $id")

    fun insert(cliente: Cliente): Cliente =
        repository.save(cliente).also { log.info("Cliente {} cadastrado", it.id) }

    companion object {
        private val log = LoggerFactory.getLogger(ClienteService::class.java)
    }
}
