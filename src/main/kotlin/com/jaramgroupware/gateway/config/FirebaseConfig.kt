package com.jaramgroupware.gateway.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

/**
 * Firebase 설정을 위한 클래스
 *
 */
@Configuration
class FirebaseConfig {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Firebase 설정을 위한 메소드
     * Admin SDK를 사용하기 위해 필요한 설정을 진행한다.
     *
     */
    @Bean
    @Throws(IOException::class)
    fun firebaseApp(): FirebaseApp? {
        logger.info("Initializing Firebase.")

        //Firebase 설정을 위한 json 파일을 읽어온다. 해당 파일은 firebase admin sdk를 위한 json 파일이며,
        //해당 파일은 resources 폴더에 위치해야 한다.
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(ClassPathResource("firebase.json").inputStream))
            .setStorageBucket("heroku-sample.appspot.com")
            .build()

        //firebase Admin sdk는 초기 실행시 앱을 초기화 해야하는데,
        //이미 초기화가 되어있는 경우에는 오류가 발생하기에 초기화전 반드시 확인을 해야한다.
        val app: FirebaseApp
        app = if (FirebaseApp.getApps().size == 0) {
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getApps()[0]
        }

        logger.info("FirebaseApp initialized " + app.name)
        return app
    }


    @Bean
    @Throws(IOException::class)
    fun getFirebaseAuth(): FirebaseAuth? {
        return FirebaseAuth.getInstance(firebaseApp())
    }
}