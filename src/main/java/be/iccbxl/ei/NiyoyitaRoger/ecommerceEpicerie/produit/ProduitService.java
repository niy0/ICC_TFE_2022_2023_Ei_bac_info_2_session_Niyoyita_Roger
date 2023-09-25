package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired
    ProduitRepository produitRepository;


    public Produit saveProduct(Produit produit) {
        produit.setNom(produit.capitalize(produit.getNom()));
        produit.setMarque(produit.getMarque().toUpperCase());
        if(produit.getMotsCles() != null)
            produit.getMotsCles().forEach(mot-> mot.toUpperCase());

        produitRepository.save(produit);
        return produit;
    }

    public List<Produit> getAllProduct() {
        return produitRepository.findAll();
    }

    public List<Produit> getAllProduct(String keyword) {
        if(keyword != null) {
            return produitRepository.getAllProduct(keyword);
        }else {
            return (List<Produit>) produitRepository.findAll();
        }
    }

    public Produit getProductById(String id) throws ProduitNotFoundException {
        Long idValid = Long.parseLong(id);
        Optional<Produit> produit_result = produitRepository.findById(idValid);
        Produit produit = null;
        if(produit_result.isPresent()) {
            return produit = produit_result.get();
        }
        throw new ProduitNotFoundException("Pas de produit avec ce numéron ID : "+ id);
    }

    public void deleteProductById(Long id) throws ProduitNotFoundException {
        Long count = produitRepository.count();
        if(count == null || count == 0) {
            throw new ProduitNotFoundException("Produit avec cette Id: "+ id + "n'a pas été supprimé.");
        }
        produitRepository.deleteById(id);
    }

    public void updateProduct(Long id, Produit produit) {
        produitRepository.save(produit);
    }
}
