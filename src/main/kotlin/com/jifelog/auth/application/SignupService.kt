package com.jifelog.auth.application

import com.jifelog.auth.application.command.ConfirmEmailVerificationCommand
import com.jifelog.auth.application.command.RegisterUserCommand
import com.jifelog.auth.application.command.RequestEmailVerificationCommand
import com.jifelog.auth.application.port.SignupCommandPort
import com.jifelog.auth.application.port.SignupQueryPort
import com.jifelog.auth.common.HashUtils
import com.jifelog.auth.common.TokenUtils
import com.jifelog.auth.domain.PasswordAlgoType
import com.jifelog.auth.domain.User
import com.jifelog.auth.domain.UserPassword
import com.jifelog.auth.infra.redis.adapter.TokenAdapter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class SignupService(
    private val signupQueryPort : SignupQueryPort,
    private val signupCommandPort: SignupCommandPort,
    private val tokenAdapter: TokenAdapter,
    private val passwordHasher: PasswordHasher
) {
    fun registerUser(
        registerUserCommand: RegisterUserCommand
    ): User {
        val userPassword = UserPassword.withoutId(
            passwordHasher.encode(registerUserCommand.password),
            PasswordAlgoType.ARGON2ID
        )

        val user = User.withoutId(
            registerUserCommand.username,
            registerUserCommand.email,
            userPassword
        )

        return signupCommandPort.saveUser(user)
    }

    fun requestEmailVerification(
        requestEmailVerificationCommand: RequestEmailVerificationCommand
    ) {
        // 토큰 생성
        val token = TokenUtils.generateToken()
        val hashedToken = HashUtils.sha256Hex(token)
        // 토근 저장
        tokenAdapter.saveEmailVerificationToken(
            requestEmailVerificationCommand.email,
            hashedToken,
            60 * 10
        )
        // 메일 발송
    }

    fun confirmEmailVerification(
        confirmEmailVerificationCommand: ConfirmEmailVerificationCommand
    ) {

    }

    fun test(id: String): User{
        return signupQueryPort.loadUser(UUID.fromString(id))
    }
}