package com.example.cinema.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@AllArgsConstructor
@Service
@Async
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String SENDER = "noreply@cinemaze.com";

    public void sendEmailHtml(String subject, String content, String to) {
        log.debug("Sending email to: {}", to);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setFrom(SENDER);
            helper.setTo(to);

            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error while sending email", e);
            throw new IllegalStateException("Failed to send email");
        }
    }
}
