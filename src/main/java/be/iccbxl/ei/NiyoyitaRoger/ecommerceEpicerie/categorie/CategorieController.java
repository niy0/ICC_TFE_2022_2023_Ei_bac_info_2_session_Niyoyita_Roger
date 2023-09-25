package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CategorieController {
    @Autowired
    CategorieRepository categorieRepository;

    @Autowired CategorieService categorieService;

    @GetMapping("/cat/allCat")//faire un autre pour les non admin gérent/manager
    public String showAllCat(Model model) {
        List<Categorie> categorieList = categorieService.getAllCategorie();
        model.addAttribute("listCat", categorieList );
        return "/produit/categorie/index";
    }

    @GetMapping("/cat/nouveau")
    public String afficherFormulaireNouveauCategorie(Model model) {
        model.addAttribute("categorie", new Categorie());
        return "categorie-form";
    }

    @PostMapping("/cat/save/{id}")//chager le chemin
    public String createCategorie(@RequestParam("nom") String nom, @PathVariable("id") String id, RedirectAttributes redirectAttrs)  {
        String message = "";
        if(nom.length() > 2 && !categorieService.categorieExist(nom)){
            Categorie categorieToSave = new Categorie(nom) ;
            categorieService.saveCategorie(categorieToSave);
        }else {
            message = "Erreur le nom de la categorie existe déjà !" ;
        }
        redirectAttrs.addFlashAttribute("message", message);
        return "redirect:/produit/"+id+"/edit";
    }

    @PostMapping("/cat/newCat")//chager le chemin
    public String newCategorie(@RequestParam("nom") String nom, RedirectAttributes redirectAttrs)  {
        String message = "";
        if(nom.length() > 2 && !categorieService.categorieExist(nom)){
            Categorie categorieToSave = new Categorie(nom) ;
            categorieService.saveCategorie(categorieToSave);
        }else {
            message = "Erreur le nom de la categorie existe déjà !" ;
        }
        redirectAttrs.addFlashAttribute("message", message);
        return "redirect:/cat/allCat";
    }

    @PostMapping("/cat/create/newCat")//chager le chemin
    public String createNewCategorie(@RequestParam("nom") String nom, RedirectAttributes redirectAttrs)  {
        String message = "";
        if(nom.length() > 2 && !categorieService.categorieExist(nom)){
            Categorie categorieToSave = new Categorie(nom) ;
            categorieService.saveCategorie(categorieToSave);
        }else {
            message = "Erreur le nom de la categorie existe déjà !" ;
        }
        redirectAttrs.addFlashAttribute("message", message);
        return "redirect:/produit/create";
    }
}
