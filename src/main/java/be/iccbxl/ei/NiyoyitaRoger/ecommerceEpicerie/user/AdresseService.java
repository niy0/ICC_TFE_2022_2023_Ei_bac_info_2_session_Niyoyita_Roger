package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdresseService {
    private final AdresseRepository adresseRepository;

    @Autowired
    public AdresseService(AdresseRepository adresseRepository) {
        this.adresseRepository = adresseRepository;
    }

    public Optional<Adresse> getAdresseById(Long id) {
        return adresseRepository.findById(id);
    }

    public Adresse saveAdresse(Adresse adresse) {
        return adresseRepository.save(adresse);
    }

    public void deleteAdresse(Long id) {
        adresseRepository.deleteById(id);
    }

    public Adresse updateAdresse(Long id, Adresse updatedAdresse) {
        return adresseRepository.findById(id)
                .map(adresse -> {
                    adresse.setRue(updatedAdresse.getRue());
                    adresse.setNumero(updatedAdresse.getNumero());
                    adresse.setLocalite(updatedAdresse.getLocalite());
                    adresse.setCodePostal(updatedAdresse.getCodePostal());
                    adresse.setVille(updatedAdresse.getVille());
                    adresse.setPays(updatedAdresse.getPays());
                    adresse.setDepartement(updatedAdresse.getDepartement());
                    return adresseRepository.save(adresse);
                })
                .orElseThrow(() -> new EntityNotFoundException("Adresse non trouvée pour l'ID : " + id));

    }
}
