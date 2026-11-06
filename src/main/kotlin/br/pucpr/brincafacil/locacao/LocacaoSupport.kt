package br.pucpr.brincafacil.locacao

import br.pucpr.brincafacil.users.SortDir
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal fun naoEncontrado(recurso: String): Nothing =
    throw ResponseStatusException(HttpStatus.NOT_FOUND, "$recurso não encontrado.")

internal fun ordenarPorNome(sortDir: String): Sort =
    Sort.by(Sort.Direction.valueOf(SortDir.findOrThrow(sortDir).name), "nome").and(Sort.by("id"))
