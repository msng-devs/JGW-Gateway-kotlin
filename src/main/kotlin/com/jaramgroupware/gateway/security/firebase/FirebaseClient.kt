package com.jaramgroupware.gateway.security.firebase

import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.jaramgroupware.gateway.security.firebase.response.TokenResponse
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationErrorCode
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationErrorCode
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Firebase Admin Sdk를 사용하여 토큰 검증 작업을 수행할 수 있는 클래스입니다.
 *
 * @property firebaseAuth
 */
@Component
class FirebaseClient(
    @Autowired
    val firebaseAuth: FirebaseAuth
) {
    /**
     * firebase admin sdk를 사용해 토큰을 검증 및 decode를 수행하고, uid를 반환합니다.
     *
     * @param token 검증 및 uid를 추출할 토큰(id token)
     * @throws AuthenticationException 이메일 인증 실패 혹은 토큰이 유효하지 않을 경우 발생합니다.
     * @throws ApplicationException firebase admin sdk에서 발생하는 예외를 처리합니다. 일반적으로 서버 내부 오류입니다.
     * @return
     */
    fun verifyAndDecodeToken(token: String, isCheckValid: Boolean): TokenResponse {

        lateinit var uid: String
        lateinit var expDataTime: LocalDateTime
        lateinit var email: String
        try {

            val decodedToken = firebaseAuth.verifyIdToken(token)
            uid = decodedToken.uid
            expDataTime =
                Instant.ofEpochSecond(((decodedToken.claims["exp"] as? Long)!!)).atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            email = decodedToken.email ?: ""
            assert(decodedToken.isEmailVerified)

        } catch (e: FirebaseAuthException) {
            if (isCheckValid) processingFireBaseAuthException(e.authErrorCode)
            return TokenResponse(
                uid = null,
                exp = null,
                email = null
            )
        } catch (e: AssertionError) {
            if (isCheckValid) throw AuthenticationException(
                message = "이메일 인증이 되지 않은 계정입니다. 이메일 인증을 완료해주세요.",
                errorCode = AuthenticationErrorCode.INVALID_TOKEN,
            )
            return TokenResponse(
                uid = null,
                exp = null,
                email = null
            )
        }

        return TokenResponse(
            uid = uid,
            exp = expDataTime,
            email = email
        )

    }

    /**
     * firebase 인증 수행중 발생한 에러를 처리하는 함수입니다.
     * 상세한 에러 코드는 다음 링크를 참고.
     * ref  : https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/auth/AuthErrorCode
     * @param authErrorCode
     */
    private fun processingFireBaseAuthException(authErrorCode: AuthErrorCode) {

        when (authErrorCode) {
            AuthErrorCode.EXPIRED_ID_TOKEN,
            AuthErrorCode.INVALID_ID_TOKEN,
            AuthErrorCode.REVOKED_ID_TOKEN,
            AuthErrorCode.TENANT_ID_MISMATCH,
            AuthErrorCode.CERTIFICATE_FETCH_FAILED,
            AuthErrorCode.TENANT_NOT_FOUND,
            AuthErrorCode.USER_DISABLED,
            AuthErrorCode.USER_NOT_FOUND -> throw AuthenticationException(
                message = "유효하지 않은 토큰입니다. 토큰이 만료되었거나, 취소된 토큰입니다.",
                errorCode = AuthenticationErrorCode.INVALID_TOKEN
            )

            else -> throw ApplicationException(
                message = "token 인증 중, firebase 시스템에 알 수 없는 에러가 발생했습니다. 나중에 다시 시도해주세요.",
                errorCode = ApplicationErrorCode.UNKNOWN_ERROR
            )
        }
    }

}