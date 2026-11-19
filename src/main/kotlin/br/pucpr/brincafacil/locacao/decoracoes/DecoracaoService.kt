package br.pucpr.brincafacil.locacao.decoracoes

import br.pucpr.brincafacil.locacao.*
import br.pucpr.brincafacil.locacao.reservas.ReservaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DecoracaoService(
    private val repository: DecoracaoRepository,
    private val reservas: ReservaRepository
) {
    fun list(nome: String?, sortDir: String) = repository
        .findByNomeContainingIgnoreCase(nome.orEmpty().trim(), ordenarPorNome(sortDir))
        .map { ItemResponse(it.id!!, it.nome, it.descricao, it.ativo) }

    fun get(id: Long) = (repository.findByIdOrNull(id) ?: naoEncontrado("Decoracao $id"))
        .let { ItemResponse(it.id!!, it.nome, it.descricao, it.ativo) }

    @Transactional
    fun insert(request: ItemRequest): ItemResponse {
        val item = repository.save(Decoracao(nome = request.nome!!.trim(), descricao = request.descricao, ativo = request.ativo))
        log.info("Decoracao {} cadastrado", item.id)
        return ItemResponse(item.id!!, item.nome, item.descricao, item.ativo)
    }

    @Transactional
    fun update(id: Long, request: ItemRequest): ItemResponse {
        val item = repository.findForUpdate(id) ?: naoEncontrado("Decoracao $id")
        item.nome = request.nome!!.trim()
        item.descricao = request.descricao
        item.ativo = request.ativo
        log.info("Decoracao {} atualizado", id)
        return ItemResponse(id, item.nome, item.descricao, item.ativo)
    }

    @Transactional
    fun delete(id: Long) {
        val item = repository.findForUpdate(id) ?: naoEncontrado("Decoracao $id")
        if (reservas.existsByDecoracoes_Id(id)) conflito("Item vinculado a uma reserva. Desative-o para preservar o histórico.")
        repository.delete(item)
        log.info("Decoracao {} excluído", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DecoracaoService::class.java)
    }
}
