package br.pucpr.brincafacil.locacao.reservas

import java.time.LocalDate

data class ReservaResponse(
    val id: Long,
    val clienteId: Long,
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val cancelada: Boolean,
    val brinquedoIds: List<Long>
) {
    constructor(reserva: Reserva) : this(
        reserva.id!!, reserva.cliente.id!!, reserva.dataInicio, reserva.dataFim,
        reserva.cancelada, reserva.brinquedos.map { it.id!! }.sorted()
    )
}
