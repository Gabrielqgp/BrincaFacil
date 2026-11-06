package br.pucpr.brincafacil.locacao.decoracoes

import br.pucpr.brincafacil.locacao.naoEncontrado
import br.pucpr.brincafacil.locacao.ordenarPorNome
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DecoracaoService(
    private val repository: DecoracaoRepository
) {
    fun list(nome: String?, sortDir: String) = repository
        .findByNomeContainingIgnoreCase(nome.orEmpty().trim(), ordenarPorNome(sortDir))

    fun get(id: Long): Decoracao =
        repository.findByIdOrNull(id) ?: naoEncontrado("Decoracao $id")

    fun insert(item: Decoracao): Decoracao =
        repository.save(item).also { log.info("Decoracao {} cadastrado", it.id) }

    fun update(id: Long, dados: Decoracao): Decoracao {
        val item = get(id)
        item.nome = dados.nome
        item.descricao = dados.descricao
        item.ativo = dados.ativo
        return repository.save(item).also { log.info("Decoracao {} atualizado", id) }
    }

    fun delete(id: Long) {
        val item = get(id)
        repository.delete(item)
        log.info("Decoracao {} excluído", id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DecoracaoService::class.java)
    }
}
