package com.mythovac.configtemplate.configuration

import com.mythovac.configtemplate.listener.MySessionListener
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 密码编码器 存取数据库时的 加密和验证器
 * */
@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
    @Bean
    fun sessionListener(): ServletListenerRegistrationBean<MySessionListener> {
        return ServletListenerRegistrationBean(MySessionListener())
    }

}