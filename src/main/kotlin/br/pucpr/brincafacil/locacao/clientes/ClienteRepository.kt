package br.pucpr.brincafacil.locacao.clientes

import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<Cliente, Long> {
    fun findByEmail(email: String) : Cliente?
}

