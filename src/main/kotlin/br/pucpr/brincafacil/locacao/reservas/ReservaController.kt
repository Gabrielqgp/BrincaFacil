package br.pucpr.brincafacil.locacao.reservas

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reservas")
@SecurityRequirement(name = "jwt")
@Validated
class ReservaController(private val service: ReservaService) {
    @GetMapping
    fun list() = service.list()

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @GetMapping("/{id}")
    fun get(@PathVariable @Min(1) id: Long) = service.get(id)

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun insert(@Valid @RequestBody request: ReservaRequest) = service.insert(request)

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @PutMapping("/{id}/brinquedos/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun adicionarBrinquedo(@PathVariable @Min(1) id: Long, @PathVariable @Min(1) itemId: Long) =
        service.adicionarBrinquedo(id, itemId)

    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    @PutMapping("/{id}/decoracoes/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun adicionarDecoracao(@PathVariable @Min(1) id: Long, @PathVariable @Min(1) itemId: Long) =
        service.adicionarDecoracao(id, itemId)

    @DeleteMapping("/{id}/brinquedos/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun removerBrinquedo(@PathVariable @Min(1) id: Long, @PathVariable @Min(1) itemId: Long) =
        service.removerBrinquedo(id, itemId)

    @DeleteMapping("/{id}/decoracoes/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun removerDecoracao(@PathVariable @Min(1) id: Long, @PathVariable @Min(1) itemId: Long) =
        service.removerDecoracao(id, itemId)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'PREMIUM')")
    fun cancelar(@PathVariable @Min(1) id: Long) = service.cancelar(id)
}
