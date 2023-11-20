package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EcommerceController {

    @Autowired
    private ProduitService produitService;

    //@Autowired
    //private CommandeService commandeService;

    @GetMapping("/login/test")
    public String loginPage(){
        return "user/login";
    }


    @GetMapping("/user/profile")
    public String userHomePage(){
        return "user/profile";
    }



    /**

    @GetMapping("/signup")
    public String showUserSignUpForm(Model model) {
        model.addAttribute("user", new User(null,null,null,null));
        model.addAttribute("isAdmin", false);
        model.addAttribute("isMember", false);
        return "user/user_signup_form";
    }**/

    //page a faire "/info-contact","/info-livraison","/info-retour","/info-utilisation","/info-confidentialite"

    @GetMapping("/achanger")
    public String accueil(Model model, HttpSession session) {
        //List<Produit> produits = produitService.getProduits();
       // model.addAttribute("produits", produits);

        // Récupérer le panier de la session
        List<Produit> panier = (List<Produit>) session.getAttribute("panier");
        if (panier == null) {
            panier = new ArrayList<>();
            session.setAttribute("panier", panier);
        }

        model.addAttribute("panier", panier);

        return "accueil";
    }

    @PostMapping("/ajouter-au-panier")
    public String ajouterAuPanier(Long produitId, HttpSession session) {
        //Produit produit = produitService.getProduit(produitId);

        // Récupérer le panier de la session
        List<Produit> panier = (List<Produit>) session.getAttribute("panier");
        if (panier == null) {
            panier = new ArrayList<>();
            session.setAttribute("panier", panier);
        }

       // panier.add(produit);

        return "redirect:/";
    }

    @GetMapping("/passer-commande")
    public String passerCommande(Model model, HttpSession session) {
        List<Produit> panier = (List<Produit>) session.getAttribute("panier");
        if (panier == null || panier.isEmpty()) {
            // Le panier est vide
            return "redirect:/";
        }

        // Vous pouvez implémenter ici le processus de paiement, l'adresse de livraison, etc.

        // Créer une commande avec les produits du panier
        Commande commande = new Commande();

        // a faire commande.setProduits(panier);

        // a faire commandeService.enregistrerCommande(commande);

        // Vider le panier
        panier.clear();

        return "commande-confirmee";
    }
}

