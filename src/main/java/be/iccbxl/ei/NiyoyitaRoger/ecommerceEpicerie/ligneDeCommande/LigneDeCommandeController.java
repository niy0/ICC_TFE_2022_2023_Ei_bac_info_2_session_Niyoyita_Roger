package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/lignedecommande")
public class LigneDeCommandeController {

    @Autowired
    private LigneDeCommandeService ligneDeCommandeService;

    @Autowired
    private PanierService panierService;

    @PostMapping("/update2")
    public ResponseEntity<?> updateLigneDeCommande(@RequestBody LigneDeCommandeUpdatePayload payload) {
        System.out.println("Reçu : id = " + payload.getId() + ", quantité = " + payload.getQuantite());
        try {
            // Mise à jour de la ligne de commande avec la nouvelle quantité
            LigneDeCommande ligneDeCommande = ligneDeCommandeService.updateLigneDeCommandeQuantite(payload.getId(), payload.getQuantite());

            // Recalcul du total du panier
            ligneDeCommande.getPanier().recalculerMontantTotalPanier();

            // Recalcul du total pour la ligne de commande
            BigDecimal nouveauTotalLigne = ligneDeCommande.getMontantTotal();

            // Création d'un DTO pour renvoyer les informations mises à jour
            LigneDeCommandeDTO updatedLineDTO = new LigneDeCommandeDTO(
                    ligneDeCommande.getProduit().getNom(),
                    ligneDeCommande.getQuantite(),
                    ligneDeCommande.getProduit().getPrix(),
                    ligneDeCommande.getMontantTotal(),
                    ligneDeCommande.getPanier().getId()
            );

            // Renvoyer la réponse avec le DTO mis à jour
            return ResponseEntity.ok(updatedLineDTO);

        } catch (RuntimeException e) {
            System.err.println("Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
