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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProduitController {

    private ProduitRepository produitRepository;

    private ProduitService produitService;

    private CategorieService categorieService;

    private CategorieRepository categorieRepository;

    private MotCleRepository motCleRepository;

    private MotCleService motCleService;

    private MarqueRepository marqueRepository;

    private MarqueService marqueService;

    private PanierRepository panierRepository;

    private UserRepository userRepository;

    private UserService userService;

    private PanierService panierService;

    @Autowired
    public ProduitController(ProduitRepository produitRepository,
                             ProduitService produitService,
                             CategorieService categorieService,
                             CategorieRepository categorieRepository,
                             MotCleRepository motCleRepository,
                             MotCleService motCleService,
                             MarqueRepository marqueRepository,
                             MarqueService marqueService,
                             PanierRepository panierRepository,
                             UserRepository userRepository,
                             UserService userService,
                             PanierService panierService) {
        this.produitRepository = produitRepository;
        this.produitService = produitService;
        this.categorieService = categorieService;
        this.categorieRepository = categorieRepository;
        this.motCleRepository = motCleRepository;
        this.motCleService = motCleService;
        this.marqueRepository = marqueRepository;
        this.marqueService = marqueService;
        this.panierRepository = panierRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.panierService = panierService;
    }

    @GetMapping("/")//faire un autre pour les non admin gérent/manager
    public String showIndex(Model model, Authentication authentication) {
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();

        // Vérifier si un utilisateur est authentifié
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userService.getUserByEmail(username);
                model.addAttribute("user", user);
            }
        }

        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        return "index";
    }

    @GetMapping("/produit")
    public String allProduit(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             Principal principal,
                             HttpSession session,
                             Authentication authentication) {

        Pageable pageable = PageRequest.of(page, 8);
        //Page<Produit> productPage = produitRepository.findAll(pageable);
        Page<Produit> productPage = produitRepository.findProduitsAvecQuantiteMin(pageable);
        List<Categorie> categorieList = categorieService.getAllCategorie();

        Panier panierTest = panierService.getOrCreatePanier(principal, session);
        Panier panier = panierService.getPanierById(panierTest.getId());

        // Récupération de l'utilisateur connecté
        User user = null;
        if (panier.getUtilisateur() != null) {
            user = userService.getUserById(panier.getUtilisateur().getId());
        }

        model.addAttribute("user", user);

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

    // bonne liste List<Produit> produits = produitService.getProduitsAvecQuantiteMinEtActif();

    @GetMapping("/produit/{id}")
    public String getProduitById(@PathVariable long id,
                                 Model model,
                                 Principal principal,
                                 HttpSession session,
                                 Authentication authentication) throws ProduitNotFoundException {
        Produit produit = produitRepository.getPro(id);

        Panier panierTest = panierService.getOrCreatePanier(principal, session);
        Panier panier = panierService.getPanierById(panierTest.getId());

        // Récupération de l'utilisateur connecté
        Object principalTest = authentication.getPrincipal();
        if (principalTest instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principalTest;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);

            if (produit != null) {
                model.addAttribute("produit", produit);
                model.addAttribute("panier", panier);
                return "/produit/show";
            }
        }
        return "redirect:/produits/admin";
    }

    @GetMapping("/user/favoris")
    public String showFavoris(Model model,
                              Authentication authentication) {

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
            List<Produit> listProduitFavoris = user.getProduitsFavoris();
            model.addAttribute("user", user);
            model.addAttribute("listProduitFavoris", listProduitFavoris);
            System.out.println(user);
        }

        model.addAttribute("catList", categorieList);
        model.addAttribute("marqueList", marqueList);
        model.addAttribute("motCleList", motCleList);
        return "produit/produit_favoris";
    }


    @GetMapping("/admin/produits")
    public String getAllProduits(@RequestParam(defaultValue = "nom") String sortBy,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
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

    @GetMapping("/produits/admin")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    public String showAllProducts(Model model, Authentication authentication) {
        String title = "Liste des produits";
        List<Produit> productsList = produitService.getAllProduct();
        List<Categorie> categorieList = categorieService.getAllCategorie();
        List<Marque> marqueList = marqueService.getAllMarques();
        List<MotCle> motCleList = motCleService.getAllMotsCles();


        model.addAttribute("listProducts", productsList);
        model.addAttribute("catList", categorieList);
        model.addAttribute("marqueList", marqueList);
        model.addAttribute("motcleList", motCleList);
        model.addAttribute("title", title);

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

        if (referrer != null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/produits");
        }
        model.addAttribute("title", title);
        model.addAttribute("produit", new Produit());
        model.addAttribute("catList", categorieList);
        model.addAttribute("motCleList", motCleList);
        model.addAttribute("marqueList", marqueList);
        model.addAttribute("message", message);

        return "produit/create";
    }


    @DeleteMapping("/produit/{id}")
    public String deleteProduit(@PathVariable("id") long id) {
        try {
            // Vérifiez si le produit existe avant de le supprimer
            if (produitRepository.existsById(id)) {
                List<Produit> produitList = (List<Produit>) produitRepository.findAll();
                for (Produit pro : produitList) {
                    if (pro.getId() == id) {
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

    @RequestMapping("/quantite-produit/{produitId}")
    @ResponseBody
    public int getQuantiteProduit(@PathVariable Long produitId) {
        return produitService.getQuantiteProduit(produitId);
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
            if (user != null) {
                if (user.hasRole("Admin")) {
                    model.addAttribute("user", user);
                    model.addAttribute("produit", validProduit.get());
                } else {
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

        if (referrer != null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/produit/" + validProduit.get().getId());
        }
        return "produit/admin_edit";
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
        if (description == null || description.trim().isEmpty()) {
            description = "";
        } else {
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

        if (marque != null && !marque.trim().isEmpty()) {
            Optional<Marque> marqueRes = marqueRepository.findById(Long.parseLong(marque));
            if (marqueRes.isPresent()) {
                Marque marque1 = marqueRes.get();
                produit.setMarque(marque1);
            }
        }

        produit.setDateCreation(LocalDateTime.now());
        produitService.updateProduit(produit);

        model.addAttribute("produit", produit);
        return "redirect:/produits/admin";
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
            Optional<Marque> marqueRes = marqueRepository.findById(Long.parseLong(marque));
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

    //**********API*******************

    @DeleteMapping("/api/produit/{id}/delete")
    public ResponseEntity<Map<String, Object>> apiDeleteProduit(@PathVariable("id") long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Vérifiez si le produit existe avant de le supprimer
            if (produitRepository.existsById(id)) {
                produitService.deleteProductById(id);
                response.put("message", "Le produit avec l'ID " + id + " a été supprimé avec succès.");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Produit non trouvé avec l'ID : " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (NumberFormatException e) {
            response.put("error", "L'ID " + id + " n'est pas valide.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @PostMapping("/api/product/save")
    public ResponseEntity<Map<String, Object>> createProduitAPI(
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
            @RequestParam(name = "actif", required = false) Boolean actif) throws CategorieNotFoundException {

        Produit produit = new Produit();
        Map<String, Object> response = new HashMap<>();

        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                byte[] imageBytes = baos.toByteArray();

                Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
                produit.setImagePrincipale(blob);
            }
        } catch (IOException | SQLException e) {
            response.put("error", "Erreur lors de l'upload de l'image.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (catId != null && !catId.trim().isEmpty()) {
            Optional<Categorie> cat = categorieRepository.findById(Long.parseLong(catId));
            if (cat.isPresent()) {
                produit.setCategorie(cat.get());
            } else {
                throw new CategorieNotFoundException("Catégorie non trouvée");
            }
        }

        for (Long motCleId : motCleIds) {
            MotCle motCle = motCleService.getMotCle(motCleId);
            produit.getMotsCles().add(motCle);
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

        if (marque != null && !marque.trim().isEmpty()) {
            Optional<Marque> marqueRes = marqueRepository.findById(Long.parseLong(marque));
            if (marqueRes.isPresent()) {
                produit.setMarque(marqueRes.get());
            }
        }

        produit.setDateCreation(LocalDateTime.now());
        produitService.updateProduit(produit);

        response.put("success", true);
        response.put("produit", produit);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/product/{id}/edit")
    public ResponseEntity<Map<String, Object>> updateProduitAPI(
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
            @RequestParam(name = "actif", required = false) Boolean actif) throws CategorieNotFoundException, ProduitNotFoundException {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException("Produit non trouvé avec l'ID : " + id));

        Map<String, Object> response = new HashMap<>();

        if (!image.isEmpty()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpeg", baos);
                byte[] imageBytes = baos.toByteArray();

                Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
                produit.setImagePrincipale(blob);
            } catch (IOException | SQLException e) {
                response.put("error", "Erreur lors de l'upload de l'image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        if (catId != null && !catId.trim().isEmpty()) {
            Long categoryId = Long.parseLong(catId);
            Categorie cat = categorieRepository.findById(categoryId)
                    .orElseThrow(() -> new CategorieNotFoundException("Catégorie non trouvée"));
            produit.setCategorie(cat);
        }

        produit.getMotsCles().clear();
        for (Long motCleId : motCleIds) {
            MotCle motCle = motCleService.getMotCle(motCleId);
            produit.getMotsCles().add(motCle);
        }

        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(new BigDecimal(prix));
        produit.setTypePrix(typePrix);
        produit.setQuantite(Integer.parseInt(quantite));

        if (!minStock.isEmpty()) {
            produit.setMinStock(Integer.parseInt(minStock.trim()));
        }

        if (!maxStock.isEmpty()) {
            produit.setMaxStock(Integer.parseInt(maxStock.trim()));
        }

        produit.setActif(actif != null && actif);

        if (marque != null && !marque.trim().isEmpty()) {
            Optional<Marque> marqueRes = marqueRepository.findById(Long.parseLong(marque));
            if (marqueRes.isPresent()) {
                produit.setMarque(marqueRes.get());
            }
        }

        produit.setDateModification(LocalDateTime.now());
        produitService.updateProduit(produit);

        response.put("success", true);
        response.put("produit", produit);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("produit/api/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé"));
        return ResponseEntity.ok(produit);
    }

    @GetMapping("/api/produit/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable long id) throws ProduitNotFoundException {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException("Produit non trouvé avec l'ID : " + id));

        return ResponseEntity.ok(produit);
    }

    @GetMapping("/api/admin/produits")
    @ResponseBody
    public Page<Produit> getProduits(
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
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

    @GetMapping("/api/all/produits")
    @ResponseBody
    public Page<Produit> getProduits(
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String searchQuery, // Un seul champ pour la recherche
            @RequestParam(required = false) String sortPrice,
            @RequestParam(required = false) String sortDate,
            @RequestParam(required = false) String filterCategorie,
            @RequestParam(required = false) String filterMarque,
            @RequestParam(required = false) String filterMotCle) {

        // Configuration du tri
        Sort sort = Sort.by(sortBy);
        if (sortPrice != null && !sortPrice.isEmpty()) {
            sort = Sort.by(sortPrice.equals("priceAsc") ? Sort.Order.asc("prix") : Sort.Order.desc("prix"));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            sort = Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        // Passer la recherche combinée à la méthode de service
        return produitService.getApiAllProduits(pageable, searchQuery, sortPrice, sortDate, filterCategorie, filterMarque, filterMotCle);
    }

    @GetMapping("/api/user/favoris")
    @ResponseBody
    public Page<Produit> getUserFavoris(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String searchNom,
            @RequestParam(required = false) String sortPrice,
            @RequestParam(required = false) String sortDate,
            @RequestParam(required = false) String filterCategorie,
            @RequestParam(required = false) String filterMarque,
            @RequestParam(required = false) String filterMotCle) {

        // Récupération de l'utilisateur connecté
        String username = userDetails.getUsername();  // Adaptez cette ligne selon votre UserDetails
        User user = userService.getUserByEmail(username);
        Long userId = user.getId();

        // Configuration du tri
        Sort sort = Sort.by(sortBy);
        if (sortPrice != null && !sortPrice.isEmpty()) {
            sort = Sort.by(sortPrice.equals("priceAsc") ? Sort.Order.asc("prix") : Sort.Order.desc("prix"));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            sort = Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return produitService.getUserFavoris(userId, pageable, searchNom, sortPrice, sortDate, filterCategorie, filterMarque, filterMotCle);
    }


    @GetMapping("/produits")
    public ResponseEntity<List<Produit>> getProduitsAvecQuantiteMin() {
        List<Produit> produits = produitService.getProduitsAvecQuantiteMinEtActif();
        return ResponseEntity.ok(produits);
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") String idString) throws IOException, SQLException, ProduitNotFoundException {
        long id = Long.parseLong(idString);
        List<Produit> produitList = (List<Produit>) produitRepository.findAll();

        Produit produit = produitService.getProduitById(id);

        if (produitRepository.existsById(produit.getId())) {
            byte[] imageBytes = produit.getImagePrincipale().getBytes(1, (int) produit.getImagePrincipale().length());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        }
        return ResponseEntity.noContent().build(); // Notez l'appel à .build() ici
    }

    @PostMapping("/user/add/favoris")
    public ResponseEntity<Map<String, Object>> ajouterAuxFavoris(@RequestBody Map<String, String> requestData) throws ProduitNotFoundException {
        Map<String, Object> response = new HashMap<>();

        // Vérification des données de la requête
        if (!requestData.containsKey("produitId") || !requestData.containsKey("userId")) {
            response.put("success", false);
            response.put("message", "Les IDs de produit et d'utilisateur sont requis.");
            return ResponseEntity.badRequest().body(response);
        }

        Long produitId;
        Long userId;

        try {
            produitId = Long.parseLong(requestData.get("produitId"));
            userId = Long.parseLong(requestData.get("userId"));
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "Les IDs fournis ne sont pas valides.");
            return ResponseEntity.badRequest().body(response);
        }

        // Vérification de l'existence de l'utilisateur
        Optional<User> optionalUser = Optional.ofNullable(userService.getUserById(userId));
        if (optionalUser.isEmpty()) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = optionalUser.get();

        // Vérification de l'existence du produit
        Optional<Produit> optionalProduit = Optional.ofNullable(produitService.getProduitById(produitId));
        if (optionalProduit.isEmpty()) {
            response.put("success", false);
            response.put("message", "Produit non trouvé.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Produit produit = optionalProduit.get();

        // Vérification si le produit est déjà dans les favoris
        if (user.getProduitsFavoris().contains(produit)) {
            response.put("success", true);
            response.put("message", "Produit déjà dans les favoris.");
            response.put("isAlreadyFavorited", true);
            return ResponseEntity.ok(response);
        } else {
            // Ajout du produit aux favoris de l'utilisateur
            user.getProduitsFavoris().add(produit);
            userService.save(user);
            response.put("success", true);
            response.put("message", "Produit ajouté aux favoris.");
            response.put("isAlreadyFavorited", false);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/user/remove/favoris")
    public ResponseEntity<String> removeFromFavoris(@RequestParam Long produitId, @AuthenticationPrincipal UserDetails userDetails) throws ProduitNotFoundException {
        // Récupérer l'utilisateur connecté
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        // Récupérer le produit à partir de l'ID
        Produit produit = produitService.getProduitById(produitId);
        if (produit == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit non trouvé");
        }

        // Supprimer le produit des favoris de l'utilisateur
        user.getProduitsFavoris().remove(produit);
        userService.save(user); // Sauvegarder les modifications

        return ResponseEntity.ok("Produit retiré des favoris avec succès");
    }

}