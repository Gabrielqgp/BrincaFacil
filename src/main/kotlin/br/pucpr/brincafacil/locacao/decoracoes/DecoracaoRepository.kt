package br.pucpr.brincafacil.locacao.decoracoes

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface DecoracaoRepository : JpaRepository<Decoracao, Long> {
    fun findByNomeContainingIgnoreCase(nome: String, sort: Sort): List<Decoracao>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from Decoracao item where item.id = :id")
    fun findForUpdate(id: Long): Decoracao?
}
