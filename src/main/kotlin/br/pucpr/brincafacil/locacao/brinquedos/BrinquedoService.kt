package br.pucpr.brincafacil.locacao.brinquedos

import br.pucpr.brincafacil.locacao.naoEncontrado
import br.pucpr.brincafacil.locacao.ordenarPorNome
import br.pucpr.brincafacil.locacao.reservas.ReservaRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BrinquedoService(
    private val repository: BrinquedoRepository,
    private val reservaRepository: ReservaRepository
) {
    fun list(nome: String?, sortDir: String) = repository
        .findByNomeContainingIgnoreCase(nome.orEmpty().trim(), ordenarPorNome(sortDir))

    fun get(id: Long): Brinquedo =
        repository.findByIdOrNull(id) ?: naoEncontrado("Brinquedo $id")

    fun insert(item: Brinquedo): Brinquedo =
        repository
            .save(item)
            .also { log.info("Brinquedo {} cadastrado", it.id) }

    fun update(id: Long, dados: Brinquedo): Brinquedo {
        val item = get(id)
        item.nome = dados.nome
        item.descricao = dados.descricao
        item.ativo = dados.ativo
        return repository.save(item).also { log.info("Brinquedo {} atualizado", id) }
    }

    fun delete(id: Long) {
        val item = get(id)
        if (reservaRepository.existsByBrinquedos_Id(id)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Brinquedo vinculado a uma reserva. Desative-o para manter o histórico.")
        }
        repository.delete(item)
        log.info("Brinquedo {} excluído", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(BrinquedoService::class.java)
    }
}
