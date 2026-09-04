package cloudflight.integra.backend.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending emails using the configured
 * {@link JavaMailSender}.
 */
@Service
public class EmailService {

    private final JavaMailSender emailSender;

    /**
     * Creates an email service with the provided mail sender.
     *
     * @param emailSender the mail sender used to send emails
     */
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Sends a simple text email to the specified recipient.
     *
     * @param to the email address of the recipient
     * @param subject the subject of the email
     * @param text the plain-text content of the email
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }
}
