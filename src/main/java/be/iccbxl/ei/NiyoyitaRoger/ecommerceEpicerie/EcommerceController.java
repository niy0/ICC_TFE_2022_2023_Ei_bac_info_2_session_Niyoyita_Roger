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


    @GetMapping("/login")
    public String loginPage(){
        return "user/login";
    }


    @GetMapping("/user/profile")
    public String userHomePage(){
        return "user/profile";
    }


    //("/info-contact","/info-livraison","/info-retour","/info-utilisation","/info-confidentialite","/addToCart").permitAll()
    @GetMapping("/info-contact")
    public String contactPage(){
        return "user/profile";
    }

    @GetMapping("/info-livraison")
    public String infoLivraisonPage(){
        return "user/profile";
    }

    @GetMapping("/info-retour")
    public String infoRetourPage(){
        return "user/profile";
    }


    //page a faire "/info-contact","/info-livraison","/info-retour","/info-utilisation","/info-confidentialite"


}

