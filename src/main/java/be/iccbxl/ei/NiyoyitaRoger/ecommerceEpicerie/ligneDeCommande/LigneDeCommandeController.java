package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.dto.ServiceResponse;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/lignedecommande")
public class LigneDeCommandeController {

    private final LigneDeCommandeService ligneDeCommandeService;
    private final ProduitRepository produitRepository;
    private final PanierRepository panierRepository;

    @Autowired
    public LigneDeCommandeController(LigneDeCommandeService ligneDeCommandeService,
                                     ProduitRepository produitRepository,
                                     PanierRepository panierRepository) {
        this.ligneDeCommandeService = ligneDeCommandeService;
        this.produitRepository = produitRepository;
        this.panierRepository = panierRepository;
    }


    // méthode pour ajouter une ligne de commande
    @PostMapping("/add88")
    public String addLigneDeCommande(@ModelAttribute("ligneDeCommande") LigneDeCommande ligneDeCommande,
                                     RedirectAttributes redirectAttributes) {
        ligneDeCommandeService.save(ligneDeCommande);
        redirectAttributes.addFlashAttribute("successMessage", "Ligne de commande ajoutée avec succès.");
        return "redirect:/lignedecommande/list";
    }

    public ResponseEntity<LigneDeCommande> addLigneDeCommandeToPanier(@RequestBody LigneDeCommandeData ligneDeCommandeData) {
        // Récupérez les données de la DTO
        Long produitId = ligneDeCommandeData.getProduitId();
        int quantite = ligneDeCommandeData.getQuantite();

        // Récupérez le panier actuel de l'utilisateur en utilisant l'ID du panier
        Panier panier = panierRepository.getPanier(ligneDeCommandeData.getPanierId());

        // Récupérez le produit en fonction de l'ID du produit (produitId) depuis votre base de données
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit non trouvé"));

        // Vérification du nombre de stock produit
        if (quantite <= produit.getQuantite()) {
            // Créez une nouvelle instance de LigneDeCommande
            LigneDeCommande nouvelleLigneDeCommande = new LigneDeCommande(produit, panier, quantite, produit.getPrix(), quantite * produit.getPrix());

            // Enregistrez la nouvelle ligne de commande dans votre base de données
            ligneDeCommandeService.save(nouvelleLigneDeCommande);

            // Ajoutez la nouvelle ligne de commande au panier
            panier.addLigneDeCommande(nouvelleLigneDeCommande);

            // Enregistrez le panier mis à jour dans votre base de données
            panierRepository.save(panier);

            return ResponseEntity.ok(nouvelleLigneDeCommande);
        } else {
            // Gérez la situation où la quantité demandée est supérieure à la quantité en stock
            // Vous pouvez envoyer une réponse appropriée ou lever une exception personnalisée ici
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Par exemple
        }
    }


    @PostMapping("/add")
    public String add(
            @RequestParam("produitId") Long produitId,
            @RequestParam("panierId") Long panierId,
            @RequestParam("quantite") int quantite) {

        String message = "";
        Produit produit = produitRepository.getPro(produitId);
        Panier panier = panierRepository.getPanier(panierId);

        if (produit == null || panier == null) {
            // Gérez le cas où le produit ou le panier n'est pas trouvé, par exemple, en renvoyant une réponse d'erreur.
            return message = ("Produit ou panier non trouvé.");
        }

        LigneDeCommande ligneDeCommande = new LigneDeCommande();
        ligneDeCommande.setProduit(produit);
        ligneDeCommande.setPanier(panier);
        ligneDeCommande.setQuantite(quantite);

        ligneDeCommandeService.save(ligneDeCommande);
        panier.getLignesDeCommande().add(ligneDeCommande);
        panierRepository.save(panier);

        return message;
    }

    @PostMapping("/add3")
    public ResponseEntity<?> addLigneDeCommande(
            @RequestParam("produitId") Long produitId,
            @RequestParam("panierId") Long panierId,
            @RequestParam("quantite") int quantite) {

        try {
            Produit produit = produitRepository.findById(produitId).orElse(null);
            Panier panier = panierRepository.findById(panierId).orElse(null);

            if (produit == null || panier == null) {
                // Produit ou panier non trouvé, retourner une réponse d'erreur
                return new ResponseEntity<>("Produit ou panier non trouvé.", HttpStatus.NOT_FOUND);
            }

            LigneDeCommande ligneDeCommande = new LigneDeCommande();
            ligneDeCommande.setProduit(produit);
            ligneDeCommande.setPanier(panier);
            ligneDeCommande.setQuantite(quantite);

            // Supposons que "save" renvoie l'entité enregistrée
            LigneDeCommande createdLigneDeCommande = ligneDeCommandeService.save(ligneDeCommande);

            // Créer une réponse de succès avec l'entité enregistrée
            return ResponseEntity.ok(createdLigneDeCommande);
        } catch (Exception e) {
            // Gérer les exceptions globales (par exemple, une erreur d'accès à la base de données)
            return new ResponseEntity<>("Une erreur est survenue lors de l'ajout de la ligne de commande.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/add5")
    public ResponseEntity<String> createLigneDeCommande(
            @RequestParam("produitId") Long produitId,
            @RequestParam("panierId") Long panierId,
            @RequestParam("quantite") int quantite,
            RedirectAttributes redirectAttrs) {

        try {
            Produit produit = produitRepository.findById(produitId).orElse(null);
            Panier panier = panierRepository.findById(panierId).orElse(null);

            System.out.println(produit+"******--------**********"+panier+"+++++++++***********");

            if (produit == null) {
                return ResponseEntity.badRequest().body("Produit non trouvé.");
            }

            if (panier == null) {
                return ResponseEntity.badRequest().body("Panier non trouvé.");
            }

            LigneDeCommande nouvelleLigne = new LigneDeCommande(
                    produit, panier, quantite, produit.getPrix(), panier.getMontantTotalPanier()
            );

            ligneDeCommandeService.save(nouvelleLigne);
            panier.getLignesDeCommande().add(nouvelleLigne);
            panierRepository.save(panier);

            return ResponseEntity.ok("Ligne de commande ajoutée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout de la ligne de commande.");
        }
    }


    @GetMapping("/{id}")
    public String viewLigneDeCommande(@PathVariable Long id, Model model) {
        LigneDeCommande ligneDeCommande = ligneDeCommandeService.getLigneDeCommandeById(id);
        if (ligneDeCommande == null) {
            // Gérer le cas où la ligne de commande n'existe pas
            return "redirect:/lignedecommande/list";
        }
        model.addAttribute("ligneDeCommande", ligneDeCommande);
        return "lignesdecommande/view";
    }

    @PutMapping("/update")
    public String updateLigneDeCommande(@ModelAttribute("ligneDeCommande") LigneDeCommande ligneDeCommande,
                                        RedirectAttributes redirectAttributes) {
        try {
            ligneDeCommandeService.updateLigneDeCommande(ligneDeCommande);
            redirectAttributes.addFlashAttribute("successMessage", "Ligne de commande mise à jour avec succès.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ligne de commande non trouvée.");
        }
        return "redirect:/lignedecommande/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteLigneDeCommande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ligneDeCommandeService.deleteLigneDeCommande(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ligne de commande supprimée avec succès.");
        return "redirect:/lignedecommande/list";
    }
}
