package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class SuccessCancelController {

    @Autowired
    private PanierService panierService;

    @GetMapping("/checkout/success")
    public String success() {
        return "stripe/success";
    }

    @GetMapping("/checkout/cancel")
    public String cancel() {
        return "stripe/cancel";
    }

    @GetMapping("/checkout/infos_de_commande")
    public String infosDeCommande(@RequestParam("idPanierStripe") Long idPanier, Model model) {
        try {
            Panier panier = panierService.getPanierById(idPanier);
            User utilisateur = panier.getUtilisateur(); // Récupérez l'utilisateur associé au panier

            System.out.println(utilisateur + "uuuuuuuuuuuuuuuuiiiiiiiiiiiiiuuuuser stripe");

            BigDecimal montantTotal = panier.getLignesDeCommande().stream()
                    .map(ligne -> ligne.getProduit().getPrix().multiply(new BigDecimal(ligne.getQuantite())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("panier", panier);
            model.addAttribute("montantTotalPanier", montantTotal);

            // Ajoutez les informations utilisateur si elles sont disponibles
            if (utilisateur != null) {
                model.addAttribute("firstName", utilisateur.getNom());
                model.addAttribute("lastName", utilisateur.getPrenom());
                model.addAttribute("email", utilisateur.getEmail());
            } else {
                // Pour les utilisateurs non associés, ajoutez des attributs vides ou des valeurs par défaut
                model.addAttribute("firstName", "");
                model.addAttribute("lastName", "");
                model.addAttribute("email", "");
            }

            return "stripe/infosDeCommande";
        } catch (Exception e) {
            // Ajoutez un journal pour l'exception
            System.err.println("Erreur lors de la récupération du panier: " + e.getMessage());
            e.printStackTrace();
            // Vous pouvez également ajouter une page d'erreur personnalisée si nécessaire
            return "error";
        }
    }



}
