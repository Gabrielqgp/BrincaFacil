package br.pucpr.brincafacil.users.responses

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
