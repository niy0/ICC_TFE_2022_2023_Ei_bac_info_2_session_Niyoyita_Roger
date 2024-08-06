package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public String getUserCommandes(@PageableDefault(size = 5) Pageable pageable,
                                   Model model,
                                   Authentication authentication) {
        //User user = userService.getUserByEmail(userDetails.getUsername());
        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
            Page<Commande> commandes = commandeService.getCommandesByUser(user, pageable);
            model.addAttribute("commandes", commandes);
        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }
        return "commande/myList";
    }

    @PreAuthorize("hasRole('Admin') or principal.username == @commandeService.getCommandeById(#id).get().getUtilisateur().getEmail()")
    @GetMapping("/commande/detail/{id}")
    public String getCommandeById(@PathVariable Long id,
                                  Model model,
                                  Authentication authentication) {
        Optional<Commande> commande = commandeService.getCommandeById(id);

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        if (commande.isPresent()) {
            model.addAttribute("commande", commande.get());
            return "commande/show";
        } else {
            return "redirect:/commandes";
        }
    }

}
