package br.pucpr.brincafacil.locacao.clientes

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.repository.JpaRepository

@Entity
@Table(name = "clientes")
class Cliente(
    @Id @GeneratedValue
    val id: Long? = null,
    @Column(nullable = false, length = 120)
    val nome: String,
    @Column(nullable = false, length = 254)
    val email: String,
    @Column(length = 30)
    val telefone: String?
)



