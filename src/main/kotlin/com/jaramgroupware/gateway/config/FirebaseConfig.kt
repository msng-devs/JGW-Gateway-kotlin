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

@Configuration
class FirebaseConfig {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    @Throws(IOException::class)
    fun firebaseApp(): FirebaseApp? {
        logger.info("Initializing Firebase.")
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(ClassPathResource("firebase.json").inputStream))
            .setStorageBucket("heroku-sample.appspot.com")
            .build()
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