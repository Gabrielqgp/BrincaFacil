package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.brinquedos.Brinquedo
import br.pucpr.brincafacil.locacao.clientes.Cliente
import br.pucpr.brincafacil.locacao.decoracoes.Decoracao
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "reservas")
class Reserva(
    @Id @GeneratedValue
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    val cliente: Cliente,

    @Column(nullable = false)
    val dataInicio: LocalDate,
    @Column(nullable = false)
    val dataFim: LocalDate,
    @Column(nullable = false)
    var cancelada: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "reserva_brinquedo",
        joinColumns = [JoinColumn(name = "reserva_id")],
        inverseJoinColumns = [JoinColumn(name = "brinquedo_id")]
    )
    val brinquedos: MutableSet<Brinquedo> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "reserva_decoracao",
        joinColumns = [JoinColumn(name = "reserva_id")],
        inverseJoinColumns = [JoinColumn(name = "decoracao_id")]
    )
    val decoracoes: MutableSet<Decoracao> = mutableSetOf()
)
