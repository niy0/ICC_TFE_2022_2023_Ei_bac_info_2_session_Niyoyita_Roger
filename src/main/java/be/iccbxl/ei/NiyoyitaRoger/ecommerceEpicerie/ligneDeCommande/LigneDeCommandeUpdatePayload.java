package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

public class LigneDeCommandeUpdatePayload {

    private Long id;

    private int quantite;

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "LigneDeCommandeUpdatePayload{" +
                "id=" + id +
                ", quantite=" + quantite +
                '}';
    }
}


