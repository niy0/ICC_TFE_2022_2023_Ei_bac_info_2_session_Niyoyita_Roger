package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque.Marque;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MotCleController {
    @Autowired
    private MotCleRepository motCleRepository;

    @Autowired
    private MotCleService motCleService;


    @GetMapping("/admin/motCle/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listMotCle(Model model) {
        try {
            List<MotCle> motCleList = motCleService.getAllMotCle();
            model.addAttribute("motCleList", motCleList);
            return "/motCle/index";
        } catch (AccessDeniedException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/admin/motCle/create")
    public String create(@RequestParam("nom") String nom,
                         @RequestParam("env") String env,
                         RedirectAttributes redirectAttrs) {
        String message = "";
        if (nom.length() < 2) {
            message = "Erreur le nom dois contenr minimum 2 caractères";
        }

        if (!motCleService.motCleExist(nom)) {
            MotCle motCleToSave = new MotCle(nom);
            motCleService.save(motCleToSave);
        } else {
            message = "Erreur le nom [ " + nom.toUpperCase() + " ] existe déjà, veuillez réssayé un autre.";
        }
        redirectAttrs.addFlashAttribute("messageMotCle", message);
        return "redirect:" + env;
    }

    @PutMapping("/admin/motCle/{id}/edit")
    public String update(@Valid @ModelAttribute("motCle") MotCle motCle,
                         BindingResult bindingResult,
                         @PathVariable("id") long id,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "/motCle/edit";
        }

        MotCle existing = motCleService.getMotCle(id);
        if (existing == null) {
            return "/motCle/index";
        }

        motCleService.save(motCle);
        return "redirect:/motCle/" + motCle.getId();
    }

    @DeleteMapping("/admin/motCle/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) throws MotCleNotFoundException {
        MotCle motCle = motCleService.getMotCle(id);

        if (motCle != null) {
            motCleService.deleteMotCleById(id);
            redirectAttrs.addFlashAttribute("messageMotCle", "Le mot-clé a été supprimé avec succès.");
        } else {
            redirectAttrs.addFlashAttribute("messageMotCle", "Erreur : Le mot-clé avec l'ID " + id + " n'existe pas.");
        }

        return "redirect:/admin/marque/list";
    }

}
