package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class EcommerceController {

    private JavaMailSender mailSender;
    private UserService userService;

    @Autowired
    public EcommerceController(JavaMailSender mailSender,
                               UserService userService) {
        this.mailSender = mailSender;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/info-contact")
    public String contactPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
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
    public String aboutPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
        return "a-propos";
    }

    @GetMapping("/faq")
    public String faqPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
        return "faq";
    }

    @GetMapping("/livraison")
    public String livraisonPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
        return "livraison";
    }

    @GetMapping("/retour")
    public String retourPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
        return "retour";
    }

    @GetMapping("/conditions")
    public String conditionsPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
        return "conditions";
    }

    @GetMapping("/politique-de-confidentialite")
    public String privacyPage(Model model, Authentication authentication) {
        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }
        return "politique-de-confidentialite";
    }
}