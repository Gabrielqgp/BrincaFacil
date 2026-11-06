package br.pucpr.brincafacil.locacao.reservas

import br.pucpr.brincafacil.locacao.brinquedos.Brinquedo
import br.pucpr.brincafacil.locacao.clientes.Cliente
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
    val brinquedoIds: List<Long> = emptyList()
) {
    fun toReserva(cliente: Cliente, brinquedos: List<Brinquedo>) = Reserva(
        cliente = cliente,
        dataInicio = dataInicio!!,
        dataFim = dataFim!!,
        brinquedos = brinquedos.toMutableSet()
    )
}
