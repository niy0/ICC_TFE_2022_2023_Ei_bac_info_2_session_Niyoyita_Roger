package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle.MotCle;
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

    //manque le get pour un

    @GetMapping("/cat/nouveau")
    public String afficherFormulaireNouveauCategorie(Model model) {
        model.addAttribute("categorie", new Categorie());
        return "categorie-form";
    }

    @PostMapping("/cat/save/")//chager le chemin
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
    public String create(@RequestParam("nom") String nom,
                         @RequestParam("env") String env,
                         RedirectAttributes redirectAttrs){
        String message = "";
        String err = "fa-solid fa-triangle-exclamation";
        //String err = "<i class=\"fa-solid fa-triangle-exclamation\" style=\"color: #ea4848;\"></i>";
        if (nom.length() < 2 ) {
            message = "Erreur le nom dois contenr minimum 2 caractères" ;
        }

        if( !categorieService.categorieExist(nom)){
            Categorie categorieToSave = new Categorie(nom) ;
            categorieService.saveCategorie(categorieToSave);
        }else {
            message = "Erreur le nom [ " + nom.toUpperCase() + " ] existe déjà, veuillez réssayé un autre." ;
        }
        redirectAttrs.addFlashAttribute("err", err);
        redirectAttrs.addFlashAttribute("messageCat", message);
        return "redirect:"+env;
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
