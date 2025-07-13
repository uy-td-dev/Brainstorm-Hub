package dev.uytd.brainstormhub.usermanagement.infrastructure.controller

import dev.uytd.brainstormhub.usermanagement.application.service.UserService
import dev.uytd.brainstormhub.usermanagement.domain.User
import dev.uytd.brainstormhub.usermanagement.infrastructure.model.RegisterRequest
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @PostMapping("/register")
     suspend fun register(@RequestBody user: RegisterRequest): Mono<User>  {
         return userService.register(user.email, user.password, user.fullName)
    }

    @PostMapping("/login")
     suspend fun login(@RequestBody user: RegisterRequest): Mono<String>  {
         return userService.login(user.email, user.password)
    }
}