package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie;


import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class EcommerceController {

    @Autowired
    private ProduitService produitService;


    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/info-contact")
    public String contactPage() {
        return "contact";
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

