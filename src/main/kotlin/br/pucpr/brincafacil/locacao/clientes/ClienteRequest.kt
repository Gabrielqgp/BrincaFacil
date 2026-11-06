package br.pucpr.brincafacil.locacao.clientes

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ClienteRequest(
    @field:NotBlank @field:Size(max = 120)
    val nome: String?,
    @field:NotBlank @field:Email @field:Size(max = 254)
    val email: String?,
    @field:Size(max = 30)
    val telefone: String? = null
) {
    fun toCliente() = Cliente(nome = nome!!.trim(), email = email!!.trim(), telefone = telefone)
}