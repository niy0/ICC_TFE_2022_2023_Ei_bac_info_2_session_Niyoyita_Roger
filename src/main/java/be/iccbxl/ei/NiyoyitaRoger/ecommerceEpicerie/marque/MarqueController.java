package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.marque;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/marque/all")
    public String listMarque(Model model){
        List<Marque> marqueList = marqueService.getAllMarque();
        model.addAttribute("marqueList", marqueList);
        return "/marque/index";
    }

    @GetMapping("/marque/{id}")
    public String show(Model model, @PathVariable("id")Long id){
        Optional<Marque> marque = marqueRepository.findById(id);
        if(marque.isPresent()){
            model.addAttribute("marque", marque);
            return "/marque/show";
        }

        return "redirect:/index";
    }

    @GetMapping("/marque/create")
    public String create(Model model){
        Marque marque = new Marque(null);
        model.addAttribute("marque", marque);
        return "marque/create";
    }

    @PostMapping("/marque/create")
    public String create(@RequestParam("nom") String nom,
                         @RequestParam("env") String env,
                         RedirectAttributes redirectAttrs){
        String message = "";
        if (nom.length() < 2 ) {
            message = "Erreur le nom dois contenr minimum 2 caractères" ;
        }

        if(!marqueService.marqueExist(nom)){
            Marque marqueToSave = new Marque(nom);
            marqueService.save(marqueToSave);
        }else {
            message = "Erreur le nom [ " + nom.toUpperCase() + " ] existe déjà, veuillez réssayé un autre." ;
        }
        redirectAttrs.addFlashAttribute("messageMarque", message);
        return "redirect:"+env;
    }

    @GetMapping("/marque/{id}/edit")
    public String edit(Model model, @PathVariable("id")Long id, HttpServletRequest request){
        Optional<Marque> marque = marqueRepository.findById(id);
        if(marque.isPresent()){
            model.addAttribute("marque", marque);

            //Générer le lien retour pour l'annulation
            String referrer = request.getHeader("Referer");
            if(referrer!=null && !referrer.equals("")) {
                model.addAttribute("back", referrer);
            } else {
                model.addAttribute("back", "/marque/"+marque.get().getId());
            }

            return "/marque/edit";
        }
        return "/index";
    }

    @PutMapping("/marque/{id}/edit")
    public String update(@PathVariable("id")long id, Model model){
        Optional<Marque> marque = Optional.ofNullable(marqueRepository.findById(id));
        if(marque.isPresent()){
            model.addAttribute("marque", marque);
            marqueService.save(marqueRepository.findById(id));
            return "/marque/edit";
        }
        return "redirect:/marque/{id}/edit";
    }
}
