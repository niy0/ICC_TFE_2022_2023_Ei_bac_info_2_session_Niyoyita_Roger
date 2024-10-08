package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarqueService {

    @Autowired
    private MarqueRepository marqueRepository;

    public Marque save(Marque marque) {
        String nom = marque.getNom();
        String nomFormatted = nom.substring(0, 1).toUpperCase() + nom.substring(1).toLowerCase();
        marque.setNom(nomFormatted);
        marqueRepository.save(marque);
        return marque;
    }

    public List<Marque> getAllMarque() {
        List<Marque> marqueList = new ArrayList<>();
        marqueRepository.findAll().forEach(marqueList::add);
        return marqueList;
    }

    public List<Marque> getAllMarques() {
        return marqueRepository.findAll();
    }

    public Optional<Marque> getMarque(long id) {
        return marqueRepository.findById(id);
    }

    public void deleteMarqueById(Long id) throws MarqueNotFoundException {
        Long count = marqueRepository.count();
        if (count == null || count == 0) {
            throw new MarqueNotFoundException("La marque avec cette Id: " + id + "n'a pas été supprimé.");
        }
        marqueRepository.deleteById(id);
    }

    public boolean marqueExist(String nom) {
        return marqueRepository.existsByNom(nom.toUpperCase());
    }
}
