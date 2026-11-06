package br.pucpr.brincafacil.users

import br.pucpr.brincafacil.users.requests.LoginRequest
import br.pucpr.brincafacil.users.requests.PatchUserRequest
import br.pucpr.brincafacil.users.requests.UserRequest
import br.pucpr.brincafacil.users.responses.UserResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Validated
class UserController(
    private val userService: UserService
) {
    @SecurityRequirement(name = "jwt")
    @GetMapping
    fun list(
        @RequestParam sortDir: String?,
        @RequestParam role: String?
    ): ResponseEntity<List<UserResponse>> {
        val users = if (role != null) userService.findByRole(role)
        else SortDir.findOrThrow(sortDir ?: "ASC")
            .let { userService.findAll(it) }

        return users
            .map { UserResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name ="jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun insert(
        @Valid @RequestBody user: UserRequest
    ) = userService.insert(user.toUser())
        .let { UserResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    fun update(
        @Min(1) @PathVariable id: Long,
        @Valid @RequestBody user: PatchUserRequest
    ) = userService.update(id, user.name!!)
        ?.let { UserResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.noContent().build()

    @SecurityRequirement(name = "jwt")
    @GetMapping("/{id}")
    fun getById(
        @Min(1) @PathVariable id: Long
    ) = userService.findByIdOrNull(id)
        ?.let { UserResponse(it) }
        ?.let { ResponseEntity.ok(it) }
        ?: ResponseEntity.notFound().build()

    @SecurityRequirement(name = "jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteById(
        @Min(1) @PathVariable id: Long
    ): ResponseEntity<Void> = userService.deleteById(id)
        .let { ResponseEntity.ok().build() }

    @SecurityRequirement(name ="jwt")
    @PutMapping("/{id}/roles/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    fun grant(
        @Min(1) @PathVariable id: Long,
        @PathVariable name: String
    ): ResponseEntity<Void> =
        if (userService.addRole(id, name)) ResponseEntity.ok().build()
        else ResponseEntity.noContent().build()

    @PostMapping("/login")
    fun login(
        @Validated @RequestBody user: LoginRequest
    ) = userService.login(user.email!!, user.password!!)
}