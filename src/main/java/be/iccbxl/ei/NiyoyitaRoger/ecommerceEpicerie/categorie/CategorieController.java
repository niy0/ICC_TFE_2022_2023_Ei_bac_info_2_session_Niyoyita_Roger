package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CategorieController {
    @Autowired
    CategorieRepository categorieRepository;

    @Autowired
    CategorieService categorieService;

    @GetMapping("/admin/cat/list")//faire un autre pour les non admin gérent/manager
    public String showAllCat(Model model) {
        List<Categorie> categorieList = categorieService.getAllCategorie();
        model.addAttribute("listCat", categorieList);
        return "/produit/categorie/index";
    }


    @PostMapping("/admin/cat/save/")//chager le chemin
    public String createCategorie(@RequestParam("nom") String nom, @PathVariable("id") String id, RedirectAttributes redirectAttrs) {
        String message = "";
        if (nom.length() > 2 && !categorieService.categorieExist(nom)) {
            Categorie categorieToSave = new Categorie(nom);
            categorieService.saveCategorie(categorieToSave);
        } else {
            message = "Erreur le nom de la categorie existe déjà !";
        }
        redirectAttrs.addFlashAttribute("message", message);
        return "redirect:/produit/" + id + "/edit";
    }

    @PostMapping("/admin/cat/newCat")//chager le chemin
    public String create(@RequestParam("nom") String nom,
                         @RequestParam("env") String env,
                         RedirectAttributes redirectAttrs) {
        String message = "";
        String err = "fa-solid fa-triangle-exclamation";
        //String err = "<i class=\"fa-solid fa-triangle-exclamation\" style=\"color: #ea4848;\"></i>";
        if (nom.length() < 2) {
            message = "Erreur le nom dois contenr minimum 2 caractères";
        }

        if (!categorieService.categorieExist(nom)) {
            Categorie categorieToSave = new Categorie(nom);
            categorieService.saveCategorie(categorieToSave);
        } else {
            message = "Erreur le nom [ " + nom.toUpperCase() + " ] existe déjà, veuillez réssayé un autre.";
        }
        redirectAttrs.addFlashAttribute("err", err);
        redirectAttrs.addFlashAttribute("messageCat", message);
        return "redirect:" + env;
    }


    @DeleteMapping("/admin/cat/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) throws CategorieNotFoundException {
        Categorie categorie = categorieService.getCategorieById(id);

        if (categorie != null) {
            categorieService.deleteCategorieById(id);
            redirectAttrs.addFlashAttribute("messageCat", "La catégorie a été supprimée avec succès.");
        } else {
            redirectAttrs.addFlashAttribute("messageCat", "Erreur : La catégorie avec l'ID " + id + " n'existe pas.");
        }

        return "redirect:/admin/cat/list";
    }
}
