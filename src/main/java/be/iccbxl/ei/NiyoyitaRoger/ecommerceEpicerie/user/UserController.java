package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserSessionService userSessionService;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController( UserService userService,
                           CustomUserDetailsService customUserDetailsService,
                           UserSessionService userSessionService,
                           RoleRepository roleRepository) {

        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.userSessionService = userSessionService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/auth/debug")
    @ResponseBody
    public String debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return "Utilisateur connecté : " + auth.getName();
        } else {
            return "Utilisateur non connecté";
        }
    }

    //permet pour un admin de changer de role a un user
    @PostMapping("/updateUserRole")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userID, @RequestParam String newRole) {
        User userToChangeRole = userService.getUserById(userID);
        if (userToChangeRole == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        Role userNewRole = roleRepository.findByNom(newRole);
        if (userNewRole == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rôle non trouvé");
        }

        // Vérifier si l'utilisateur a déjà ce rôle pour éviter les doublons
        if (userToChangeRole.getRoles().contains(userNewRole)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'utilisateur a déjà ce rôle");
        }

        userToChangeRole.getRoles().add(userNewRole);
        userService.save(userToChangeRole);

        // Ensuite, après avoir mis à jour les autorisations dans la base de données
        userSessionService.clearUserCache(userToChangeRole.getEmail());

        return ResponseEntity.ok("Rôle de l'utilisateur mis à jour avec succès.");
    }


    @GetMapping("/user/signup")
    public String affichageInscription(Model model){
        model.addAttribute("user", new User());
        return "user/user_signup_form";
    }

    @PostMapping("/user/signup")
    public String processRegistration(User user, RedirectAttributes redirectAttributes, Model model) {
        User userTest = userService.getUserByEmail(user.getEmail());
        if (userTest != null) {
            System.out.println(userTest+"ttttttttttttttt");
            //model.addAttribute("currentUser", user);
            redirectAttributes.addFlashAttribute("message", "Login existe déjà !");
            return "redirect:/signup";
        } else {
            userService.addNewUser(user);
            model.addAttribute("user",user);
            return "redirect:/user/"+user.getId()+"/profile";
        }
    }

    //permet d'affiche la page profile de l'user
    @GetMapping("/user/{userId}/profile")
    public String getUserProfilById(Model model, @PathVariable("userId") long id) {
        User user = userService.getUserById(id);
        Adresse adresse = user.getAdresse();

        model.addAttribute("user", user);
        model.addAttribute("adresse", adresse);
        model.addAttribute("title", "Fiche d'un user");

        return "user/profile";
    }

    @GetMapping("/user/{userId}/profile/edit")
    public String getUpdateUser(@PathVariable("userId") long userId,Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "/user/edit";
    }

    @PostMapping("/user/{userId}/profile/edit")
    public String updateUser(@RequestParam("id") long id,
                             @RequestParam("nom") String nom,
                             @RequestParam("prenom") String prenom,
                             @RequestParam("email") String email,
                             @RequestParam("sexe") String sexe,
                             @RequestParam("telephone") String telepone) {

        String erroMessage ="";
        User user = userService.getUserById(id);

        if(user != null) {
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setEmail(email);
            user.setSexe(Sexe.valueOf(sexe));
            user.setTelephone(telepone);
            user.setDateModification(LocalDateTime.now());
            userService.save(user);
            return "redirect:/user/" + user.getId()+ "/profile";
        }else {
            erroMessage = "Utilisateur vide";
        }

        return erroMessage;
    }

    @PutMapping("/admin/user/{userId}/profile/edit")
    public String adminUpdateUser(@PathVariable("userId") long userId, @RequestBody User updatedUser) {
        String errorMessage = userService.updateUser(userId,updatedUser);
        System.out.println(errorMessage + " : meeeeeesssssssaaaaaaage");
        if(errorMessage.length() == 0) {
            return "redirect:/user/" + updatedUser.getId()+ "/profile";
        }else {
            return errorMessage;
        }
    }


    //permet pour l'admin d'afficher tous les users
    @GetMapping("admin/users")
    public String getAllUsers() {
        return "";
    }


    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
