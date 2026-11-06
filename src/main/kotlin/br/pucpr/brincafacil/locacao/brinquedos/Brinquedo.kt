package br.pucpr.brincafacil.locacao.brinquedos

import jakarta.persistence.*

@Entity
@Table(name = "brinquedos")
class Brinquedo(
    @Id @GeneratedValue
    val id: Long? = null,
    @Column(nullable = false, length = 120)
    var nome: String,
    @Column(length = 500)
    var descricao: String?,
    @Column(nullable = false)
    var ativo: Boolean = true
)
