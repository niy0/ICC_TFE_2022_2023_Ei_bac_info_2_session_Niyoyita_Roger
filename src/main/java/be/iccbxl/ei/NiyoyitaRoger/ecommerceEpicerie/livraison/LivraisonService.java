package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.livraison;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.Adresse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.math.BigDecimal;

@Service
public class LivraisonService {

    // Récupérer la clé d'API depuis application.properties
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    // URL de l'API Google Maps Distance Matrix
    private static final String GOOGLE_MAPS_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    // Constante pour l'adresse du magasin
    private static final String MAGASIN_ADRESSE = "117 Rue de Paris, 16000 Angoulême, France";

    // Méthode pour calculer les frais de livraison
    public BigDecimal calculerFraisLivraison(Commande commande) {
        RestTemplate restTemplate = new RestTemplate();

        // Adresse du client
        String clientAdresse = commande.getRue() + " " + commande.getNumero() + ", " +
                commande.getVille() + ", " + commande.getCodePostal() + ", " + commande.getPays();


        String url = GOOGLE_MAPS_DISTANCE_MATRIX_URL + "?origins=" + MAGASIN_ADRESSE + "&destinations=" + clientAdresse + "&key=" + googleMapsApiKey;

        //System.out.println("URL envoyée à l'API Google Distance Matrix: " + url);

        try {
            // Affichage de l'URL pour diagnostic
            System.out.println("URL envoyée à l'API Google Distance Matrix: " + url);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            // Vérification de la réponse API
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                // Affichage de la réponse API pour diagnostic
                System.out.println("Réponse de l'API Google Distance Matrix: " + responseBody);

                JSONObject jsonObject = new JSONObject(responseBody);

                if (jsonObject.has("rows") && jsonObject.getJSONArray("rows").length() > 0) {
                    JSONObject row = jsonObject.getJSONArray("rows").getJSONObject(0);

                    if (row.has("elements") && row.getJSONArray("elements").length() > 0) {
                        JSONObject element = row.getJSONArray("elements").getJSONObject(0);

                        if (element.has("distance")) {
                            int distanceInMeters = element.getJSONObject("distance").getInt("value");

                            double distanceInKilometers = distanceInMeters / 1000.0;

                            Livraison livraison = new Livraison();
                            livraison.setDistance(distanceInKilometers);
                            livraison.calculerFraisLivraison();

                            return BigDecimal.valueOf(livraison.getFraisLivraisonEstimes()).divide(BigDecimal.valueOf(100));
                        } else {
                            throw new RuntimeException("Distance non trouvée dans la réponse de l'API.");
                        }
                    } else {
                        throw new RuntimeException("Aucun élément trouvé dans la réponse de l'API.");
                    }
                } else {
                    throw new RuntimeException("Aucune ligne trouvée dans la réponse de l'API.");
                }
            } else {
                throw new RuntimeException("Erreur lors de l'appel à l'API Google Maps.");
            }
        } catch (Exception e) {
            // Affichage de l'erreur pour diagnostic
            System.err.println("Erreur lors de l'appel à l'API Google: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'estimation des frais de livraison.", e);
        }
    }

    public BigDecimal calculerFraisLivraisonVersDestination(Adresse adresseDestination) {
        RestTemplate restTemplate = new RestTemplate();

        // Adresse fixe du magasin
        final String MAGASIN_ADRESSE = "117 Rue de Paris, 16000 Angoulême, France";

        // Construction de l'adresse de destination
        String clientAdresse = adresseDestination.getRue() + " " + adresseDestination.getNumero() + ", " +
                adresseDestination.getVille() + ", " + adresseDestination.getCodePostal() + ", " +
                adresseDestination.getPays();

        // Construction de l'URL pour l'appel à l'API Google Distance Matrix
        String url = GOOGLE_MAPS_DISTANCE_MATRIX_URL + "?origins=" + MAGASIN_ADRESSE + "&destinations=" + clientAdresse + "&key=" + googleMapsApiKey;

        try {
            // Affichage de l'URL pour diagnostic
            System.out.println("URL envoyée à l'API Google Distance Matrix: " + url);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            // Vérification de la réponse API
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                // Affichage de la réponse API pour diagnostic
                System.out.println("Réponse de l'API Google Distance Matrix: " + responseBody);

                JSONObject jsonObject = new JSONObject(responseBody);

                if (jsonObject.has("rows") && jsonObject.getJSONArray("rows").length() > 0) {
                    JSONObject row = jsonObject.getJSONArray("rows").getJSONObject(0);

                    if (row.has("elements") && row.getJSONArray("elements").length() > 0) {
                        JSONObject element = row.getJSONArray("elements").getJSONObject(0);

                        if (element.has("distance")) {
                            int distanceInMeters = element.getJSONObject("distance").getInt("value");

                            double distanceInKilometers = distanceInMeters / 1000.0;

                            Livraison livraison = new Livraison();
                            livraison.setDistance(distanceInKilometers);
                            livraison.calculerFraisLivraison();

                            return BigDecimal.valueOf(livraison.getFraisLivraisonEstimes()).divide(BigDecimal.valueOf(100));
                        } else {
                            throw new RuntimeException("Distance non trouvée dans la réponse de l'API.");
                        }
                    } else {
                        throw new RuntimeException("Aucun élément trouvé dans la réponse de l'API.");
                    }
                } else {
                    throw new RuntimeException("Aucune ligne trouvée dans la réponse de l'API.");
                }
            } else {
                throw new RuntimeException("Erreur lors de l'appel à l'API Google Maps.");
            }
        } catch (Exception e) {
            // Affichage de l'erreur pour diagnostic
            System.err.println("Erreur lors de l'appel à l'API Google: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'estimation des frais de livraison.", e);
        }
    }

}

