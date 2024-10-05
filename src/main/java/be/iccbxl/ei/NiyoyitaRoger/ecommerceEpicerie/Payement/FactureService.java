package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.Commande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.CommandeService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.Adresse;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.AdresseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private AdresseRepository adresseRepository;

    public Facture creerFacture(Commande commande) {
        Facture facture = new Facture();
        //genererNumeroAutomatique("FACT-" + UUID.randomUUID().toString().substring(0, 10));
        facture.setNumeroFacture("FACT-" + UUID.randomUUID().toString().substring(0, 10));
        facture.setCommande(commande);
        facture.setDateFacture(commande.getDateCommande());
        facture.setMontantHT(commande.getMontantCommande());
        facture.setMontantTVA(commande.getMontantCommande());
        facture.setMontantTTC(commande.getMontantCommande());

        String numero = "";
        numero = commande.getNumero();
        if (numero != null && numero.length() > 10) {
            numero = numero.substring(0, 10);
        }

        // Créer et sauvegarder l'adresse de facturation avec le numéro généré
        Adresse adresseFacturation = new Adresse(
                commande.getPrenom(),
                commande.getNom(),
                commande.getRue(),
                numero,
                commande.getLocalite(),
                commande.getVille(),
                commande.getCodePostal(),
                commande.getDepartement(),
                commande.getPays()
        );


        adresseRepository.save(adresseFacturation); // Sauvegarder l'adresse dans la base de données

        // Utilisation de la même adresse pour la livraison
        Adresse adresseLivraison = new Adresse(
                commande.getPrenom(),
                commande.getNom(),
                commande.getRue(),
                numero,
                commande.getLocalite(),
                commande.getVille(),
                commande.getCodePostal(),
                commande.getDepartement(),
                commande.getPays()
        );
        adresseRepository.save(adresseLivraison); // Sauvegarder l'adresse de livraison

        // Assigner les adresses à la facture
        facture.setAdresseFacturation(adresseFacturation);
        facture.setAdresseLivraison(adresseLivraison);
        if (commande.getUtilisateur() != null) {
            adresseLivraison.setUtilisateur(commande.getUtilisateur());  // Associer l'utilisateur si nécessaire
        }

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
