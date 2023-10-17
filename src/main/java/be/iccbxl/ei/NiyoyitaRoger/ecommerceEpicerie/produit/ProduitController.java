package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.CategorieService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque.Marque;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque.MarqueRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque.MarqueService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCleRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCleService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private MarqueRepository marqueRepository;

    @Autowired
    private MarqueService marqueService;

    @GetMapping("/")//faire un autre pour les non admin gérent/manager
    public String showIndex(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "index";
    }
    /**
    @GetMapping("/produit")
    public String allProduit(Model model) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "produit/index_produits";
    }**/


    //revoir l'erreur de la pagination !!
    @GetMapping("/produit")
    public String allProduit(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 24); // 20 produits par page
        Page<Produit> productPage = produitRepository.findAll(pageable); // Vous devriez avoir une méthode 'findAll' qui accepte un objet 'Pageable'

        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productPage.getContent());
        model.addAttribute("catList", categorieList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "produit/index_produits";
    }

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
        Produit produit = produitRepository.getPro(id);
        if(produit != null){
            model.addAttribute("produit", produit);
            return "/produit/show";
        }
        return "redirect:/produits/admin";
    }

    @GetMapping("/produit/create")
    public String afficherFormulaireNouveauProduit(Model model, HttpServletRequest request) {
        String message = "";
        String title = "Nouveau produit";

        //Trié les listes par ordre !!
        List<Categorie> categorieList = categorieService.getAllCategorie();
        List<MotCle> motCleList = motCleService.getAllMotCle();
        List<Marque> marqueList = marqueService.getAllMarque();

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
        model.addAttribute("motCleList", motCleList);
        model.addAttribute("marqueList", marqueList);
        model.addAttribute("message",message);

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
            @RequestParam("marqueId") String marque,
            @RequestParam("motCleIds") List<Long> motCleIds,
            @RequestParam("typePrix") String typePrix,
            @RequestParam("minStock") String minStock,
            @RequestParam("maxStock") String maxStock,
            @RequestParam(name = "actif", required = false) Boolean actif,
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

        if(catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if(cat.isPresent()){
                Categorie catRes = categorieService.getCategorieById(cat.get().getId());
                produit.setCategorie(catRes);
            } else {
                throw new CategorieNotFoundException("Catégorie non trouvée");
            }
        }

        System.out.println(produit);
        // Faire de même pour la gestion des mots-clés
        String errorCat = "";
        if(catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if(cat.isPresent()){
                Categorie catRes = cat.orElseThrow(() -> new CategorieNotFoundException("Catégorie non trouvée"));
                produit.setCategorie(catRes);
            } else {
                throw new CategorieNotFoundException("Catégorie non trouvée");
            }
        }

        //Ajout des mots clés
        for(Long motCleId : motCleIds) {
            MotCle motCle = motCleService.getMotCle(motCleId);
            produit.getMotsCles().add(motCle);
        }
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(Double.parseDouble(prix));
        produit.setDisponibilite(true);
        produit.setTypePrix(typePrix);
        quantite.trim();
        minStock.trim();
        maxStock.trim();
        produit.setQuantite(Integer.parseInt(quantite));

        if(!minStock.isEmpty() && minStock != "") {
            produit.setMinStock(Integer.parseInt(minStock));
        }
        if(!maxStock.isEmpty() && maxStock != ""){
            produit.setMaxStock(Integer.parseInt(maxStock));
        }

        if (actif == null) {
            produit.setActif(false);
        }

        if(marque != null && !marque.trim().isEmpty()) {
            Optional<Marque> marqueRes = Optional.ofNullable(marqueRepository.findById(Long.parseLong(marque)));
            if(marqueRes.isPresent()) {
                Marque marque1 = marqueRes.get();
                produit.setMarque(marque1);
            }
            System.out.println(marqueRes+"***************************debug");
        }

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

    @GetMapping("produit/{id}/edit")//faire attention qu'il existe
    public String edit(Model model, @PathVariable("id") String idStr, HttpServletRequest request) throws ProduitNotFoundException {
        Long id = Long.parseLong(idStr);

        Produit validProduit = produitRepository.getPro(id);
        List<Categorie> categorieList = categorieService.getAllCategorie();
        List<MotCle> motCleList = motCleService.getAllMotCle();
        List<Marque> marqueList = marqueService.getAllMarque();

        model.addAttribute("produit", validProduit);
        model.addAttribute("catList", categorieList);
        model.addAttribute("motCleList", motCleList);
        model.addAttribute("marqueList", marqueList);

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
    public String updateProduit(
            @PathVariable("id") Long id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") String prix,
            @RequestParam("catId") String catId,
            @RequestParam("quantite") String quantite,
            @RequestParam("image") MultipartFile image,
            @RequestParam("marqueId") String marque,
            @RequestParam("motCleIds") List<Long> motCleIds,
            @RequestParam("typePrix") String typePrix,
            @RequestParam("minStock") String minStock,
            @RequestParam("maxStock") String maxStock,
            @RequestParam(name = "actif", required = false) Boolean actif,
            Model model) throws CategorieNotFoundException, ProduitNotFoundException {

        //Produit produit = produitService.getProduitById(id); // Récupérer le produit existant par son ID
        Produit produit = produitRepository.getPro(id);

        // Vérifier si une nouvelle image a été sélectionnée
        if (!image.isEmpty()) {
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
        }

        if (catId != null && !catId.trim().isEmpty()) {
            // Valider catId avant de le convertir en Long
            try {
                Long categoryId = Long.parseLong(catId);
                Categorie cat = categorieRepository.findById(categoryId).orElse(null);
                if (cat != null) {
                    produit.setCategorie(cat);
                } else {
                    throw new CategorieNotFoundException("Catégorie non trouvée");
                }
            } catch (NumberFormatException e) {
                // Gérer l'erreur ici, par exemple, afficher un message à l'utilisateur
            }
        }

        // Ajout des mots clés
        produit.getMotsCles().clear(); // Supprimer les mots-clés existants
        for (Long motCleId : motCleIds) {
            MotCle motCle = motCleService.getMotCle(motCleId);
            produit.getMotsCles().add(motCle);
        }

        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(Double.parseDouble(prix));
        produit.setTypePrix(typePrix);
        quantite.trim();
        minStock.trim();
        maxStock.trim();
        produit.setQuantite(Integer.parseInt(quantite));

        if (!minStock.isEmpty() && minStock != "") {
            produit.setMinStock(Integer.parseInt(minStock));
        }
        if (!maxStock.isEmpty() && maxStock != "") {
            produit.setMaxStock(Integer.parseInt(maxStock));
        }

        if (actif == null) {
            produit.setActif(false);
        } else {
            produit.setActif(actif);
        }

        if (marque != null && !marque.trim().isEmpty()) {
            Optional<Marque> marqueRes = Optional.ofNullable(marqueRepository.findById(Long.parseLong(marque)));
            if (marqueRes.isPresent()) {
                Marque marque1 = marqueRes.get();
                produit.setMarque(marque1);
            }
        }

        produit.setDateModification(LocalDateTime.now()); // Ajouter une date de modification
        produitService.updateProduit(produit); // Mettre à jour le produit

        // Ajouter le produit à l'objet Model si nécessaire
        model.addAttribute("produit", produit);

        // Rediriger l'utilisateur vers une page appropriée après la mise à jour du produit
        return "redirect:/produits/admin";
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