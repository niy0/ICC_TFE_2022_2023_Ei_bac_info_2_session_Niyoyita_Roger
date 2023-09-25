package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.motCle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MotCleController {
    @Autowired
    private MotCleRepository motCleRepository;

    @Autowired
    private  MotCleService motCleService;

    @GetMapping("/motCle/all")
    public String allMotCle(Model model){
        List<MotCle> motCleList = motCleService.getAllMotCle();
        model.addAttribute("motCleList", motCleList);
        return "/motCle/index";
    }

    @GetMapping("/motCle/{id}")
    public String show(Model model, @PathVariable("id")Long id){

        MotCle motCle = motCleService.getMotCle(id);
        return "motCle/show";
    }

    @GetMapping("/motCle/create")
    public String createShow(Model model){
        MotCle motCle = new MotCle(null);
        model.addAttribute("motCle", motCle);
        return "motCle/create";
    }

    @PostMapping("/motCle/create")
    public String create(@Valid @ModelAttribute("motCle") MotCle motCle,
                         BindingResult bindingResult,
                         Model model){

        if(bindingResult.hasErrors()){
            return "motCle/create";
        }

        motCleService.save(motCle);
        return "redirect:/motCle/index";
    }

    @GetMapping("/motCle/{id}/edit")
    public String edit(Model model, @PathVariable("id")long id, HttpServletRequest request){

        MotCle motCle = motCleService.getMotCle(id);
        model.addAttribute("motCle",motCle);

        //Générer le lien retour pour l'annulation
        String referrer = request.getHeader("Referer");
        if(referrer!=null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/motCle/"+motCle.getId());
        }

        return "/motCle/edit";
    }

    @PutMapping("/motCle/{id}/edit")
    public String update(@Valid @ModelAttribute("motCle") MotCle motCle,
                         BindingResult bindingResult,
                         @PathVariable("id")long id,
                         Model model){

        if(bindingResult.hasErrors()){
            return "/motCle/edit";
        }

        MotCle existing = motCleService.getMotCle(id);
        if (existing == null) {
            return "/motCle/index";
        }

        motCleService.save(motCle);
        return "redirect:/motCle/"+motCle.getId();
    }
}
