package com.snorlaexes.raem.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class SMTPConfig {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private Boolean auth;
    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int timeout;
    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
    private int writeTimeout;
    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private int connectionTimeout;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean tlsEnable;
    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private Boolean tlsRequired;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getSMTPProperties());

        return mailSender;
    }

    private Properties getSMTPProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", tlsEnable);
        properties.put("mail.smtp.starttls.required", tlsRequired);
        properties.put("mail.smtp.connectiontimeout", connectionTimeout);
        properties.put("mail.smtp.timeout", timeout);
        properties.put("mail.smtp.writetimeout", writeTimeout);

        return properties;
    }
}
