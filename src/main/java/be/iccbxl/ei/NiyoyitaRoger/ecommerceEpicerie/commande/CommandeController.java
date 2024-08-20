package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.User;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }
        return "commande/myList";
    }

    @GetMapping("/employe/commandes")
    public String getAllCommandes(@RequestParam(defaultValue = "dateCommande") String sortBy,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model,
                                  Authentication authentication) {
        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Commande> commandesPage = commandeService.getAllCommandes(pageable, sortBy);
        model.addAttribute("commandesPage", commandesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commandesPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        return "commande/employe_liste_commandes";
    }

    @GetMapping("/api/employe/commandes")
    @ResponseBody
    public Page<Commande> getCommandes(
            @RequestParam(defaultValue = "dateCommande") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) String searchId,
            @RequestParam(required = false) String searchVille,
            @RequestParam(required = false) String searchCodePostal,
            @RequestParam(required = false) String filterStatut,
            @RequestParam(required = false) String filterMethodCommande) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return commandeService.getAllCommandes(pageable, sortBy, searchId, searchVille, searchCodePostal, filterStatut, filterMethodCommande);
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

    //methode traiter une commande
    @PostMapping("/employe/commande/updateStatut")
    public String updateStatutCommande(@RequestParam("commandeId") Long commandeId,
                                       @RequestParam("statut") String statut,
                                       Authentication authentication,
                                       Model model) {

        Optional<Commande> commande = commandeService.getCommandeById(commandeId);

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails && commande.isPresent()) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);

            // Vérification des rôles et user non null
            if (user != null){
                if(user.hasRole("Employee") || user.hasRole("Admin"))  {
                    try {
                        // Mise à jour du statut de la commande
                        commandeService.updateStatutCommande(commandeId, statut);
                        model.addAttribute("user",user);
                        model.addAttribute("commande",commande);
                        model.addAttribute("successMessage", "Le statut de la commande a été mis à jour avec succès.");
                        return "redirect:/employe/commande/detail/"+ commandeId;
                    } catch (Exception e) {
                        model.addAttribute("errorMessage", "Une erreur est survenue lors de la mise à jour du statut de la commande.");
                    }
                }else {
                    return "redirect:/employe/commandes";
                }
            } else {
                model.addAttribute("errorMessage", "Vous n'avez pas les permissions nécessaires pour effectuer cette action.");
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
        return "redirect:/"; // Redirection vers la liste des commandes après la mise à jour
    }

    //methode pour voir une commande
    @GetMapping("/employe/commande/detail/{id}")
    public String showCommande(@PathVariable Long id,
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

            if(user.hasRole("Employee") || user.hasRole("Admin")) {
                if (commande.isPresent()) {
                    model.addAttribute("commande", commande.get());
                    return "commande/show";
                } else {
                    return "redirect:/employe/commandes";
                }
            }else {
                return "redirect:/";
            }
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }
    }
}