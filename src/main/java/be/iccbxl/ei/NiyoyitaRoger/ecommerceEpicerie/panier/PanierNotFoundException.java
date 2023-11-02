package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

public class PanierNotFoundException extends RuntimeException {

    public PanierNotFoundException(String panierId) {
        super("Le panier avec l'ID " + panierId + " n'a pas été trouvé.");
    }
}

