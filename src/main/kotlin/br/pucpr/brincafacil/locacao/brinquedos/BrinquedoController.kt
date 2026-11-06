package br.pucpr.brincafacil.locacao.brinquedos

import br.pucpr.brincafacil.locacao.ItemRequest
import br.pucpr.brincafacil.locacao.ItemResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/brinquedos")
@Validated
class BrinquedoController(private val service: BrinquedoService) {
    @SecurityRequirement(name = "jwt")
    @GetMapping
    fun list(@RequestParam nome: String?, @RequestParam(defaultValue = "ASC") sortDir: String) =
        service.list(nome, sortDir)
            .map { ItemResponse(it) }
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    fun get(@PathVariable @Min(1) id: Long) = service.get(id)
        .let { ItemResponse(it) }
        .let { ResponseEntity.ok(it) }

    @PostMapping
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasRole('ADMIN')")
    fun insert(@Valid @RequestBody request: ItemRequest) = service.insert(request.toBrinquedo())
        .let { ItemResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable @Min(1) id: Long, @Valid @RequestBody request: ItemRequest) =
        service.update(id, request.toBrinquedo())
            .let { ItemResponse(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable @Min(1) id: Long): ResponseEntity<Void> =
        service.delete(id).let { ResponseEntity.noContent().build() }
}
