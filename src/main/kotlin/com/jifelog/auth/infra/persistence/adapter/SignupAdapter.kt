package com.jifelog.auth.infra.persistence.adapter

import com.jifelog.auth.application.port.SignupCommandPort
import com.jifelog.auth.application.port.SignupQueryPort
import com.jifelog.auth.domain.User
import com.jifelog.auth.infra.persistence.mapper.UserMapper
import com.jifelog.auth.infra.persistence.mapper.UserPasswordMapper
import com.jifelog.auth.infra.persistence.repository.UserJpaRepository
import com.jifelog.auth.infra.persistence.repository.UserPasswordJpsRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SignupAdapter(
    private val userJapRepository: UserJpaRepository,
    private val userPasswordJpsRepository: UserPasswordJpsRepository
) : SignupQueryPort, SignupCommandPort {
    override fun loadUser(id: UUID): User {
        val userEntity = userJapRepository.findById(id)
            .orElseThrow { EntityNotFoundException("No user exists for id $id") }

        val passwordEntity = userPasswordJpsRepository.findById(id)
            .orElseThrow { EntityNotFoundException("No password exists for user id $id") }

        return UserMapper.toDomain(userEntity, passwordEntity)
    }

    override fun saveUser(user: User): User {
        val userEntity = userJapRepository.save(
            UserMapper.toEntity(user)
        )

        val passwordEntity = userPasswordJpsRepository.save(
            UserPasswordMapper.toEntity(
                userEntity.id!!,
                user.userPassword
            )
        )

        return UserMapper.toDomain(userEntity, passwordEntity)
    }
}