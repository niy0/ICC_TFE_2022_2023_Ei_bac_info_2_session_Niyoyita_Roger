package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommande;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommandeDTO;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommandeData;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.LigneDeCommandeRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PanierService {

    @Autowired
    private final PanierRepository panierRepository;

    @Autowired
    private LigneDeCommandeRepository ligneDeCommandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Transactional
    public void addLigneDeCommandeToPanier(Long panierId, Long produitId, int quantite) throws ProduitNotFoundException, QuantiteInsuffisanteException {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new PanierNotFoundException("Panier non trouvé avec l'ID : " + panierId));

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ProduitNotFoundException("Produit non trouvé avec l'ID : " + produitId));

        if (panier != null && produit != null && quantite > produit.getQuantite()) {
            throw new QuantiteInsuffisanteException("Quantité insuffisante pour le produit : " + produit.getNom());
        }

        LigneDeCommande existingLigneDeCommande = ligneDeCommandeRepository.findByProduitIdAndPanierId(produitId, panierId);

        if (existingLigneDeCommande != null) {
            existingLigneDeCommande.setQuantite(existingLigneDeCommande.getQuantite() + quantite);
            ligneDeCommandeRepository.save(existingLigneDeCommande);
            System.out.println("existe deeeeeeeeejjaaa"+ existingLigneDeCommande);
        } else {
            LigneDeCommande nouvelleLigneDeCommande = new LigneDeCommande(produit, panier, quantite, produit.getPrix(), produit.getPrix() * quantite);
            ligneDeCommandeRepository.save(nouvelleLigneDeCommande);

            panier.getLignesDeCommande().add(nouvelleLigneDeCommande);

            System.out.println("nouvellle ligne rajouter*************"+panier);
        }

        panierRepository.save(panier);
    }



    public List<LigneDeCommandeDTO> getLignesDeCommande(Long panierId) {
        Panier panier = panierRepository.getPanier(panierId);
        List<LigneDeCommande> lignesDeCommande = panier.getLignesDeCommande();

        // Créer une Map pour stocker la quantité totale de chaque produit
        Map<Long, Integer> quantiteParProduit = new HashMap<>();

        // Remplir la Map en parcourant les lignes de commande
        for (LigneDeCommande ligneDeCommande : lignesDeCommande) {
            Long produitId = ligneDeCommande.getProduit().getId();
            int quantite = ligneDeCommande.getQuantite();

            // Si le produit existe déjà dans la Map, ajoutez la quantité
            if (quantiteParProduit.containsKey(produitId)) {
                int quantiteExistante = quantiteParProduit.get(produitId);
                quantiteParProduit.put(produitId, quantiteExistante + quantite);
            } else {
                // Sinon, ajoutez le produit à la Map
                quantiteParProduit.put(produitId, quantite);
            }
        }

        // Créez une liste de LigneDeCommandeDTO en mappant les entités LigneDeCommande
        List<LigneDeCommandeDTO> lignesDeCommandeDataList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : quantiteParProduit.entrySet()) {
            Long produitId = entry.getKey();
            int quantiteTotale = entry.getValue();

            // Obtenez les informations du produit à partir de la première occurrence de la ligne de commande
            LigneDeCommande premiereLigne = lignesDeCommande.stream()
                    .filter(l -> l.getProduit().getId().equals(produitId))
                    .findFirst()
                    .orElse(null);

            if (premiereLigne != null) {
                double montantTotal = quantiteTotale * premiereLigne.getProduit().getPrix();
                LigneDeCommandeDTO ligneDTO = new LigneDeCommandeDTO(
                        premiereLigne.getProduit().getNom(),
                        quantiteTotale,
                        premiereLigne.getProduit().getPrix(),
                        montantTotal,
                        premiereLigne.getPanier().getId()
                );
                lignesDeCommandeDataList.add(ligneDTO);
            }
        }

        return lignesDeCommandeDataList;
    }


    public void addToCart(LigneDeCommande ligneDeCommande) {
        // Récupérez le panier actif de l'utilisateur (vous devrez implémenter cette logique)
        //Panier panierActif = getPanierActifDeLUtilisateur(); // Implémentez cette méthode

        Panier panierActif = ligneDeCommande.getPanier(); //test

        // Assurez-vous que le panier est actif
        if (panierActif != null) {
            // Associez la ligne de commande au panier
            ligneDeCommande.setPanier(panierActif);

            // Calculer le montant total de la ligne de commande (si ce n'est pas déjà fait)
            ligneDeCommande.calculerMontantTotal();

            // Enregistrez la ligne de commande dans la base de données
            ligneDeCommandeRepository.save(ligneDeCommande);
        }
    }

    // Implémentez la logique pour récupérer le panier actif de l'utilisateur
    // Cette méthode dépendra de la structure de votre application
    private Panier getPanierActifDeLUtilisateur() {
        // Implémentez la logique pour récupérer le panier actif de l'utilisateur
        // Par exemple, vous pourriez utiliser le Spring Security pour obtenir l'utilisateur
        // et ensuite récupérer son panier actif à partir de la base de données
        // En fonction de votre application, cela peut varier.
        return null; // Remplacez par la logique de récupération réelle
    }

    public PanierService(PanierRepository panierRepository) {
        this.panierRepository = panierRepository;
    }

    // Ajoutez des méthodes pour gérer le panier, par exemple, ajouter, mettre à jour, supprimer, etc.

    public List<Panier> getAllPaniers() {
        return panierRepository.findAll();
    }

    public Panier getPanierById(Long id) {
        return panierRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Panier createPanier(Panier panier) {
        return panierRepository.save(panier);
    }

    public Panier updatePanier(Panier panier) {
        return panierRepository.save(panier);
    }

    public void deletePanier(Long id) {
        panierRepository.deleteById(id);
    }
}
