package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MotCleService {
    @Autowired
    private MotCleRepository motCleRepository;

    public MotCle save(MotCle motCle){
        motCle.setNom(motCle.getNom().toUpperCase());
        motCleRepository.save(motCle);
        return motCle;
    }

    public List<MotCle> getAllMotCle() {
        List<MotCle> motCleList = new ArrayList<>();
        motCleRepository.findAll().forEach(motCleList::add);
        return motCleList;
    }

    public List<MotCle> getAllMotsCles() {
        return motCleRepository.findAll();
    }

    public MotCle getMotCle(long id) {
        return motCleRepository.findById(id);
    }

    public void deleteMotCleById(Long id) throws MotCleNotFoundException {
        Long count = motCleRepository.count();
        if(count == null || count == 0) {
            throw new MotCleNotFoundException("La marque avec cette Id: "+ id + "n'a pas été supprimé.");
        }
        motCleRepository.deleteById(id);
    }

    public boolean motCleExist(String nom) {
        return motCleRepository.existsByNom(nom.toUpperCase());
    }
}
