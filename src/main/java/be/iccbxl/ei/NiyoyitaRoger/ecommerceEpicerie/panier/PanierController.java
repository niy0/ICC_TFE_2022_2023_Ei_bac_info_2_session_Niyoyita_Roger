package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.NoSuchElementException;

@Controller
public class PanierController {

    @Autowired
    private ProduitRepository produitRepository;

    /**@Autowired
    private UtilisateurRepository utilisateurRepository;**/


    @GetMapping("panier/show")
    public String afficherPanier(Model model, HttpSession session) {
        // Obtenez le panier de la session
        Panier panier = (Panier) session.getAttribute("panier");

        if (panier == null) {
            panier = new Panier();
            session.setAttribute("panier", panier);
        }

        model.addAttribute("panier", panier);
        return "panier/show";
    }

    @PostMapping("/ajouter/{produitId}")
    public String ajouterProduitAuPanier(@PathVariable Long produitId, HttpSession session) {
        Produit produit = produitRepository.findById(produitId).orElseThrow(() -> new NoSuchElementException("Produit non trouvé"));
        Panier panier = (Panier) session.getAttribute("panier");

        if (panier == null) {
            panier = new Panier();
            session.setAttribute("panier", panier);
        }

       // panier.ajouterProduit(produit);
        return "redirect:/panier";
    }

    /**
    @PostMapping("/valider")
    public String validerPanier(HttpSession session, Principal principal) {
        Panier panier = (Panier) session.getAttribute("panier");
        Utilisateur utilisateur = utilisateurRepository.findByNom(principal.getName());

        if (panier != null && utilisateur != null) {
            Commande commande = new Commande();
            commande.setUtilisateur(utilisateur);
            commande.setLignesDeCommande(new ArrayList<>(panier.getLignesDeCommande()));
            commandeRepository.save(commande);
            session.removeAttribute("panier");
        }

        return "redirect:/";
    }**/
}
