package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class CommandeController {

    private final CommandeService commandeService;
    private final UserService userService;

    @Autowired
    public CommandeController(CommandeService commandeService, UserService userService) {
        this.commandeService = commandeService;
        this.userService = userService;
    }

    @GetMapping("/commandes")
    public String getUserCommandes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<Commande> commandes = user.getCommandes() ;
        List<Commande> commandes2 = commandeService.getCommandesByUser(user);
        model.addAttribute("commandes", commandes);
        return "commande/myList";
    }

    @PreAuthorize("hasRole('ADMIN') or principal.username == @commandeService.getCommandeById(#id).get().getUtilisateur().getEmail()")
    @GetMapping("/commande/detail/{id}")
    public String getCommandeById(@PathVariable Long id, Model model) {
        Optional<Commande> commande = commandeService.getCommandeById(id);
        if (commande.isPresent()) {
            model.addAttribute("commande", commande.get());
            return "commande/show";
        } else {
            return "redirect:/commandes";
        }
    }

}
