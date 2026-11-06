package br.pucpr.brincafacil.users.responses

import br.pucpr.brincafacil.users.User

data class UserResponse(
    var id: Long,
    var email: String,
    var name: String
) {
    constructor(user: User) :
            this(
                id = user.id!!,
                email = user.email,
                name = user.name
            )
}
