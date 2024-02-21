package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

public class LigneDeCommandeNotFoundException extends RuntimeException{

    public LigneDeCommandeNotFoundException( String ligneDeCommandeId){
        super("La ligne de commande avec l'ID " + ligneDeCommandeId + " n'a pas été trouvé.");
    }
}