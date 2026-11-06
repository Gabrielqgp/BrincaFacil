package br.pucpr.brincafacil.users.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    @Email
    var email: String?,

    @NotBlank
    var password: String?
)