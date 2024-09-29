package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.CommandeService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.MethodCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.livraison.LivraisonService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitService;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.thymeleaf.context.Context;


import java.util.Locale;

@Controller
public class SuccessCancelController {

    private final PanierService panierService;
    private final CommandeService commandeService;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserService userService;
    private final LivraisonService livraisonService;
    private final ProduitService produitService;
    private final FactureService factureService;

    @Autowired
    public SuccessCancelController(PanierService panierService,
                                   CommandeService commandeService,
                                   JavaMailSender mailSender,
                                   TemplateEngine templateEngine,
                                   UserService userService,
                                   LivraisonService livraisonService,
                                   ProduitService produitService,
                                   FactureService factureService) {
        this.panierService = panierService;
        this.commandeService = commandeService;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.userService = userService;
        this.livraisonService = livraisonService;
        this.produitService = produitService;
        this.factureService = factureService;
    }

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
            @RequestParam("fraisLivraison") BigDecimal fraisLivraison, // Ajout des frais de livraison
            HttpSession session) throws StripeException, MessagingException, IOException {

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
            nouvelleCommande.setFraisCommande(fraisLivraison);
        } else {
            nouvelleCommande = new Commande(prenom, nom, email, rue, numero, localite, ville, codePostal, departement, pays, methodCommande);
            nouvelleCommande.setMontantCommande(montantCommande); // ajout montantCommande
            nouvelleCommande.setFraisCommande(fraisLivraison);
            nouvelleCommande.getLignesDeCommande().addAll(list);
        }

        commandeService.createCommande(nouvelleCommande);

        System.out.println("Nouvelle commande créée : " + nouvelleCommande);

        // Réduire la quantité de chaque produit en fonction de la commande
        for (LigneDeCommande ligne : nouvelleCommande.getLignesDeCommande()) {
            Produit produit = ligne.getProduit(); // Supposons que chaque ligne de commande ait une référence au produit
            int quantiteAchetee = ligne.getQuantite(); // Quantité achetée
            int nouvelleQuantiteStock = produit.getQuantite() - quantiteAchetee;

            // Vérifier si le produit n'est pas en rupture de stock
            if (nouvelleQuantiteStock < 0) {
                throw new RuntimeException("La quantité en stock pour le produit " + produit.getNom() + " est insuffisante.");
            }

            // Mettre à jour la quantité du produit
            produit.setQuantite(nouvelleQuantiteStock);

            // Si la nouvelle quantité est zéro, désactiver le produit
            if (nouvelleQuantiteStock == 0) {
                produit.setActif(false); // Supposons que le produit ait un champ "actif" pour indiquer s'il est disponible à la vente
            }

            // Enregistrer les modifications du produit
            produitService.updateProduit(produit);
        }

        //crée une facture
        /**Facture nouvelleFacture = factureService.creerFacture(
         nouvelleCommande,
         prenom,
         nom,
         rue,
         numero,
         localite,
         ville,
         codePostal,
         departement,
         pays
         );**/

        //envoyer mail facture
        sendInvoiceEmail(nouvelleCommande, email);

        //vider le panier
        panierService.deleteAllLigneDeCommande(panier.getId());
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

    // Méthode pour envoyer l'email avec facture
    private void sendInvoiceEmail(Commande commande, String toEmail) throws MessagingException, IOException {
        // Créez un objet MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Définir les destinataires, le sujet, etc.
        helper.setTo(toEmail);
        helper.setSubject("Votre facture pour la commande #" + commande.getId());

        // Charger le modèle HTML depuis un fichier
        String htmlTemplate = loadHtmlTemplate("src/main/resources/templates/commande/emailFacture.html");

        // Calculer la TVA (20%) et le total avec la TVA
        BigDecimal tva = commande.getMontantCommande().multiply(new BigDecimal("0.20"));
        BigDecimal totalAvecTVA = commande.getMontantCommande().add(commande.getFraisCommande());

        // Construire les lignes de commande
        StringBuilder lignesDeCommandeHtml = new StringBuilder();
        for (LigneDeCommande ligne : commande.getLignesDeCommande()) {
            lignesDeCommandeHtml.append("<tr>")
                    .append("<td>").append(ligne.getProduit().getNom()).append("</td>")
                    .append("<td>").append(ligne.getQuantite()).append("</td>")
                    .append("<td>").append(String.format("%.2f", ligne.getProduit().getPrix())).append(" €</td>")
                    .append("<td>").append(String.format("%.2f", ligne.getMontantTotal())).append(" €</td>")
                    .append("</tr>");
        }

        // Remplacer les placeholders avec les données de la commande
        String htmlContent = htmlTemplate
                .replace("${commandeId}", String.valueOf(commande.getId()))
                .replace("${dateCommande}", formatDate(commande.getDateCommande(), "dd/MM/yyyy"))
                .replace("${heureCommande}", formatDate(commande.getDateCommande(), "HH:mm"))
                .replace("${prenom}", commande.getPrenom())
                .replace("${nom}", commande.getNom())
                .replace("${rue}", commande.getRue())
                .replace("${numero}", String.valueOf(commande.getNumero()))
                .replace("${codePostal}", commande.getCodePostal())
                .replace("${ville}", commande.getVille())
                .replace("${pays}", commande.getPays())
                .replace("${email}", commande.getEmail())
                .replace("${methodCommande}", commande.getMethodCommande().name())
                .replace("${statut}", commande.getStatut().name())
                .replace("${lignesDeCommande}", lignesDeCommandeHtml.toString())
                .replace("${montantCommande}", String.format("%.2f", commande.getMontantCommande()))
                .replace("${tva}", String.format("%.2f", tva))
                .replace("${fraisCommande}", String.format("%.2f", commande.getFraisCommande()))
                .replace("${totalAvecFrais}", String.format("%.2f", totalAvecTVA));

        // Ajouter le contenu de l'e-mail au MimeMessage
        helper.setText(htmlContent, true);  // true pour HTML

        // Envoyer l'e-mail
        mailSender.send(message);
    }

    // Méthode pour charger le fichier HTML
    private String loadHtmlTemplate(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    // Méthode pour formater la date
    private String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}