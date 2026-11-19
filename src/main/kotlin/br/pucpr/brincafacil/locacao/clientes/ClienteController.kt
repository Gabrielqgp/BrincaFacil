package br.pucpr.brincafacil.locacao.clientes

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
@SecurityRequirement(name = "jwt")
@Validated
class ClienteController(private val service: ClienteService) {
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @GetMapping
    fun list() = service.list()

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @GetMapping("/{id}")
    fun get(@PathVariable @Min(1) id: Long) = service.get(id)

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun insert(@Valid @RequestBody request: ClienteRequest) = service.insert(request)
}
