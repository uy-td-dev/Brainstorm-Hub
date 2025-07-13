package dev.uytd.brainstormhub.usermanagement.infrastructure.repository

import dev.uytd.brainstormhub.usermanagement.domain.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository: CoroutineCrudRepository<User, UUID> {
     fun findByEmail(email: String): Mono<User>
}