package br.pucpr.brincafacil.locacao.reservas

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ReservaRequest(
    @field:NotNull @field:Min(1)
    val clienteId: Long?,
    @field:NotNull @field:FutureOrPresent
    val dataInicio: LocalDate?,
    @field:NotNull
    val dataFim: LocalDate?,
    val brinquedoIds: List<Long> = emptyList(),
    val decoracaoIds: List<Long> = emptyList()
)

data class ReservaResponse(
    val id: Long,
    val clienteId: Long,
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val cancelada: Boolean,
    val brinquedoIds: List<Long>,
    val decoracaoIds: List<Long>
) {
    constructor(reserva: Reserva) : this(
        reserva.id!!, reserva.cliente.id!!, reserva.dataInicio, reserva.dataFim, reserva.cancelada,
        reserva.brinquedos.map { it.id!! }.sorted(),
        reserva.decoracoes.map { it.id!! }.sorted()
    )
}
