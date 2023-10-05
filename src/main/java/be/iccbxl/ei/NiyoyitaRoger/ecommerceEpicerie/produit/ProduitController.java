package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCleRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCleService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;


import javax.imageio.ImageIO;
import java.util.List;
import java.util.Optional;

@Controller
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private CategorieService categorieService;

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private MotCleRepository motCleRepository;

    @Autowired
    private MotCleService motCleService;


    @GetMapping("/produits/admin")//faire un autre pour les non admin gérent/manager
    public String showAllProducts(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "produit/admin_produit";
    }

    @GetMapping("/produit/{id}")
    public String getProduitById(@PathVariable long id, Model model) throws ProduitNotFoundException {
        //List<Produit> produitList = (List<Produit>) produitRepository.findAll();
        //Long id = Long.parseLong(idStr);
        Produit test = produitRepository.getPro(id);

        if(test != null){
            model.addAttribute("produit", test);
            return "/produit/show";
        }

        return "redirect:/produits/admin";
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
    public String createProduit(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") String prix,
            @RequestParam("catId") String catId,
            @RequestParam("quantite") String quantite,
            @RequestParam("image") MultipartFile image,
            @RequestParam("marque") String marque,
            @RequestParam("motCle") String motCle,
            Model model) throws CategorieNotFoundException {

        if (image.isEmpty()) {
            return "redirect:/produits";
        }

        Produit produit = new Produit();

        try {
            // Lire l'image à l'aide de ImageIO
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

            // Convertir l'image en format JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpeg", baos);
            byte[] imageBytes = baos.toByteArray();

            // Enregistrez l'image dans l'objet Produit
            Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
            produit.setImagePrincipale(blob);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        System.out.println("**********/test*********cat"+catId+"********************");
        if(catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if(cat.isPresent()){
                Categorie catRes = categorieService.getCategorieById(cat.get().getId());
                produit.setCategorie(catRes);
            }
        }

        System.out.println(produit);
        // Faire de même pour la gestion des mots-clés
        if(catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if(cat.isPresent()){
                Categorie catRes = cat.orElseThrow(() -> new CategorieNotFoundException("Catégorie non trouvée"));
                produit.setCategorie(catRes);
            } else {
                throw new CategorieNotFoundException("Catégorie non trouvée");
            }
        }

        if(motCle.trim().length() >= 1 && motCle != null){
            MotCle newMot = new MotCle(motCle);
            motCleService.save(newMot);
            produit.getMotsCles().add(newMot);
        }

        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(Double.parseDouble(prix));
        produit.setQuantite(Integer.parseInt(quantite));
        produit.setDisponibilite(true);
        produit.setMarque(marque);
        produit.setDateCreation(LocalDateTime.now());

        produitService.updateProduit(produit);

        model.addAttribute("produit", produit);

        return "redirect:/produits/admin";
    }

    @DeleteMapping("/produit/{id}")
    public String deleteProduit(@PathVariable("id") long id)  {
        try {
            // Vérifiez si le produit existe avant de le supprimer
            if (produitRepository.existsById(id)) {
                List<Produit> produitList = (List<Produit>) produitRepository.findAll();
                for(Produit pro : produitList){
                    if(pro.getId() == id){
                        produitService.deleteProductById(id);
                        return "redirect:/produits/admin";
                    }
                }
            } else {
                // Gérez le cas où le produit n'existe pas (éventuellement en affichant un message d'erreur)
                System.out.println("Le produit avec l'ID " + id + " n'existe pas.");
            }
        } catch (NumberFormatException e) {
            // Gérez le cas où l'ID n'est pas un nombre valide (par exemple, affichez un message d'erreur)
            System.out.println("L'ID " + id + " n'est pas valide.");
        }

        return "redirect:/produits/admin";
    }

    @GetMapping("produit/{id}/edit")
    public String edit(Model model, @PathVariable("id") String idStr, HttpServletRequest request) throws ProduitNotFoundException {
        Long id = Long.parseLong(idStr);
        Produit validProduit = produitRepository.getPro(id);
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

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") String idString) throws IOException, SQLException, ProduitNotFoundException {
        long id = Long.parseLong(idString);
        List<Produit> produitList = (List<Produit>) produitRepository.findAll();

        Produit produit = produitService.getProduitById(id);
        //System.out.println("************test"+ produit+ "****** id="+ id);
        if(produitRepository.existsById(produit.getId())) {
            byte[] imageBytes = produit.getImagePrincipale().getBytes(1, (int) produit.getImagePrincipale().length());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        }
        return ResponseEntity.noContent().build(); // Notez l'appel à .build() ici
    }

}
