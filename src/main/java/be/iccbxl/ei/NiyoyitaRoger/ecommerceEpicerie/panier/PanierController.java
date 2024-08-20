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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.commons.lang3.math.NumberUtils;

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


    @ModelAttribute("panier")
    public Panier getPanier() {
        return new Panier();
    }

    @PostMapping("/viderPanier")
    public String viderPanier(@RequestParam("idPanier") String panierId){
        Long idPanier = Long.parseLong(panierId);
        panierService.deleteAllLigneDeCommande(idPanier);
        return "redirect:/produit";
    }


    //A tester pour user non connecter
    @GetMapping("/panier")
    public String showPanier(Model model, Principal principal, HttpSession session) {
       // Panier panier = getOrCreatePanier(principal, session);
        Panier panierTest = panierService.getOrCreatePanier(principal, session);
        Panier panier = panierService.getPanierById(panierTest.getId());
        System.out.println(panier);

        System.out.println(principal+" pppppprincipal");

        BigDecimal montantTotal = panier.getLignesDeCommande().stream()
                .map(ligne -> ligne.getProduit().getPrix().multiply(new BigDecimal(ligne.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("panier", panier);
        model.addAttribute("montantTotalPanier", montantTotal);

        return "panier/panier";
    }

    @GetMapping("/panier/api/{id}")
    public ResponseEntity<Panier> getPanierById(Model model,@PathVariable("id") Long id) {
        // Remplacez "panierRepository" par le nom de votre repository de panier
        Optional<Panier> panier = panierRepository.findById(id);

        if (panier.isPresent()) {
            return ResponseEntity.ok(panier.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Panier non trouvé");
        }
    }


    @PostMapping("/deleteElemPanier")
    public String deleteElementInPanier(@RequestParam("idPanier") String idPanier,
                                        @RequestParam("idLigneDeCommande") String idLigneDeCommande) {

        System.out.println("idPanier : "+idPanier+ " :: ligne co:"+ idLigneDeCommande);

        if (NumberUtils.isDigits(idPanier) && NumberUtils.isDigits(idLigneDeCommande)) {
            Long panierId = Long.parseLong(idPanier);
            Long ligneDeCommandeId = Long.parseLong(idLigneDeCommande);

            panierService.deleteLigneDeCommandePanier(panierId, ligneDeCommandeId);
            return "redirect:/panier";
        } else {
            // Gérer les erreurs de paramètres invalides ici
            return "redirect:/errorPage"; // Rediriger vers une page d'erreur spécifique
        }
    }

    @PostMapping("/deleteFirstElemPanier")
    public String deleteFirstElementInPanier(@RequestParam("idPanier") String idPanier,
                                        @RequestParam("idLigneDeCommande") String idLigneDeCommande) {

        System.out.println("idPanier : "+idPanier+ " :: ligne co:"+ idLigneDeCommande);

        if (NumberUtils.isDigits(idPanier) && NumberUtils.isDigits(idLigneDeCommande)) {
            Long panierId = Long.parseLong(idPanier);
            Long ligneDeCommandeId = Long.parseLong(idLigneDeCommande);

            panierService.deleteFirstElemLigneDeCommandePanier(panierId, ligneDeCommandeId);
            return "redirect:/panier";
        } else {
            // Gérer les erreurs de paramètres invalides ici
            return "redirect:/errorPage"; // Rediriger vers une page d'erreur spécifique
        }
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

            Panier panier = panierService.getPanierById(ligneDeCommandeDTO.getPanierId());
            System.out.println(panier.getId() + "je suis laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + quantiteDemandee + ligneDeCommandeDTO.getPanierId());

            // Vérifiez si la quantité demandée est inférieure à la quantité en stock du produit
            if (produit != null && quantiteDemandee <= produit.getQuantite() && quantiteDemandee > 0) {

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    for (GrantedAuthority authority : auth.getAuthorities()) {
                        System.out.println("Authority: " + authority.getAuthority());
                        System.out.println(panier.getLignesDeCommande().size());
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

                System.out.println(panier.getLignesDeCommande().size() + "siiiiiiiiizzzzzzzzzzzeee");

                int testQ1 = produit.getQuantite();

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
            e.printStackTrace(); // Journalise la pile d'appel complète de l'exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de la ligne de commande: " + e.getMessage());
        }
    }

}
