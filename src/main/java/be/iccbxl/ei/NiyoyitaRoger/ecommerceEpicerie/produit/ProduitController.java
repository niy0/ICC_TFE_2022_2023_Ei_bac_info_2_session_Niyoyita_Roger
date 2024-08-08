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
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.CustomUserDetails;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PanierService panierService;

    
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
    public String allProduit(Model model, @RequestParam(defaultValue = "0") int page, Principal principal, HttpSession session) {
        Pageable pageable = PageRequest.of(page, 24); // 20 produits par page
        Page<Produit> productPage = produitRepository.findAll(pageable); // Vous devriez avoir une méthode 'findAll' qui accepte un objet 'Pageable'
        List<Categorie> categorieList = categorieService.getAllCategorie();

        Panier panier = null;

        if (principal != null && principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;

            //CustomUserDetails customUserDetails = (CustomUserDetails) principal;
            CustomUserDetails customUserDetails = (CustomUserDetails) authenticationToken.getPrincipal();
            System.out.println(customUserDetails.getEmailUser());
            User user = userRepository.findByEmail(customUserDetails.getEmailUser());
            model.addAttribute("userConnecte",user);
            System.out.println(user);

            // Récupérez ou créez le panier de l'utilisateur connecté
            panier = user.getPanier(); // Assurez-vous d'avoir une méthode getPanier() dans votre modèle User
            System.out.println("panier ::::::::::::::" + panier);
            // Récupérez le panier temporaire de la session s'il existe
            Panier panierTemporaire = (Panier) session.getAttribute("panierTemporaire");
            if (panierTemporaire != null) {
                // Fusionnez le panier temporaire avec le panier de l'utilisateur connecté
                panier.mergeWith(panierTemporaire);
                // Supprimez le panier temporaire de la session
                session.removeAttribute("panierTemporaire");
            }

        } else {
            // L'utilisateur n'est pas connecté, récupérez le panier temporaire de la session
            System.out.println(" TEst Utilisateur visiteur non connecté !!! :)");
            panier = (Panier) session.getAttribute("panierTemporaire");
            if (panier == null) {
                // Si l'utilisateur non connecté n'a pas de panier temporaire dans la session, créez-en un nouveau
                panier = new Panier();
                panierRepository.save(panier);
                // ... Initialisation du panier temporaire ...
                session.setAttribute("panierTemporaire", panier);
            }
            System.out.println(" TEst Utilisateur visiteur non connecté !!! son panier :" + panier);
        }

        model.addAttribute("listProducts", productPage.getContent());
        model.addAttribute("catList", categorieList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("panier", panier);

        return "produit/index_produits";
    }**/


    @GetMapping("/produit")
    public String allProduit(Model model, @RequestParam(defaultValue = "0") int page, Principal principal, HttpSession session) {
        Pageable pageable = PageRequest.of(page, 24);
        Page<Produit> productPage = produitRepository.findAll(pageable);
        List<Categorie> categorieList = categorieService.getAllCategorie();

        Panier panier = panierService.getOrCreatePanier(principal, session);
        System.out.println(panier);

        BigDecimal montantTotal = panier.getLignesDeCommande().stream()
                .map(ligne -> ligne.getProduit().getPrix().multiply(new BigDecimal(ligne.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("listProducts", productPage.getContent());
        model.addAttribute("catList", categorieList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("panier", panier);
        model.addAttribute("montantTotalPanier", montantTotal);

        return "produit/index_produits";
    }


    @GetMapping("/admin/produits")
    public String getAllProduits(@RequestParam(defaultValue = "nom") String sortBy,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model,
                                 Authentication authentication) {
        String title = "Liste des produits";

        // Récupération des données pour les filtres
        List<Categorie> categorieList = categorieService.getAllCategories();
        List<Marque> marqueList = marqueService.getAllMarques();
        List<MotCle> motCleList = motCleService.getAllMotsCles();

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        //Page<Produit> produitsPage = produitService.getAllProduits(pageable, sortBy);
        Page<Produit> produitsPage = produitService.getAllProduits(pageable, sortBy, null, null, null, null, null, null, null);


        model.addAttribute("produitsPage", produitsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produitsPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("catList", categorieList);
        model.addAttribute("marqueList", marqueList);
        model.addAttribute("motCleList", motCleList);
        model.addAttribute("title", title);

        return "produit/admin_produit";
    }

    @GetMapping("/api/admin/produits")
    @ResponseBody
    public Page<Produit> getProduits(
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchId,
            @RequestParam(required = false) String searchNom,
            @RequestParam(required = false) String sortPrice,
            @RequestParam(required = false) String sortDate,
            @RequestParam(required = false) String filterCategorie,
            @RequestParam(required = false) String filterMarque,
            @RequestParam(required = false) String filterMotCle) {

        Sort sort = Sort.by(sortBy);

        // Appliquez le tri en fonction des valeurs de sortPrice et sortDate
        if (sortPrice != null && !sortPrice.isEmpty()) {
            sort = Sort.by(sortPrice.equals("priceAsc") ? Sort.Order.asc("prix") : Sort.Order.desc("prix"));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            sort = Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation"));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return produitService.getAllProduits(pageable, sortBy, searchId, searchNom, sortPrice, sortDate, filterCategorie, filterMarque, filterMotCle);
    }


    @GetMapping("/produits/admin")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String showAllProducts(Model model, Authentication authentication) {
        String title = "Liste des produits";
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        model.addAttribute("title",title);

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        }

        return "produit/admin_produit";
    }

    @GetMapping("produit/api/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé"));
        return ResponseEntity.ok(produit);
    }

    @GetMapping("/produits")
    public ResponseEntity<List<Produit>> getProduitsAvecQuantiteMin() {
        List<Produit> produits = produitService.getProduitsAvecQuantiteMinEtActif();
        return ResponseEntity.ok(produits);
    }


    @RequestMapping("/quantite-produit/{produitId}")
    @ResponseBody
    public int getQuantiteProduit(@PathVariable Long produitId) {
        return produitService.getQuantiteProduit(produitId);
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
    @PreAuthorize("isAuthenticated() and hasRole('Admin')")
    public String afficherFormulaireNouveauProduit(Model model,
                                                   HttpServletRequest request,
                                                   Authentication authentication) {
        String message = "";
        String title = "Ajouter un produit";

        //Trié les listes par ordre !!
        List<Categorie> categorieList = categorieService.getAllCategorie();
        List<MotCle> motCleList = motCleService.getAllMotCle();
        List<Marque> marqueList = marqueService.getAllMarque();

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }


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

        Produit produit = new Produit();


        try {
            // Lire l'image à l'aide de ImageIO
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage != null) {
                // Convertir l'image en format JPEG
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                byte[] imageBytes = baos.toByteArray();

                // Enregistrez l'image dans l'objet Produit
                Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
                produit.setImagePrincipale(blob);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de téléchargement d'image
            model.addAttribute("error", "Erreur lors de l'upload de l'image.");
            return "produit/create";
        }

        if (catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if (cat.isPresent()) {
                produit.setCategorie(cat.get());
            } else {
                throw new CategorieNotFoundException("Catégorie non trouvée");
            }
        }

        // Ajout des mots clés
        for (Long motCleId : motCleIds) {
            MotCle motCle = motCleService.getMotCle(motCleId);
            produit.getMotsCles().add(motCle);
        }
        if(description == null || description.trim().isEmpty()){
            description = "";
        }else {
            description = description.trim();
        }

        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(new BigDecimal(prix));
        produit.setTypePrix(typePrix);

        produit.setQuantite(Integer.parseInt(quantite.trim()));

        if (!minStock.isEmpty()) {
            produit.setMinStock(Integer.parseInt(minStock.trim()));
        }

        if (!maxStock.isEmpty()) {
            produit.setMaxStock(Integer.parseInt(maxStock.trim()));
        }

        produit.setActif(actif != null && actif);

        if(marque != null && !marque.trim().isEmpty()) {
            Optional<Marque> marqueRes = Optional.ofNullable(marqueRepository.findById(Long.parseLong(marque)));
            if(marqueRes.isPresent()) {
                Marque marque1 = marqueRes.get();
                produit.setMarque(marque1);
            }
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

    @GetMapping("/admin/produit/{id}/edit")
    public String edit2(Model model,
                        @PathVariable("id") Long idStr,
                        HttpServletRequest request,
                        Authentication authentication) throws ProduitNotFoundException {

        Optional<Produit> validProduit = produitService.getProduitByIdOp(idStr);

        List<Categorie> categorieList = categorieService.getAllCategorie();
        List<MotCle> motCleList = motCleService.getAllMotCle();
        List<Marque> marqueList = marqueService.getAllMarque();

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails && validProduit.isPresent()) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);

            // Vérification des rôles et user non null
            if (user != null){
                if(user.hasRole("Admin"))  {
                    model.addAttribute("user", user);
                    model.addAttribute("produit", validProduit.get());
                }else {
                    model.addAttribute("errorMessage", "Vous n'avez pas les permissions nécessaires pour effectuer cette action.");
                    System.out.println("errorMessage , vous n'avez pas les permissions nécessaires pour effectuer cette action.");
                    return "redirect:/";
                }
            } else {
                model.addAttribute("errorMessage", "Vous n'avez pas les permissions nécessaires pour effectuer cette action.");
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }

        model.addAttribute("catList", categorieList);
        model.addAttribute("motCleList", motCleList);
        model.addAttribute("marqueList", marqueList);

        //Générer le lien retour pour l'annulation
        String referrer = request.getHeader("Referer");

        if(referrer!=null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/produit/"+validProduit.get().getId());
        }
        return "produit/admin_edit";
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
        produit.setPrix(new BigDecimal(prix));
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

        if(produitRepository.existsById(produit.getId())) {
            byte[] imageBytes = produit.getImagePrincipale().getBytes(1, (int) produit.getImagePrincipale().length());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        }
        return ResponseEntity.noContent().build(); // Notez l'appel à .build() ici
    }

}