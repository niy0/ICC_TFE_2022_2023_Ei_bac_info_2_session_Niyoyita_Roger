package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProduitsNotesService {

    private final UserProduitsNotesRepository userProduitsNotesRepository;

    @Autowired
    public UserProduitsNotesService(UserProduitsNotesRepository userProduitsNotesRepository) {
        this.userProduitsNotesRepository = userProduitsNotesRepository;
    }

    public List<Object[]> getMoyenneNotesProduits() {
        return userProduitsNotesRepository.findMoyenneNotesProduits();
    }

}
