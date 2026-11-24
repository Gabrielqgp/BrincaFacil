package br.pucpr.brincafacil.locacao

import br.pucpr.brincafacil.locacao.clientes.Cliente
import br.pucpr.brincafacil.locacao.brinquedos.Brinquedo
import br.pucpr.brincafacil.locacao.decoracoes.Decoracao
import br.pucpr.brincafacil.locacao.reservas.Reserva
import java.time.LocalDate

fun oneClient(
    id: Long? = 1L,
    name: String = "John Doe",
    email: String = "${name.lowercase().replace(" ", ".")}@email.com",
    telefone: String = "4199998888"
) = Cliente(id, name, email, telefone)

fun oneBrinquedo(
    id: Long? = 1L,
    nome: String = "Cama elástica",
    descricao: String? = "Modelo infantil",
    ativo: Boolean = true
) = Brinquedo(id = id, nome = nome, descricao = descricao, ativo = ativo)

fun oneDecoracao(
    id: Long? = 1L,
    nome: String = "Painel de balões",
    descricao: String? = "Tema infantil",
    ativo: Boolean = true
) = Decoracao(id = id, nome = nome, descricao = descricao, ativo = ativo)

fun oneReserva(
    id: Long? = 1L,
    cliente: Cliente = oneClient(),
    dataInicio: LocalDate = LocalDate.now().plusDays(5),
    dataFim: LocalDate = dataInicio.plusDays(2),
    cancelada: Boolean = false,
    brinquedos: Set<Brinquedo> = emptySet(),
    decoracoes: Set<Decoracao> = emptySet()
) = Reserva(
    id = id,
    cliente = cliente,
    dataInicio = dataInicio,
    dataFim = dataFim,
    cancelada = cancelada,
    brinquedos = brinquedos.toMutableSet(),
    decoracoes = decoracoes.toMutableSet()
)