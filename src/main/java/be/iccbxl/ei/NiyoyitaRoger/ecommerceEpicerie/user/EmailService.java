package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String recipientEmail, String resetUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Réinitialisation du mot de passe");

            String emailContent = "<p>Bonjour,</p>"
                    + "<p>Vous avez demandé à réinitialiser votre mot de passe. Cliquez sur le lien ci-dessous pour le faire :</p>"
                    + "<p><a href=\"" + resetUrl + "\">Réinitialiser mon mot de passe</a></p>"
                    + "<p>Si vous n'avez pas fait cette demande, ignorez cet email.</p>";

            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
            logger.info("Email de réinitialisation envoyé à {}", recipientEmail);

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à {}: {}", recipientEmail, e.getMessage());
        }
    }
}


