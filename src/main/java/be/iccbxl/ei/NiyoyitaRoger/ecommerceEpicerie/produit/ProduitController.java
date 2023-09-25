package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
public class ProduitController {

    @Autowired
    ProduitRepository produitRepository;

    @Autowired
    ProduitService produitService;


    @GetMapping("/produits")//faire un autre pour les non admin gérent/manager
    public String showAllProducts(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        //List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        //model.addAttribute("catList", categorieList);
        return "produit/products";
    }

}
