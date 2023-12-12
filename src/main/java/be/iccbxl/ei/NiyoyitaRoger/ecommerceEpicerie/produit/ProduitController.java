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
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.CustomUserDetails;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private UserRepository userRepository;

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
    /**
    @GetMapping("/produit")
    public String allProduit(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 24); // 20 produits par page
        Page<Produit> productPage = produitRepository.findAll(pageable); // Vous devriez avoir une méthode 'findAll' qui accepte un objet 'Pageable'
        List<Categorie> categorieList = categorieService.getAllCategorie();

        //panier
       String s = "1";
       Long idTest = Long.parseLong(s);
        Optional<Panier> panier = panierRepository.findById(idTest);
        //Panier panier = new Panier();
        panierRepository.save(panier.get());

        System.out.println(panier.get().getId()+"******************************************************");

        model.addAttribute("panier",panier.get());


        model.addAttribute("listProducts", productPage.getContent());
        model.addAttribute("catList", categorieList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "produit/index_produits";
    }**/

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

    //*******************
    @GetMapping("/produit")
    public String allProduit(Model model, @RequestParam(defaultValue = "0") int page, Principal principal, HttpSession session) {
        Pageable pageable = PageRequest.of(page, 24);
        Page<Produit> productPage = produitRepository.findAll(pageable);
        List<Categorie> categorieList = categorieService.getAllCategorie();

        Panier panier = getOrCreatePanier(principal, session);

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

    private Panier getOrCreatePanier(Principal principal, HttpSession session) {
        if (principal != null) {
            return getOrCreateAuthenticatedUserPanier(principal, session);
        } else {
            return getOrCreateSessionPanier(session);
        }
    }

    private Panier getOrCreateAuthenticatedUserPanier(Principal principal, HttpSession session) {
        CustomUserDetails customUserDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        User user = userRepository.findByEmail(customUserDetails.getEmailUser());
        Panier panier = user.getPanier();

        Panier panierTemporaire = (Panier) session.getAttribute("panierTemporaire");
        if (panierTemporaire != null) {
            panier.mergeWith(panierTemporaire);
            session.removeAttribute("panierTemporaire");
        }

        return panier;
    }

    private Panier getOrCreateSessionPanier(HttpSession session) {
        Long panierId = (Long) session.getAttribute("panierTemporaireId");
        Panier panier;

        if (panierId == null) {
            panier = new Panier();
            panierRepository.save(panier); // Assurez-vous que le panier est persisté pour obtenir un ID
            session.setAttribute("panierTemporaireId", panier.getId());
        } else {
            panier = panierRepository.findById(panierId)
                    .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        }

        return panier;
    }

    //*********************


    @GetMapping("/produit2")
    public String allProduit2(Model model, @RequestParam(defaultValue = "0") int page, Principal principal, HttpSession session) {
        Pageable pageable = PageRequest.of(page, 24); // 20 produits par page
        Page<Produit> productPage = produitRepository.findAll(pageable);
        List<Categorie> categorieList = categorieService.getAllCategorie();

        Panier panier = null;

        if (principal != null) {
            System.out.println("laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa*****************************");
            // L'utilisateur est connecté, utilisez Spring Security pour obtenir l'utilisateur connecté
            // Vous devrez peut-être ajuster votre configuration Spring Security
           // User user = (User) ((Authentication) principal).getPrincipal();
            // L'utilisateur est connecté, utilisez Spring Security pour obtenir l'utilisateur connecté
            CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();

            System.out.println(userDetails.getUser(userDetails.getEmailUser())+"laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            // Récupérez ou créez le panier de l'utilisateur connecté
            //panier = user.getPanier(); // Assurez-vous d'avoir une méthode getPanier() dans votre modèle User
            panier = userDetails.getUser(userDetails.getEmailUser()).getPanier(); // Assurez-vous que CustomUserDetails contient une référence à User

        } else {
            // L'utilisateur n'est pas connecté, récupérez le panier temporaire de la session
            panier = (Panier) session.getAttribute("panierTemporaire");
            if (panier == null) {
                // Si l'utilisateur non connecté n'a pas de panier temporaire dans la session, créez-en un nouveau
                panier = new Panier();
                // ... Initialisation du panier temporaire ...
                session.setAttribute("panierTemporaire", panier);
            }
        }

        model.addAttribute("listProducts", productPage.getContent());
        model.addAttribute("catList", categorieList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("panier", panier);

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

    //a tester
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
        produit.setPrix(new BigDecimal(prix));
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
        //System.out.println("************test"+ produit+ "****** id="+ id);
        if(produitRepository.existsById(produit.getId())) {
            byte[] imageBytes = produit.getImagePrincipale().getBytes(1, (int) produit.getImagePrincipale().length());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        }
        return ResponseEntity.noContent().build(); // Notez l'appel à .build() ici
    }
}