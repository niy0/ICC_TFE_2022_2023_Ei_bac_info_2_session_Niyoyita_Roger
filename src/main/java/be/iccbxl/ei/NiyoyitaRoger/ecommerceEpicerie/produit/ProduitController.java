package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

    @Autowired
    CategorieService categorieService;

    @Autowired
    CategorieRepository categorieRepository;


    @GetMapping("/index")//faire un autre pour les non admin gérent/manager
    public String showIndex(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "index";
    }

    @GetMapping("/produits")//faire un autre pour les non admin gérent/manager
    public String showAllProducts(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "produit/products";
    }

    @GetMapping("/produits/user")//faire un autre pour les non admin gérent/manager
    public String showAllProductsUser(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "produit/index";
    }

    @GetMapping("/produit/create")
    public String afficherFormulaireNouveauProduit(Model model, HttpServletRequest request) {

        String title = "Ajout d'un nouveau produit.";
        List<Categorie> categorieList = categorieService.getAllCategorie();

        //Générer le lien retour pour l'annulation
        String referrer = request.getHeader("Referer");

        if(referrer!=null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/produits");
        }
        model.addAttribute("title",title);
        model.addAttribute("produit", new Produit());
        model.addAttribute("catList", categorieList);
        return "produit/create";
    }

    @PostMapping("/product/save")
    public String createProduit(@RequestParam("nom") String nom,
                                @RequestParam("description") String description,
                                @RequestParam("prix") String prix,
                                @RequestParam("catId") String catId,
                                @RequestParam("quantite") String quantite,
                                @RequestParam("image") MultipartFile image,
                                @RequestParam("marque") String marque,
                                @RequestParam("motCle") String motCle,
                                Model model) throws IOException, CategorieNotFoundException {

        if (image.isEmpty()) {
            //  renvoyer un message d'erreur ou effectuer une autre action appropriée
            return "redirect:/produits";
        }

        Produit produit = new Produit();
        try {
            byte[] imageBytes = image.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
            produit.setImagePrincipale(blob);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        if(catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if(cat.isPresent()){
                Categorie catRes = categorieService.getCategorieById(cat.get().getId());
                if(produit.getCategorie() == null || !catRes.getNom().equalsIgnoreCase(produit.getCategorie().getNom())) {
                    produit.setCategorie(catRes);
                }
            } else {
                throw new CategorieNotFoundException("Catégorie non trouvée");
            }
        }
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(Double.parseDouble(prix));
        produit.setQuantite(Integer.parseInt(quantite));
        produit.setDisponibilite(true);
        produit.setMarque(marque);
        produit.setDateCreation(LocalDateTime.now());
        produit.setMotsCles(new HashSet(Collections.singletonList(motCle))); // Correction

        produitService.saveProduct(produit);
        model.addAttribute("produit", produit);
        System.out.println(produit);

        return "redirect:/produits";
    }

    @GetMapping("/produit/delete/{id}") //le deleteMapping
    public String deleteProduit(@PathVariable("id") String id) throws ProduitNotFoundException {
        Long idValid = Long.parseLong(id);
        Produit validProduit = produitService.getProductById(id);

        if(validProduit != null) {
            produitService.deleteProductById(idValid);
        }

        return "redirect:/produits";
    }

    @GetMapping("produit/{id}/edit")
    public String edit(Model model, @PathVariable("id") String id, HttpServletRequest request) throws ProduitNotFoundException {
        Produit validProduit = produitService.getProductById(id);
        List<Categorie> categorieList = categorieService.getAllCategorie();
        model.addAttribute("produit", validProduit);
        model.addAttribute("catList", categorieList);

        //Générer le lien retour pour l'annulation
        String referrer = request.getHeader("Referer");

        if(referrer!=null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/produit/"+validProduit.getId());
        }

        return "produit/edit";
    }


    @PutMapping("/produit/{id}/edit")
    public String updateProduit(@Valid @ModelAttribute("produit") Produit produit, BindingResult bindingResult,
                                @PathVariable("id") String id,
                                @RequestParam("motCle") String mot,
                                @RequestParam("catId") String catId,
                                @RequestParam("image") MultipartFile image,
                                Model model) throws ProduitNotFoundException, CategorieNotFoundException {

        List<String> errTab = Collections.emptyList() ;


        if (bindingResult.hasErrors()) {
            System.out.println("error 7777777777"+bindingResult.toString()+"7777777!");
            return "redirect:/produits";
        }

        System.out.println("******************"+produit.getId()+"****************");
        Produit validProduit = produitService.getProductById(id);

        if(validProduit == null) {
            model.addAttribute("error", "Produit non trouvé");
            return "redirect:/produits";
        }

        Long idProd = produit.getId();
        Produit produit1 = produitRepository.getReferenceById(idProd);

        if (!image.isEmpty()) {
            try {
                byte[] imageBytes = image.getBytes();
                Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
                produit1.setImagePrincipale(blob);
            } catch (IOException | SQLException e) {
                model.addAttribute("error", "Erreur lors de la mise à jour de l'image");
                return "errorPage";
            }
        }
        produit.setImagePrincipale(produit1.getImagePrincipale());

        //roduit.setId(idValid);
        if( !mot.isEmpty() && mot.length() >= 2) {
            produit1.getMotsCles().add(new MotCle(mot));//test
        }
        if(produit.getQuantite() >= 0){
            produit1.setQuantite(produit.getQuantite());
        }

        if(catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if(cat.isPresent()){
                Categorie catRes = categorieService.getCategorieById(cat.get().getId());
                if(produit1.getCategorie() == null || !catRes.getNom().equalsIgnoreCase(produit1.getCategorie().getNom())) {
                    produit1.setCategorie(catRes);
                }
            }
        }

        produit.setDateCreation(produit1.getDateCreation());
        produit.setDateModification(LocalDateTime.now());


        System.out.println(errTab);
        //mettre cond errTab vide
        produit.setMotsCles(produit1.getMotsCles());
        produitService.saveProduct(produit);

        produit1.setDateModification(produit.getDateModification());
        produitService.updateProduct(produit1.getId(), produit1);

        model.addAttribute("produit", produit1);
        model.addAttribute("message", errTab);

        System.out.println(produit);
        System.out.println("*****"+produit1+"******");
        return "redirect:/admin/produit/"+ produit.getId();
    }

    @GetMapping("/produit/{id}")
    public String getProduitById(@PathVariable String id, Model model) throws ProduitNotFoundException {
        Produit produit = produitService.getProductById(id);
        if (produit != null) {
            model.addAttribute("produit", produit);

            System.out.println("-----------*********tets-----------------");
            System.out.println(produit.getMotsCles().isEmpty());
            System.out.println(produit.getMotsCles().size());
            return "/produit/show";
        } else {
            return "redirect:/produits";
        }
    }

    @GetMapping("/admin/produit/{id}")
    public String adminGetProduitById(@PathVariable String id, Model model) throws ProduitNotFoundException {
        Produit produit = produitService.getProductById(id);
        if (produit != null) {
            model.addAttribute("produit", produit);
            return "/produit/show/admin";
        } else {
            return "redirect:/produits";
        }
    }

    // display image
    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id) throws IOException, SQLException {
        Optional<Produit> produit = produitRepository.findById(id);
        byte [] imageBytes = null;

        if(produit.isPresent()) {
            imageBytes = produit.get().getImagePrincipale().getBytes(1,(int) produit.get().getImagePrincipale().length());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        }
        return (ResponseEntity<byte[]>) ResponseEntity.noContent();
    }
}
