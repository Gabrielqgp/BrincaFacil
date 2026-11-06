package br.pucpr.brincafacil.locacao

import br.pucpr.brincafacil.locacao.brinquedos.Brinquedo
import br.pucpr.brincafacil.locacao.decoracoes.Decoracao
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ItemRequest(
    @field:NotBlank @field:Size(max = 120)
    val nome: String?,
    @field:Size(max = 500)
    val descricao: String? = null,
    val ativo: Boolean = true
) {
    fun toBrinquedo() = Brinquedo(nome = nome!!.trim(), descricao = descricao, ativo = ativo)

    fun toDecoracao() = Decoracao(nome = nome!!.trim(), descricao = descricao, ativo = ativo)
}

data class ItemResponse(val id: Long, val nome: String, val descricao: String?, val ativo: Boolean) {
    constructor(item: Brinquedo) : this(item.id!!, item.nome, item.descricao, item.ativo)

    constructor(item: Decoracao) : this(item.id!!, item.nome, item.descricao, item.ativo)
}
