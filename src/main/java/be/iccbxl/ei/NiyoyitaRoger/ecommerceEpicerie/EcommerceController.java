package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie;

import org.springframework.mail.javamail.JavaMailSender;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class EcommerceController {

    private JavaMailSender mailSender;

    @Autowired
    public EcommerceController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/info-contact")
    public String contactPage() {
        return "contact";
    }

    @PostMapping("/info-contact")
    public String sendEmail(@RequestParam("nom") String nom,
                            @RequestParam("email") String email,
                            @RequestParam("sujet") String sujet,
                            @RequestParam("message") String message,
                            RedirectAttributes redirectAttributes) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(email);
            helper.setTo("contactepicerielhoumeau@gmail.com");
            helper.setSubject(sujet);

            String textMessage = "Nom: " + nom + "\n\n"
                    + "Email de réponse : " + email + "\n\n"
                    + "Sujet  : " + sujet + "\n\n"
                    + "Message: " + message;

            helper.setText(textMessage, true);

            mailSender.send(mimeMessage);

            redirectAttributes.addFlashAttribute("successMessage", "Votre message a été envoyé avec succès!");

        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'envoi du message. Veuillez réessayer.");
            e.printStackTrace();
        }

        return "redirect:/info-contact";
    }

    @GetMapping("/a-propos")
    public String aboutPage() {
        return "a-propos";
    }

    @GetMapping("/faq")
    public String faqPage() {
        return "faq";
    }

    @GetMapping("/livraison")
    public String livraisonPage() {
        return "livraison";
    }

    @GetMapping("/retour")
    public String retourPage() {
        return "retour";
    }

    @GetMapping("/conditions")
    public String conditionsPage() {
        return "conditions";
    }

    @GetMapping("/politique-de-confidentialite")
    public String privacyPage() {
        return "politique-de-confidentialite";
    }
}