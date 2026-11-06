package br.pucpr.brincafacil.locacao.decoracoes

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DecoracaoRepository : JpaRepository<Decoracao, Long> {
    fun findByNomeContainingIgnoreCase(nome: String, sort: Sort): List<Decoracao>
}
