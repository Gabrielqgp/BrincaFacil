package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.brinquedos.Brinquedo
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class ReservaService(private val repository: ReservaRepository) {
    fun findAll() = repository.findAll(Sort.by("dataInicio", "id"))

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun get(id: Long): Reserva = findByIdOrNull(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva $id não encontrada")

    fun insert(reserva: Reserva): Reserva {
        if (reserva.dataFim.isBefore(reserva.dataInicio)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "dataFim deve ser igual ou posterior a dataInicio")
        }
        reserva.brinquedos.forEach { validarDisponibilidade(reserva, it) }
        return repository.save(reserva).also { log.info("Reserva {} criada", it.id) }
    }

    fun addBrinquedo(id: Long, brinquedo: Brinquedo): Reserva {
        val reserva = get(id)
        validarEdicao(reserva)
        if (reserva.brinquedos.any { it.id == brinquedo.id }) return reserva
        validarDisponibilidade(reserva, brinquedo)
        reserva.brinquedos.add(brinquedo)
        return repository.save(reserva).also {
            log.info("Brinquedo {} adicionado à reserva {}", brinquedo.id, id)
        }
    }

    fun removeBrinquedo(id: Long, brinquedoId: Long): Reserva {
        val reserva = get(id)
        validarEdicao(reserva)
        reserva.brinquedos.removeIf { it.id == brinquedoId }
        return repository.save(reserva).also {
            log.info("Brinquedo {} removido da reserva {}", brinquedoId, id)
        }
    }

    fun cancelar(id: Long): Reserva {
        val reserva = get(id)
        if (reserva.cancelada) return reserva
        validarEdicao(reserva)
        reserva.cancelada = true
        return repository.save(reserva).also { log.info("Reserva {} cancelada", id) }
    }

    private fun validarEdicao(reserva: Reserva) {
        if (reserva.cancelada || reserva.dataFim.isBefore(LocalDate.now())) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Reserva cancelada ou já encerrada")
        }
    }

    private fun validarDisponibilidade(reserva: Reserva, brinquedo: Brinquedo) {
        if (!brinquedo.ativo) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Brinquedo ${brinquedo.id} está inativo")
        }
        if (repository.contarConflitos(
                brinquedo.id!!, reserva.id ?: 0L, reserva.dataInicio, reserva.dataFim
            ) > 0
        ) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Brinquedo ${brinquedo.id} já reservado no período")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ReservaService::class.java)
    }
}
