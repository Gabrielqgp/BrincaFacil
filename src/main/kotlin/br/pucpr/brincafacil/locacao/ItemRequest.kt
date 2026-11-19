package br.pucpr.brincafacil.locacao

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ItemRequest(
    @field:NotBlank @field:Size(max = 120)
    val nome: String?,
    @field:Size(max = 500)
    val descricao: String? = null,
    val ativo: Boolean = true
)

data class ItemResponse(val id: Long, val nome: String, val descricao: String?, val ativo: Boolean)
