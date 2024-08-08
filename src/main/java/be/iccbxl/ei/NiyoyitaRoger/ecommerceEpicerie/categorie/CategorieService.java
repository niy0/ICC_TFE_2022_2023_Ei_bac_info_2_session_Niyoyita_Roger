package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {
    @Autowired
    private CategorieRepository categorieRepository;

    public Categorie saveCategorie(Categorie categorie){
        categorie.setNom(categorie.getNom().toUpperCase());
        categorieRepository.save(categorie);
        return categorie;
    }

    public List<Categorie> getAllCategorie() {
        return categorieRepository.findAll();
    }

    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    public Categorie getCategorieById(Long id) throws CategorieNotFoundException {
        Optional<Categorie> cat_result = categorieRepository.findById(id);
        Categorie categorie = null;
        if(cat_result.isPresent()) {
            return categorie = cat_result.get();
        }
        throw new CategorieNotFoundException("Pas de categorie avec ce numéro ID : "+ id);
    }

    public void deleteCategorieById(Long id) throws CategorieNotFoundException {
        Long count = categorieRepository.count();
        if(count == null || count == 0) {
            throw new CategorieNotFoundException("Categorie avec cette Id: "+ id + "n'a pas été supprimé.");
        }
        categorieRepository.deleteById(id);
    }

    public boolean categorieExist(String nom) {
        return categorieRepository.existsByNom(nom.toUpperCase());
    }
}
