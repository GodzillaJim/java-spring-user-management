package com.godzillajim.user_management.mails;

import com.godzillajim.user_management.exceptions.SomethingWentWrongException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender{
    private final static Logger log = LoggerFactory.getLogger("Email service " +
            "class");
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("admin@godspace.com");
            mailSender.send(message);
        }catch(MessagingException e){
            log.error("Failed to send email", e);
            throw new SomethingWentWrongException("Failed to send " +
                    "confirmation code");
        }
    }
}
