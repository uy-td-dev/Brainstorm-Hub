package dev.uytd.brainstormhub.usermanagement.domain

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("users")
data class User(
    @Id val userId: UUID = UUID.randomUUID(),
    val email: String,
    val passwordHash: String,
    val fullName: String?,
    val createdAt: Instant = Instant.now(),
): Persistable<UUID> {

    @Transient
    private var isNewUser: Boolean = true

    override fun getId(): UUID? = userId

    override fun isNew(): Boolean = this.isNewUser || userId == null


}
