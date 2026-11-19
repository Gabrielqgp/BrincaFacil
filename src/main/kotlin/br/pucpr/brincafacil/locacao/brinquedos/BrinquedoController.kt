package br.pucpr.brincafacil.locacao.brinquedos

import br.pucpr.brincafacil.locacao.ItemRequest
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/brinquedos")
@Validated
class BrinquedoController(private val service: BrinquedoService) {
    @GetMapping
    fun list(@RequestParam nome: String?, @RequestParam(defaultValue = "ASC") sortDir: String) =
        service.list(nome, sortDir)

    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @GetMapping("/{id}")
    fun get(@PathVariable @Min(1) id: Long) = service.get(id)

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "jwt")
    fun insert(@Valid @RequestBody request: ItemRequest) = service.insert(request)

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @PutMapping("/{id}")
    @SecurityRequirement(name = "jwt")
    fun update(@PathVariable @Min(1) id: Long, @Valid @RequestBody request: ItemRequest) =
        service.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun delete(@PathVariable @Min(1) id: Long) = service.delete(id)
}
