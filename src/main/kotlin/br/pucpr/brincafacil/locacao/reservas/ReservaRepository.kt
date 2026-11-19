package br.pucpr.brincafacil.locacao.reservas

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface ReservaRepository : JpaRepository<Reserva, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.id = :id")
    fun findForUpdate(id: Long): Reserva?

    fun existsByBrinquedos_Id(id: Long): Boolean
    fun existsByDecoracoes_Id(id: Long): Boolean

    @Query("""
        select count(r) from Reserva r join r.brinquedos item
        where item.id = :itemId and r.id <> :reservaId and r.cancelada = false
        and r.dataInicio <= :fim and r.dataFim >= :inicio
    """)
    fun contarConflitosBrinquedo(itemId: Long, reservaId: Long, inicio: LocalDate, fim: LocalDate): Long

    @Query("""
        select count(r) from Reserva r join r.decoracoes item
        where item.id = :itemId and r.id <> :reservaId and r.cancelada = false
        and r.dataInicio <= :fim and r.dataFim >= :inicio
    """)
    fun contarConflitosDecoracao(itemId: Long, reservaId: Long, inicio: LocalDate, fim: LocalDate): Long
}
