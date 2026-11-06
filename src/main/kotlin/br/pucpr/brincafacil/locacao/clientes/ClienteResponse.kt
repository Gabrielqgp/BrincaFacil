package br.pucpr.brincafacil.locacao.clientes

data class ClienteResponse(val id: Long, val nome: String, val email: String, val telefone: String?) {
    constructor(cliente: Cliente) : this(cliente.id!!, cliente.nome, cliente.email, cliente.telefone)
}