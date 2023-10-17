package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.ligneDeCommande;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/lignedecommande")
public class LigneDeCommandeController {

    private final LigneDeCommandeService ligneDeCommandeService;

    @Autowired
    public LigneDeCommandeController(LigneDeCommandeService ligneDeCommandeService) {
        this.ligneDeCommandeService = ligneDeCommandeService;
    }

    @GetMapping("/list")
    public String listLignesDeCommande(Model model) {
        List<LigneDeCommande> lignesDeCommande = ligneDeCommandeService.getAllLignesDeCommande();
        model.addAttribute("lignesDeCommande", lignesDeCommande);
        return "panier/lignesdecommande/show";
    }

    // méthode pour ajouter une ligne de commande
    @PostMapping("/add")
    public String addLigneDeCommande(@ModelAttribute("ligneDeCommande") LigneDeCommande ligneDeCommande,
                                     RedirectAttributes redirectAttributes) {
        ligneDeCommandeService.save(ligneDeCommande);
        redirectAttributes.addFlashAttribute("successMessage", "Ligne de commande ajoutée avec succès.");
        return "redirect:/lignedecommande/list";
    }

    @GetMapping("/{id}")
    public String viewLigneDeCommande(@PathVariable Long id, Model model) {
        LigneDeCommande ligneDeCommande = ligneDeCommandeService.getLigneDeCommandeById(id);
        if (ligneDeCommande == null) {
            // Gérer le cas où la ligne de commande n'existe pas
            return "redirect:/lignedecommande/list";
        }
        model.addAttribute("ligneDeCommande", ligneDeCommande);
        return "lignesdecommande/view";
    }

    @PutMapping("/update")
    public String updateLigneDeCommande(@ModelAttribute("ligneDeCommande") LigneDeCommande ligneDeCommande,
                                        RedirectAttributes redirectAttributes) {
        try {
            ligneDeCommandeService.updateLigneDeCommande(ligneDeCommande);
            redirectAttributes.addFlashAttribute("successMessage", "Ligne de commande mise à jour avec succès.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ligne de commande non trouvée.");
        }
        return "redirect:/lignedecommande/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteLigneDeCommande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ligneDeCommandeService.deleteLigneDeCommande(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ligne de commande supprimée avec succès.");
        return "redirect:/lignedecommande/list";
    }
}
