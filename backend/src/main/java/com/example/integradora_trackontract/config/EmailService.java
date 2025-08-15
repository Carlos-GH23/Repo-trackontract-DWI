package com.example.integradora_trackontract.config;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("carlosgalan254@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // <-- true indica que es HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage(), e);
        }
    }
}
