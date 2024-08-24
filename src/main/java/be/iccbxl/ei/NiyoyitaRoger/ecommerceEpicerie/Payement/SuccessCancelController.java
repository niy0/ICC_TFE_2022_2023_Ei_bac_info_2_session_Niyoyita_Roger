package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.CommandeService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.MethodCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;

import java.math.BigDecimal;
import java.util.List;

import org.thymeleaf.context.Context;


import java.util.Locale;

@Controller
public class SuccessCancelController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout/success")
    public String handleStripeSuccess(
            @RequestParam("session_id") String sessionId,
            @RequestParam("methodCommande") String methodCommandeStr,
            @RequestParam("prenom") String prenom,
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam("rue") String rue,
            @RequestParam("numero") String numero,
            @RequestParam("localite") String localite,
            @RequestParam("ville") String ville,
            @RequestParam("codePostal") String codePostal,
            @RequestParam("departement") String departement,
            @RequestParam("pays") String pays,
            @RequestParam("montantCommande") BigDecimal montantCommande,  // Montant de la commande
            @RequestParam("idPanierStripe") String idPanierStripe, // Ajout
            HttpSession session) throws StripeException, MessagingException {

        Panier panier = panierService.getPanierById(Long.valueOf(idPanierStripe));
        Long idUser = null;
        List<LigneDeCommande> list = panier.getLignesDeCommande();
        User utilisateur = null;

        //récupere l'id de l'utilisateur grace au panier
        if (panier.getUtilisateur() != null) {
            idUser = panier.getUtilisateur().getId();
        }

        //recupère l'utilisateur
        if (idUser != null) {
            utilisateur = userService.getUserById(idUser);
        }

        System.out.println(utilisateur);
        MethodCommande methodCommande;
        try {
            methodCommande = MethodCommande.valueOf(methodCommandeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid methodCommande value: " + methodCommandeStr, e);
        }

        // Créeation d'une commande
        Commande nouvelleCommande;

        if (utilisateur != null) {
            nouvelleCommande = new Commande(prenom, nom, email, rue, numero, localite, ville, codePostal, departement, pays, methodCommande);
            nouvelleCommande.setMontantCommande(montantCommande); // ajout montantCommande
            nouvelleCommande.getLignesDeCommande().addAll(list);
            nouvelleCommande.setUserId(idUser);
        } else {
            nouvelleCommande = new Commande(prenom, nom, email, rue, numero, localite, ville, codePostal, departement, pays, methodCommande);
            nouvelleCommande.setMontantCommande(montantCommande); // ajout montantCommande
            nouvelleCommande.getLignesDeCommande().addAll(list);
        }
        commandeService.createCommande(nouvelleCommande);

        //Ajouter id de commande dans les lignes de commande
        nouvelleCommande.getLignesDeCommande().forEach(ligne -> ligne.setCommande(nouvelleCommande));
        commandeService.createCommande(nouvelleCommande);

        System.out.println("Nouvelle commande créée : " + nouvelleCommande);

        //envoyer mail facture
        sendInvoiceEmail(nouvelleCommande, email);

        //vider le panier

        return "redirect:/commande/detail/" + nouvelleCommande.getId();
    }

    @GetMapping("/checkout/cancel")
    public String handleStripeCancel() {
        return "stripe/cancel";
    }

    @GetMapping("/checkout/infos_de_commande")
    public String infosDeCommande(@RequestParam("idPanierStripe") Long idPanier, Model model) {
        try {
            Panier panier = panierService.getPanierById(idPanier);
            User utilisateur = panier.getUtilisateur(); // Récupération de l'utilisateur associé au panier

            BigDecimal montantTotal = panier.getLignesDeCommande().stream()
                    .map(ligne -> ligne.getProduit().getPrix().multiply(new BigDecimal(ligne.getQuantite())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("panier", panier);
            model.addAttribute("montantTotalPanier", montantTotal);

            //informations utilisateur si elles sont disponibles + a compléter
            if (utilisateur != null) {
                model.addAttribute("firstName", utilisateur.getPrenom());
                model.addAttribute("lastName", utilisateur.getNom());
                model.addAttribute("email", utilisateur.getEmail());
            } else {
                // Pour les utilisateurs non associés, ajoutez des attributs vides ou des valeurs par défaut
                model.addAttribute("firstName", "");
                model.addAttribute("lastName", "");
                model.addAttribute("email", "");
            }

            return "stripe/infosDeCommande";
        } catch (Exception e) {
            // journal pour l'exception
            System.err.println("Erreur lors de la récupération du panier: " + e.getMessage());
            e.printStackTrace();
            // page d'erreur personnalisée
            return "error";
        }
    }

    private void sendInvoiceEmail(Commande commande, String toEmail) throws MessagingException {
        // Créez un objet MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Définir les destinataires, le sujet, etc.
        helper.setTo(toEmail);
        helper.setSubject("Votre facture pour la commande #" + commande.getId());

        // Créez le contexte pour Thymeleaf
        Context context = new Context(Locale.FRENCH);
        context.setVariable("commande", commande);

        // Générez le contenu de l'e-mail en utilisant Thymeleaf
        String htmlContent = templateEngine.process("commande/emailFacture", context);

        // Ajoutez le contenu de l'e-mail au MimeMessage
        helper.setText(htmlContent, true);  // true pour HTML

        // Envoyer l'e-mail
        mailSender.send(message);
    }
}