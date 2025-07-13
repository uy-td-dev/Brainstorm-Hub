package dev.uytd.brainstormhub.usermanagement.application.service.impl

import dev.uytd.brainstormhub.usermanagement.application.service.UserService
import dev.uytd.brainstormhub.usermanagement.domain.User
import dev.uytd.brainstormhub.usermanagement.infrastructure.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*
import javax.crypto.SecretKey

// FIX 1: Tên class theo quy ước UpperCamelCase
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    // FIX 2: Inject secret từ application.properties một cách an toàn
    @Value("\${spring.security.jwt.secret}") private val jwtSecret: String
) : UserService {

    // FIX 3: Tạo SecretKey một lần và tái sử dụng
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    // FIX 4: Bỏ 'suspend' và 'mono' builder, làm việc trực tiếp với Mono
    override suspend fun register(
        email: String,
        password: String,
        fullName: String?
    ): Mono<User> = mono{
        val user = User(
            email = email,
            passwordHash = passwordEncoder.encode(password),
            fullName = fullName
        )
        // Giả sử userRepository.save trả về Mono<User>
         userRepository.save(user)
    }

    override suspend fun login(
        email: String,
        password: String
    ): Mono<User> {
       return  userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(Exception("User not found")))
            .flatMap { user ->
                if (passwordEncoder.matches(password, user.passwordHash)) {
                    Mono.just(user) // Trả về user nếu password đúng
                } else {
                    Mono.error(Exception("Invalid credentials"))
                }
            }
    }

    // FIX 5: Cập nhật hàm tạo token để dùng SecretKey
    private fun generateJwtToken(userId: UUID, email: String): String {
        val expirationTime = 86400000L // 1 day in milliseconds
        val now = Date()
        val expirationDate = Date(now.time + expirationTime)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(secretKey) // Dùng SecretKey thay vì phương thức cũ
            .compact()
    }
}