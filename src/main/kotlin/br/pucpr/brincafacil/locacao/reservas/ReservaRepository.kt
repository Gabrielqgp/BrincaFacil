package br.pucpr.brincafacil.locacao.reservas

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface ReservaRepository : JpaRepository<Reserva, Long> {
    fun existsByBrinquedos_Id(id: Long): Boolean

    @Query("""
        select count(r) from Reserva r join r.brinquedos b
        where b.id = :brinquedoId and r.id <> :reservaId and r.cancelada = false
        and r.dataInicio <= :fim and r.dataFim >= :inicio
    """)
    fun contarConflitos(brinquedoId: Long, reservaId: Long, inicio: LocalDate, fim: LocalDate): Long
}
