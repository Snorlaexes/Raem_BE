package com.snorlaexes.raem.global.smtp;

import com.snorlaexes.raem.global.apiPayload.code.status.ErrorStatus;
import com.snorlaexes.raem.global.apiPayload.exception.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
public class SMTPService {
    private final JavaMailSender javaMailSender;
    // 발신 이메일 데이터 설정
    private SimpleMailMessage createEmailForm(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        return message;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage emailForm = createEmailForm(to, subject, text);
        try {
            javaMailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new ExceptionHandler(ErrorStatus._UNABLE_TO_SEND_EMAIL);
        }
    }

    // 이메일 전송 비동기 처리
    public void sendAsyncEmail(String to, String subject, String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new Thread(
                () -> {
                    sendEmail(to, subject, text);
                    future.complete(null);
                }
        ).start();
    }
}
