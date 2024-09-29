package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


@Controller
public class EcommerceController {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    @Autowired
    public EcommerceController(JavaMailSender mailSender,
                               UserService userService,
                               PasswordResetTokenService passwordResetTokenService,
                               EmailService emailService) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
    }

    // Page "Mot de passe oublié"
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request) {
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return "redirect:/forgot-password?error";
        }

        // Générer un token
        String token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user, token);

        // Construire le lien de réinitialisation
        String resetUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/reset-password?token=" + token;

        // Envoyer l'e-mail de réinitialisation
        emailService.sendPasswordResetEmail(userEmail, resetUrl);

        return "redirect:/forgot-password?success";
    }

    // Page "Réinitialisation de mot de passe"
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "resetPassword";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword) {

        // Valider le token et réinitialiser le mot de passe de l'utilisateur
        if (password.equals(confirmPassword)) {
            userService.updatePassword(token, password);
            return "redirect:/login?resetSuccess";
        } else {
            return "redirect:/reset-password?token=" + token + "&error";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "lang", required = false) String lang, HttpServletRequest request, HttpServletResponse response) {
        if (lang != null) {
            Locale newLocale = new Locale(lang);
            CookieLocaleResolver localeResolver = new CookieLocaleResolver();
            localeResolver.setLocale(request, response, newLocale);
        }
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