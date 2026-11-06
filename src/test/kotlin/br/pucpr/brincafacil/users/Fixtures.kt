package br.pucpr.brincafacil.users

import br.pucpr.brincafacil.roles.Role

fun oneUser(
    id: Long? = 1L,
    name: String = "John Doe",
    email: String = "${name.lowercase().replace(" ", ".")}@email.com",
    password: String = "password",
    roles: MutableSet<Role> = mutableSetOf()
) = User(id, name, email, password, roles)
