package dev.uytd.brainstormhub.usermanagement.application.service

import dev.uytd.brainstormhub.usermanagement.domain.User
import reactor.core.publisher.Mono

interface UserService {
    suspend fun register(email:String ,password:String,fullName:String?) : Mono<User>

    suspend fun login(email: String, password: String): Mono<String>

}