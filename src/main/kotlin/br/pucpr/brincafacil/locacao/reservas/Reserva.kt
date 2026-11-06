package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.brinquedos.Brinquedo
import br.pucpr.brincafacil.locacao.clientes.Cliente
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "reservas")
class Reserva(
    @Id @GeneratedValue
    val id: Long? = null,
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    val cliente: Cliente,
    @Column(nullable = false)
    val dataInicio: LocalDate,
    @Column(nullable = false)
    val dataFim: LocalDate,
    @Column(nullable = false)
    var cancelada: Boolean = false,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "reserva_brinquedo",
        joinColumns = [JoinColumn(name = "reserva_id")],
        inverseJoinColumns = [JoinColumn(name = "brinquedo_id")]
    )
    val brinquedos: MutableSet<Brinquedo> = mutableSetOf()
)
