package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class MarqueController {

    @Autowired
    private MarqueRepository marqueRepository;

    @Autowired
    private MarqueService marqueService;

    @GetMapping("/admin/marque/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listMarque(Model model) {
        try {
            List<Marque> marqueList = marqueService.getAllMarque();
            model.addAttribute("marqueList", marqueList);
            return "/marque/index";
        } catch (AccessDeniedException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/admin/marque/create")
    public String create(@RequestParam("nom") String nom,
                         @RequestParam("env") String env,
                         RedirectAttributes redirectAttrs) {
        String message = "";
        if (nom.length() < 2) {
            message = "Erreur le nom dois contenr minimum 2 caractères";
        }

        if (!marqueService.marqueExist(nom)) {
            Marque marqueToSave = new Marque(nom);
            marqueService.save(marqueToSave);
        } else {
            message = "Erreur le nom [ " + nom.toUpperCase() + " ] existe déjà, veuillez réssayé un autre.";
        }
        redirectAttrs.addFlashAttribute("messageMarque", message);
        return "redirect:" + env;
    }

    @PutMapping("/admin/marque/{id}/edit")
    public String update(@PathVariable("id") long id, Model model) {
        Optional<Marque> marque = marqueRepository.findById(id);
        if (marque.isPresent()) {
            model.addAttribute("marque", marque);
            marqueService.save(marque.get());
            return "/marque/edit";
        }
        return "redirect:/marque/{id}/edit";
    }

    @DeleteMapping("/admin/marque/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) throws MarqueNotFoundException {
        Optional<Marque> marqueOptional = marqueRepository.findById(id);

        if (marqueOptional.isPresent()) {
            marqueService.deleteMarqueById(id);
            redirectAttrs.addFlashAttribute("messageMarque", "La marque a été supprimée avec succès.");
        } else {
            redirectAttrs.addFlashAttribute("messageMarque", "Erreur : La marque avec l'ID " + id + " n'existe pas.");
        }

        return "redirect:/admin/marque/list";
    }

}