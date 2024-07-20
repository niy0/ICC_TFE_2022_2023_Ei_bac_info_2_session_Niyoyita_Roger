package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.CommandeService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.MethodCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;

@Controller
public class SuccessCancelController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private CommandeService commandeService;

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
            Principal principal,
            HttpSession session) throws StripeException {

        Panier panier = panierService.getOrCreatePanier(principal, session);
        User utilisateur = null;
        if (principal != null) {
            utilisateur = userService.getUserByEmail(principal.getName());
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
            nouvelleCommande = new Commande(utilisateur, panier, methodCommande);
            nouvelleCommande.setMontantCommande(montantCommande); // ajout montantCommande
        } else {
            nouvelleCommande = new Commande(prenom, nom, email, rue, numero, localite, ville, codePostal, departement, pays, panier, methodCommande);
            nouvelleCommande.setMontantCommande(montantCommande); // ajout montantCommande
        }
        commandeService.createCommande(nouvelleCommande);
        System.out.println("Nouvelle commande créée : " + nouvelleCommande);

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
}
