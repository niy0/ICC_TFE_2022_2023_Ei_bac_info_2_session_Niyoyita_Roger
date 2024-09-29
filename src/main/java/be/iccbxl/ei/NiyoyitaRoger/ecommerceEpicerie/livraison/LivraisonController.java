package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.livraison;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.Adresse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/livraison")
public class LivraisonController {

    @Autowired
    private LivraisonService livraisonService;

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateDeliveryFees(@RequestBody Adresse adresse) {
        try {
            // Appel de la méthode de calcul des frais de livraison
            BigDecimal fraisLivraison = livraisonService.calculerFraisLivraisonVersDestination(adresse);

            // Renvoyer la réponse avec les frais de livraison
            Map<String, Object> response = new HashMap<>();
            response.put("fraisLivraison", fraisLivraison);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du calcul des frais de livraison.");
        }
    }
}

