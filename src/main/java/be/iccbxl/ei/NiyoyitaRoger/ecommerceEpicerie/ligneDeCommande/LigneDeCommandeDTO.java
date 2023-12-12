package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import java.math.BigDecimal;

public class LigneDeCommandeDTO {
    private String nomProduit;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
    private Long idPanier;

    public LigneDeCommandeDTO(String nomProduit, int quantite, BigDecimal prixUnitaire, BigDecimal montantTotal, Long idPanier) {
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantTotal = montantTotal;
        this.idPanier = idPanier;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }


    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Long getIdPanier() {
        return idPanier;
    }

    public void setIdPanier(Long idPanier) {
        this.idPanier = idPanier;
    }

    @Override
    public String toString() {
        return "LigneDeCommandeDTO{" +
                "nomProduit='" + nomProduit + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", montantTotal=" + montantTotal +
                ", idPanier=" + idPanier +
                '}';
    }
}
