package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande.*;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.Produit;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit.ProduitRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserEmailNotFoundException;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PanierService {

    private final PanierRepository panierRepository;

    private LigneDeCommandeRepository ligneDeCommandeRepository;
    private LigneDeCommandeService ligneDeCommandeService;

    private ProduitRepository produitRepository;

    private UserRepository userRepository;

    // Durée d'inactivité après laquelle les paniers sont considérés comme non utilisés (en minutes)
    private static final long DELAI_INACTIVITE = 60; // par exemple, 1 heure

    @Autowired
    public PanierService(PanierRepository panierRepository,
                         LigneDeCommandeRepository ligneDeCommandeRepository,
                         LigneDeCommandeService ligneDeCommandeService,
                         ProduitRepository produitRepository,
                         UserRepository userRepository) {

        this.panierRepository = panierRepository;
        this.ligneDeCommandeRepository = ligneDeCommandeRepository;
        this.ligneDeCommandeService = ligneDeCommandeService;
        this.produitRepository = produitRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 3600000) // Exécute cette méthode toutes les heures
    public void nettoyerPaniersInactifs() {
        LocalDateTime seuil = LocalDateTime.now().minusMinutes(DELAI_INACTIVITE);
        List<Panier> paniersInactifs = panierRepository.findByDateModificationBefore(seuil);

        for (Panier panier : paniersInactifs) {
            if (panier.getUtilisateur() == null) { // Vérifiez s'il s'agit d'un panier de visiteur non enregistré
                panierRepository.delete(panier);
            }
        }
    }

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
        } else {
            LigneDeCommande nouvelleLigneDeCommande = new LigneDeCommande(produit, panier, quantite, produit.getPrix());
            ligneDeCommandeRepository.save(nouvelleLigneDeCommande);

            panier.getLignesDeCommande().add(nouvelleLigneDeCommande);
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
                BigDecimal montantTotal = BigDecimal.valueOf(quantiteTotale).multiply(premiereLigne.getProduit().getPrix());
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


    // Ajoutez des méthodes pour gérer le panier, par exemple, ajouter, mettre à jour, supprimer, etc.

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

    @Transactional
    public void deleteAllLigneDeCommande(Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new PanierNotFoundException("Panier non trouvé avec l'ID : " + panierId));

        if (!panier.getLignesDeCommande().isEmpty()) {
            for (LigneDeCommande ligneDeCommande : panier.getLignesDeCommande()) {
                ligneDeCommandeRepository.delete(ligneDeCommande);
            }
            panier.getLignesDeCommande().clear();
            panierRepository.save(panier);
        }
    }

    public boolean deleteLigneDeCommandePanier(Long idPanier, Long idLigneDeCommande) {
        Panier panier = panierRepository.findById(idPanier)
                .orElseThrow(() -> new PanierNotFoundException("Panier non trouvé avec l'ID : " + idPanier));

        LigneDeCommande ligneDeCommande = ligneDeCommandeRepository.findById(idLigneDeCommande)
                .orElseThrow(() -> new LigneDeCommandeNotFoundException("Ligne de commande non trouvée avec l'ID : " + idLigneDeCommande));

        if (panier.getLignesDeCommande().contains(ligneDeCommande)) {
            panier.getLignesDeCommande().remove(ligneDeCommande);

            ligneDeCommande.setPanier(null); // Dissociez la ligne de commande du panier si nécessaire
            ligneDeCommandeRepository.save(ligneDeCommande);

            //test suppression de ligne de commande
            ligneDeCommandeService.deleteLigneDeCommande(idLigneDeCommande);

            panierRepository.save(panier);
            return true;
        }
        return false;
    }

    public boolean deleteFirstElemLigneDeCommandePanier(Long idPanier, Long idLigneDeCommande) {
        Panier panier = panierRepository.findById(idPanier)
                .orElseThrow(() -> new PanierNotFoundException("Panier non trouvé avec l'ID : " + idPanier));

        LigneDeCommande ligneDeCommande = ligneDeCommandeRepository.findById(idLigneDeCommande)
                .orElseThrow(() -> new LigneDeCommandeNotFoundException("Ligne de commande non trouvée avec l'ID : " + idLigneDeCommande));

        if (panier.getLignesDeCommande().contains(ligneDeCommande)) {
            panier.getLignesDeCommande().remove(0);

            ligneDeCommande.setPanier(null); // Dissociez la ligne de commande du panier si nécessaire
            ligneDeCommandeRepository.save(ligneDeCommande);

            //test suppression de ligne de commande
            ligneDeCommandeService.deleteLigneDeCommande(idLigneDeCommande);

            panierRepository.save(panier);
            return true;
        }
        return false;
    }

    public Panier getOrCreatePanier(Principal principal, HttpSession session) {
        if (principal != null) {
            return getOrCreateAuthenticatedUserPanier(principal, session);
        } else {
            return getOrCreateSessionPanier(session);
        }
    }

    public Panier getOrCreateAuthenticatedUserPanier(Principal principal, HttpSession session) {
        // Logique pour récupérer ou créer un panier pour un utilisateur authentifié
        User user = userRepository.findByEmail(principal.getName());
        if (user.getPanier() == null) {
            Panier panier = new Panier();
            panier.setUtilisateur(user);
            this.createPanier(panier);//a été modifier
            user.setPanier(panier);
            userRepository.save(user);
        }
        return user.getPanier();
    }

    public Panier getOrCreateSessionPanier(HttpSession session) {
        // Logique pour récupérer ou créer un panier pour un utilisateur non authentifié
        Panier panier = (Panier) session.getAttribute("panier");
        if (panier == null) {
            panier = new Panier();
            panierRepository.save(panier);
            session.setAttribute("panier", panier);
        }
        return panier;
    }
}
