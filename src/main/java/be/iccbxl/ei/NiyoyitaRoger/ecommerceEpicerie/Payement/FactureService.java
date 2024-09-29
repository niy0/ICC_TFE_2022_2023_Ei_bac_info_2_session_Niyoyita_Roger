package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.CommandeService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.Adresse;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.AdresseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private AdresseRepository adresseRepository;

    public Facture creerFacture(
            Commande commande,
            String prenom,
            String nom,
            String rue,
            String numero,
            String localite,
            String ville,
            String codePostal,
            String departement,
            String pays
    ) {
        Facture facture = new Facture();
        facture.setCommande(commande);
        facture.setNumeroFacture("FACT-" + UUID.randomUUID().toString().substring(0, 10));

        // Générer automatiquement le numéro si nécessaire
        numero = genererNumeroAutomatique(numero);

        // Créer et sauvegarder l'adresse de facturation avec le numéro généré
        Adresse adresseFacturation = new Adresse(prenom, nom, rue, numero, localite, ville, codePostal, departement, pays);
        adresseRepository.save(adresseFacturation); // Sauvegarder l'adresse dans la base de données

        // Utilisation de la même adresse pour la livraison
        Adresse adresseLivraison = new Adresse(prenom, nom, rue, numero, localite, ville, codePostal, departement, pays);
        adresseRepository.save(adresseLivraison); // Sauvegarder l'adresse de livraison

        // Assigner les adresses à la facture
        facture.setAdresseFacturation(adresseFacturation);
        facture.setAdresseLivraison(adresseLivraison);

        // Calcul des montants (HT, TVA et TTC)
        facture.calculerMontantFacture();

        // Sauvegarder la facture
        return factureRepository.save(facture);
    }

    // Méthode pour générer un numéro valide
    public String genererNumeroAutomatique(String baseNumero) {
        if (baseNumero == null || baseNumero.isEmpty()) {
            return "NUM-" + UUID.randomUUID().toString().substring(0, 6); // Exemple de numéro auto-généré
        } else if (baseNumero.length() > 10) {
            return baseNumero.substring(0, 10); // Tronquer à 10 caractères
        }
        return baseNumero;
    }

}
