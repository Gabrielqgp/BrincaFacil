package br.pucpr.brincafacil.users.requests

import jakarta.validation.constraints.NotBlank

data class PatchUserRequest(
    @NotBlank
    var name: String?
)
