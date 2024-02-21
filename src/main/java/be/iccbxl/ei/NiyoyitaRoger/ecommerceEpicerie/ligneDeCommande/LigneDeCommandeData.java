package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

// Importations nécessaires
import java.io.Serializable;

/**
 * DTO pour le transfert des données de LigneDeCommande entre le client et le serveur.
 */
public class LigneDeCommandeData implements Serializable {

    private Long produitId; // l'ID du produit sélectionné
    private Long panierId;  // l'ID du panier en cours
    private int quantite;   // la quantité demandée pour le produit

    // Constructeurs
    public LigneDeCommandeData() {
    }

    public LigneDeCommandeData(Long produitId, Long panierId, int quantite) {
        this.produitId = produitId;
        this.panierId = panierId;
        this.quantite = quantite;
    }

    // Getters et setters
    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public Long getPanierId() {
        return panierId;
    }

    public void setPanierId(Long panierId) {
        this.panierId = panierId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Vous pouvez éventuellement redéfinir toString() pour le débogage
    @Override
    public String toString() {
        return "LigneDeCommandeData{" +
                "produitId=" + produitId +
                ", panierId=" + panierId +
                ", quantite=" + quantite +
                '}';
    }
}
