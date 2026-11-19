package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.brinquedos.BrinquedoRepository
import br.pucpr.brincafacil.locacao.clientes.ClienteRepository
import br.pucpr.brincafacil.locacao.conflito
import br.pucpr.brincafacil.locacao.decoracoes.DecoracaoRepository
import br.pucpr.brincafacil.locacao.naoEncontrado
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class ReservaService(
    private val repository: ReservaRepository,
    private val clientes: ClienteRepository,
    private val brinquedos: BrinquedoRepository,
    private val decoracoes: DecoracaoRepository
) {
    fun list() = repository.findAll(Sort.by("dataInicio", "id")).map(::ReservaResponse)
    fun get(id: Long) = ReservaResponse(repository.findByIdOrNull(id) ?: naoEncontrado("Reserva $id"))

    @Transactional
    fun insert(request: ReservaRequest): ReservaResponse {
        val inicio = request.dataInicio!!
        val fim = request.dataFim!!
        if (fim.isBefore(inicio)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "dataFim deve ser igual ou posterior a dataInicio.")
        }
        val cliente = clientes.findByIdOrNull(request.clienteId!!) ?: naoEncontrado("Cliente ${request.clienteId}")
        if (request.brinquedoIds.any { it < 1 } || request.decoracaoIds.any { it < 1 }) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Os IDs dos itens devem ser positivos.")
        }
        val reserva = repository.save(Reserva(cliente = cliente, dataInicio = inicio, dataFim = fim))
        request.brinquedoIds.distinct().sorted().forEach { adicionarBrinquedo(reserva.id!!, it) }
        request.decoracaoIds.distinct().sorted().forEach { adicionarDecoracao(reserva.id!!, it) }
        log.info("Reserva {} criada", reserva.id)
        return ReservaResponse(reserva)
    }

    @Transactional
    fun adicionarBrinquedo(id: Long, itemId: Long) {
        val reserva = editar(id)
        // O bloqueio do item serializa tentativas de reservar a mesma unidade.
        val item = brinquedos.findForUpdate(itemId) ?: naoEncontrado("Brinquedo $itemId")
        if (item in reserva.brinquedos) return
        if (!item.ativo) conflito("Brinquedo $itemId está inativo.")
        if (repository.contarConflitosBrinquedo(itemId, id, reserva.dataInicio, reserva.dataFim) > 0) {
            log.info("Brinquedo {} indisponível para a reserva {}", itemId, id)
            conflito("Brinquedo $itemId já está reservado no período.")
        }
        reserva.brinquedos.add(item)
        log.info("Brinquedo {} adicionado à reserva {}", itemId, id)
    }

    @Transactional
    fun adicionarDecoracao(id: Long, itemId: Long) {
        val reserva = editar(id)
        val item = decoracoes.findForUpdate(itemId) ?: naoEncontrado("Decoração $itemId")
        if (item in reserva.decoracoes) return
        if (!item.ativo) conflito("Decoração $itemId está inativa.")
        if (repository.contarConflitosDecoracao(itemId, id, reserva.dataInicio, reserva.dataFim) > 0) {
            log.info("Decoração {} indisponível para a reserva {}", itemId, id)
            conflito("Decoração $itemId já está reservada no período.")
        }
        reserva.decoracoes.add(item)
        log.info("Decoração {} adicionada à reserva {}", itemId, id)
    }

    @Transactional
    fun removerBrinquedo(id: Long, itemId: Long) {
        val reserva = editar(id)
        if (reserva.brinquedos.removeIf { it.id == itemId }) {
            log.info("Brinquedo {} removido da reserva {}", itemId, id)
        }
    }

    @Transactional
    fun removerDecoracao(id: Long, itemId: Long) {
        val reserva = editar(id)
        if (reserva.decoracoes.removeIf { it.id == itemId }) {
            log.info("Decoração {} removida da reserva {}", itemId, id)
        }
    }

    @Transactional
    fun cancelar(id: Long) {
        val reserva = repository.findForUpdate(id) ?: naoEncontrado("Reserva $id")
        if (reserva.cancelada) return
        if (reserva.dataFim.isBefore(LocalDate.now())) conflito("O período da reserva já terminou.")
        reserva.cancelada = true
        log.info("Reserva {} cancelada", id)
    }

    private fun editar(id: Long): Reserva {
        val reserva = repository.findForUpdate(id) ?: naoEncontrado("Reserva $id")
        if (reserva.cancelada) conflito("A reserva está cancelada.")
        if (reserva.dataFim.isBefore(LocalDate.now())) conflito("O período da reserva já terminou.")
        return reserva
    }

    companion object {
        private val log = LoggerFactory.getLogger(ReservaService::class.java)
    }
}
