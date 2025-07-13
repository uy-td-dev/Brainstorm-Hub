package dev.uytd.brainstormhub.config


import io.jsonwebtoken.security.Keys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import javax.crypto.SecretKey

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtProperties: JwtProperties // Injecct properties class thay vì String
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            // Vô hiệu hóa CSRF vì dùng JWT
            csrf { disable() }

            // Cấu hình các endpoint được phép truy cập
            authorizeExchange {
                authorize ("/api/users/register", permitAll)
                authorize("/api/users/login", permitAll)
                authorize(anyExchange, authenticated)
            }

            // Kích hoạt xử lý JWT cho resource server
            oauth2ResourceServer {
                jwt {
                    // Cung cấp bean để giải mã và xác thực token
                    jwtDecoder()
                }
            }
        }
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        // Tạo SecretKey từ chuỗi secret trong properties
        val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

        // Dùng NimbusReactiveJwtDecoder để giải mã token
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build()
    }
}