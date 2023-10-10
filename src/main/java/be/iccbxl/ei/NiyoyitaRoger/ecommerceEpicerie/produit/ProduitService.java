package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.produit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Produit> getAllProduct() {
        return (List<Produit>) produitRepository.findAll();
    }

    public List<Produit> getAllProduct(String keyword) {
        if(keyword != null) {
            return produitRepository.getAllProduct(keyword);
        } else {
            return (List<Produit>) produitRepository.findAll();
        }
    }

    public void updateProduit(Produit produit){
        produitRepository.save(produit);
    }

    public Produit getProduitById(Long id) throws ProduitNotFoundException {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException("Produit non trouvé avec l'ID : " + id));
    }

    public void deleteProductById(Long id) {
        produitRepository.deleteById(id);
    }
}

