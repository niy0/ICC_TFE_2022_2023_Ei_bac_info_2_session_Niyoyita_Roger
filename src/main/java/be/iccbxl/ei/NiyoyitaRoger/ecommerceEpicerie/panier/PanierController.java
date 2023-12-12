package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommandeDTO;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommandeData;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommandeRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.CustomUserDetails;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@SessionAttributes("panier")
public class PanierController {

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private PanierService panierService;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private LigneDeCommandeRepository ligneDeCommandeRepository;

    @Autowired
    private UserRepository userRepository;

    //
    @ModelAttribute("panier")
    public Panier getPanier() {
        return new Panier();
    }

    @GetMapping("/ajouter-au-panier/{produitId}")
    public String ajouterAuPanier(@PathVariable Long produitId, @ModelAttribute("panier") Panier panier) {
        // Ajouter le produit au panier
        // par exemple: panier.ajouterProduit(produitService.trouverParId(produitId));
        return "redirect:/liste-produits";
    }

    @PostMapping("/viderPanier")
    public String viderPanier(@RequestParam("idPanier") String panierId){
        Long idPanier = Long.parseLong(panierId);
        panierService.deleteAllLigneDeCommande(idPanier);
        return "redirect:/produit";
    }

    //
    @GetMapping("/panier")
    public String showPanier(Model model, Principal principal, HttpSession session) {
        Panier panier = getOrCreatePanier(principal, session);
        model.addAttribute("panier", panier);
        return "panier/panier";
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

    //


    @GetMapping("/panier/{id}")
    public String voirPanier(Model model, @PathVariable("id")long idPanier) {
        String errorMessage = "";
        Optional<Panier> panier = panierRepository.findById(idPanier);
        if(panier.isPresent()) {
            model.addAttribute("panier",panier.get());
        }else {
            errorMessage = "Panier introuvable";
        }
        return "panier/panier";
    }

    @GetMapping("/vider-panier")
    public String viderPanier(SessionStatus status) {
        status.setComplete();
        return "redirect:/liste-produits";
    }
    //



    @GetMapping("panier/show")
    public String afficherPanier(Model model, HttpSession session) {
        // Obtenez le panier de la session
        Panier panier = (Panier) session.getAttribute("panier");

        if (panier == null) {
            panier = new Panier();
            session.setAttribute("panier", panier);
        }

        model.addAttribute("panier", panier);
        return "panier/show";
    }

    @GetMapping("/panier/lignedecommande/api/{id}")
    public ResponseEntity<LigneDeCommande> getProduitInPanierById(@PathVariable Long id) {
        // Remplacez "ligneDeCommandeRepository" par le nom de votre repository de ligne de commande
        Optional<LigneDeCommande> ligneDeCommande = ligneDeCommandeRepository.findById(id);

        if (ligneDeCommande.isPresent()) {
            return ResponseEntity.ok(ligneDeCommande.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé dans le panier");
        }
    }
    /**
    @PostMapping("/add")
    public ResponseEntity<String> addLigneDeCommandeToPanier(@RequestBody LigneDeCommandeData request) {
        try {
            // Appelez le service pour ajouter la ligne de commande au panier
            panierService.addLigneDeCommandeToPanier(request.getPanierId(), request.getProduitId(), request.getQuantite());

            return ResponseEntity.ok("Ligne de commande ajoutée avec succès.");
        } catch (PanierNotFoundException | ProduitNotFoundException | QuantiteInsuffisanteException e) {
            // Gérez les exceptions ici, par exemple, en renvoyant une réponse d'erreur appropriée
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }**/

    @GetMapping("/api/{panierId}/lignesdecommande")
    public ResponseEntity<List<LigneDeCommandeDTO>> getLignesDeCommande(@PathVariable Long panierId) {
        try {
            List<LigneDeCommandeDTO> lignesDeCommandeDataList = panierService.getLignesDeCommande(panierId);
            return ResponseEntity.ok(lignesDeCommandeDataList);
        } catch (PanierNotFoundException e) {
            // Gérer l'exception si le panier n'est pas trouvé
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Gérer d'autres exceptions génériques
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(@RequestBody LigneDeCommandeData ligneDeCommandeDTO, HttpServletRequest request) {
        try {
            // Récupérez le produit en fonction de l'ID du produit
            Produit produit = produitRepository.getPro(ligneDeCommandeDTO.getProduitId());

            // Récupérez la quantité demandée
            int quantiteDemandee = ligneDeCommandeDTO.getQuantite();

            // Vérifiez si la quantité demandée est inférieure à la quantité en stock du produit
            if (produit != null && quantiteDemandee <= produit.getQuantite() && quantiteDemandee > 0) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    for (GrantedAuthority authority : auth.getAuthorities()) {
                        System.out.println("Authority: " + authority.getAuthority());
                    }
                }

                // Vérifiez si le jeton CSRF est inclus dans les en-têtes de la requête
                String csrfHeader = request.getHeader("X-XSRF-TOKEN");
                if (csrfHeader != null) {
                    System.out.println("Jeton CSRF inclus dans les en-têtes de la requête : " + csrfHeader);
                } else {
                    System.out.println("Aucun jeton CSRF trouvé dans les en-têtes de la requête.");
                }


                // Utilisez votre service pour ajouter la ligne de commande au panier

                panierService.addLigneDeCommandeToPanier(
                        ligneDeCommandeDTO.getPanierId(),
                        ligneDeCommandeDTO.getProduitId(),
                        quantiteDemandee
                );

                int testQ1 = produit.getQuantite();

                System.out.println();

                //Diminuer la quantite ajouter dans la bd pour le produit
               // a la création d'une commande produit.setQuantite(produit.getQuantite() - quantiteDemandee);

                //Si la quantite du produit est à 0, l'enlever du site
                //à faire!!

                produitService.updateProduit(produit);
                int testQ2 = produit.getQuantite();

                System.out.println("test 1 qty : "+ testQ1 + "q dem::"+quantiteDemandee+ "*****/////******/////******////** test qty 2 : "+ testQ2);

                // La ligne de commande a été ajoutée avec succès
                return ResponseEntity.ok("Ligne de commande ajoutée avec succès.");
            } else {
                // Gérez la situation où la quantité demandée est invalide (trop élevée ou nulle)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantité demandée invalide.");
            }
        } catch (Exception e) {
            // Gérez les erreurs internes, par exemple, une erreur de base de données
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de la ligne de commande.");
        }
    }



    @GetMapping("/panier/api/{id}")
    public ResponseEntity<Panier> getPanierById(@PathVariable Long id) {
        // Remplacez "panierRepository" par le nom de votre repository de panier
        Optional<Panier> panier = panierRepository.findById(id);

        if (panier.isPresent()) {
            return ResponseEntity.ok(panier.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Panier non trouvé");
        }
    }


    @PostMapping("/addToCart2")//modifier
    public String addToCart(@RequestParam("quantite")Integer qty, @RequestParam("produitId")Long productId ) {
        // Obtenez le produit à partir de la base de données en utilisant l'ID du produit
        //Produit produit = produitService.getProduitById(productId);
        String s = "1";
        Long idTest = Long.parseLong(s);
        Optional<Panier> panier = panierRepository.findById(idTest);
        //panierRepository.save(panier.get());
        Produit produit = produitRepository.getPro(productId);

        // Créez une nouvelle ligne de commande
        //Produit produit, Panier panier, int quantite, double prixUnitaire, double montantTotal
        LigneDeCommande ligneDeCommande = new LigneDeCommande(produit,panier.get(),qty,produit.getPrix());
        ligneDeCommande.setProduit(produit);
        ligneDeCommande.setQuantite(1); // Vous pouvez ajuster la quantité comme vous le souhaitez

        // Ajoutez la ligne de commande au panier
        panierService.addToCart(ligneDeCommande);

        // Redirigez l'utilisateur vers la page du panier ou une autre page appropriée
        return "redirect:/panier/show";
    }

    /**public BigDecimal getMontantTotalPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId).orElse(null);
        if (panier != null) {
            return panier.getMontantTotalPanier();
        }
        return BigDecimal.ZERO; // Si le panier n'existe pas ou est vide, retourne BigDecimal.ZERO
    }**/


}