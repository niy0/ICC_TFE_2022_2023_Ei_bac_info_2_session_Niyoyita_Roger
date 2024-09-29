package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.livraison;

public class Livraison {
    // Constantes pour les frais de livraison
    private static final int FRAIS_FIXE_5KM = 500;         // 5 € pour les livraisons jusqu'à 5 km (en centimes)
    private static final double TARIF_PAR_KM = 1.5;        // 1,5 € par kilomètre au-delà de 5 km
    private static final int MAX_DISTANCE_FIXE = 5;        // Limite de 5 km pour le tarif fixe
    private static final int CONVERSION_CENTIMES = 100;    // Pour convertir en centimes

    private int frais_livraison_estimes;                   // Frais de livraison en centimes
    private int estimation_collecte;                       // Temps de collecte estimé en minutes
    private int estimation_livraison;                      // Temps de livraison estimé en minutes

    private String adresseDepart;                          // Point de départ
    private String adresseDestination;                     // Point de destination
    private double distance;                               // Distance en kilomètres

    // Getters et Setters
    public int getFraisLivraisonEstimes() {
        return frais_livraison_estimes;
    }

    public void setFraisLivraisonEstimes(int frais_livraison_estimes) {
        this.frais_livraison_estimes = frais_livraison_estimes;
    }

    public int getEstimationCollecte() {
        return estimation_collecte;
    }

    public void setEstimationCollecte(int estimation_collecte) {
        this.estimation_collecte = estimation_collecte;
    }

    public int getEstimationLivraison() {
        return estimation_livraison;
    }

    public void setEstimationLivraison(int estimation_livraison) {
        this.estimation_livraison = estimation_livraison;
    }

    public String getAdresseDepart() {
        return adresseDepart;
    }

    public void setAdresseDepart(String adresseDepart) {
        this.adresseDepart = adresseDepart;
    }

    public String getAdresseDestination() {
        return adresseDestination;
    }

    public void setAdresseDestination(String adresseDestination) {
        this.adresseDestination = adresseDestination;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    // Méthode pour calculer la distance entre l'adresse de départ et l'adresse de destination
    public void calculerDistance() {
        // Simuler une distance aléatoire entre 1 et 50 km
        this.distance = Math.random() * 50 + 1;
    }

    // Méthode pour calculer le montant des frais de livraison en fonction de la distance
    public void calculerFraisLivraison() {
        if (distance <= MAX_DISTANCE_FIXE) {
            // Montant fixe pour les distances de 5 km ou moins
            this.frais_livraison_estimes = FRAIS_FIXE_5KM;
        } else {
            // Calcul des frais pour les distances au-delà de 5 km
            double fraisSupplementaires = (distance - MAX_DISTANCE_FIXE) * TARIF_PAR_KM * CONVERSION_CENTIMES;
            this.frais_livraison_estimes = (int) (FRAIS_FIXE_5KM + fraisSupplementaires);
        }
    }

    // Affichage des informations de la livraison
    public void afficherDetailsLivraison() {
        System.out.println("Adresse de départ : " + adresseDepart);
        System.out.println("Adresse de destination : " + adresseDestination);
        System.out.println("Distance : " + distance + " km");
        System.out.println("Frais de livraison estimés : " + frais_livraison_estimes / 100.0 + " €");
    }
}


