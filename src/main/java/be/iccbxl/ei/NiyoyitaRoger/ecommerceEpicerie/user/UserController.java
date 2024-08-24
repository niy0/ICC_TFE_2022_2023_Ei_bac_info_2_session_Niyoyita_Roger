package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.categorie.Categorie;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Controller
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserSessionService userSessionService;
    private final RoleRepository roleRepository;
    private final AdresseRepository adresseRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService,
                          CustomUserDetailsService customUserDetailsService,
                          UserSessionService userSessionService,
                          RoleRepository roleRepository,
                          AdresseRepository adresseRepository,
                          AuthenticationManager authenticationManager) {

        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.userSessionService = userSessionService;
        this.roleRepository = roleRepository;
        this.adresseRepository = adresseRepository;
        this.authenticationManager = authenticationManager;
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

    @GetMapping("/admin/add_new_user")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminNewUserForm(Model model, Authentication authentication) {

        String title = "Ajouter un utilisateur";

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        model.addAttribute("newUser", new User()); // changer le nom user par newUser
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("title", title);
        return "user/admin_create_user";
    }

    //permet pour un admin de changer de role a un user
    @PostMapping("/admin/add_new_user")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminNewUser(@ModelAttribute("newUser") User user, @RequestParam List<Long> roleIds, Model model) {
        User userTest = userService.getUserByEmail(user.getEmail());
        if (userTest != null) {
            //Ajouter message err utilisateur existe déjà avec cette e-mail
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/add_new_user";
        }

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userService.adminNewUser(user);

        return "redirect:/admin/user_list";
    }

    @GetMapping("/admin/users")
    public String getAllUsers(@RequestParam(defaultValue = "nom") String sortBy,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model,
                              Authentication authentication) {
        String title = "Admin liste users";

        // Récupération des rôles pour le filtre
        List<Role> roleList = userService.getAllRoles();

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
        Page<User> usersPage = userService.getAllUsers(pageable, sortBy, null, null, null, null, null, null);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("roleList", roleList);
        model.addAttribute("title", title);

        return "user/admin_user_list";
    }

    @GetMapping("/api/admin/users")
    @ResponseBody
    public Page<User> getUsers(
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(required = false) String searchId,
            @RequestParam(required = false) String searchNom,
            @RequestParam(required = false) String searchPrenom,
            @RequestParam(required = false) String searchEmail,
            @RequestParam(required = false) String sortRoles,
            @RequestParam(required = false) String sortDate) {

        Sort sort = Sort.by(sortBy);

        // Appliquez le tri en fonction des valeurs de sortRoles et sortDate
        if (sortRoles != null && !sortRoles.isEmpty()) {
            sort = Sort.by(Sort.Order.asc("roles.nom"));
        } else if (sortDate != null && !sortDate.isEmpty()) {
            sort = Sort.by(sortDate.equals("dateAsc") ? Sort.Order.asc("dateCreation") : Sort.Order.desc("dateCreation"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return userService.getAllUsers(pageable, sortBy, searchId, searchNom, searchPrenom, searchEmail, sortRoles, sortDate);
    }

    @GetMapping("/admin/user_list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model, Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        List<User> listUsers = userService.getAllUsers();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("title", "Liste des utilisateurs");
        return "user/admin_user_list";
    }

    @GetMapping("/admin/user/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserRoles(@PathVariable("id") Long id,
                                Model model,
                                Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        User editUser = userService.getUserById(id);
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("editUser", editUser);
        model.addAttribute("roles", roles);
        return "user/admin_update_user";
    }

    @PostMapping("/admin/user/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRoles(@RequestParam("editUserId") Long editUserId,
                                  @RequestParam("roleIds") List<Long> roleIds,
                                  Authentication authentication,
                                  Model model) {

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            model.addAttribute("user", user);
        } else {
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        userService.updateUserRoles(editUserId, roleIds);
        return "redirect:/admin/users";
    }

    @GetMapping("/user/signup")
    public String affichageInscription(Model model) {
        model.addAttribute("user", new User());
        return "user/user_signup_form";
    }

    @PostMapping("/user/signup")
    public String processRegistration(User user, RedirectAttributes redirectAttributes, Model model) {
        User userTest = userService.getUserByEmail(user.getEmail());
        if (userTest != null) {
            redirectAttributes.addFlashAttribute("message", "Login existe déjà !");
            return "redirect:/signup";
        } else {
            userService.addNewUser(user);
            model.addAttribute("user", user);
            return "redirect:/user/" + user.getId() + "/profile";
        }
    }

    //permet d'affiche la page profile de l'user
    @GetMapping("/user/{userId}/profile")
    public String getUserProfilById(Model model,
                                    @PathVariable("userId") long id,
                                    Authentication authentication) {

        User userTest = userService.getUserById(id);

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);

            if (userTest.getId() == user.getId()) {
                Adresse adresse = user.getAdresse();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String formattedDate = user.getDateCreation().format(formatter);

                model.addAttribute("formattedDateCreation", formattedDate);
                model.addAttribute("user", user);
                model.addAttribute("adresse", adresse);
                model.addAttribute("title", "Fiche d'un user");
            } else {
                return "redirect:/user/" + user.getId() + "/profile";
            }
        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        return "user/profile";
    }

    @GetMapping("/user/{userId}/profile/edit")
    public String getUpdateUser(@PathVariable("userId") long userId,
                                Model model,
                                Authentication authentication) {

        User userTest = userService.getUserById(userId);

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);

            if (user.getId() == userTest.getId()) {
                model.addAttribute("user", user);
            } else {
                return "redirect:/user/" + user.getId() + "/profile/edit";
            }
        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        return "/user/edit_profile";
    }


    @GetMapping("/user/{userId}/adresse/edit")
    public String getUpdateAdresseUser(@PathVariable("userId") long userId,
                                       Model model,
                                       Authentication authentication) {

        User userTest = userService.getUserById(userId);

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            Adresse adresse = user.getAdresse();

            System.out.println(adresse + "aaaaaaaaaaaaaaaaaaaaaddddddddddddddddddddddrrrrrrrrrrrrrrreeeeeeeeeeeessssssssssssss************");

            if (user.getId() == userTest.getId()) {
                model.addAttribute("adresse", adresse != null ? adresse : new Adresse()).addAttribute("user", user);
            } else {
                return "redirect:/user/" + user.getId() + "/adresse/edit";
            }

        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }

        return "/user/edit_adresse";
    }


    @PostMapping("/user/{userId}/adresse/save")
    public String updateAdresseUser(
            @PathVariable("userId") long userId,
            @RequestParam("rue") String rue,
            @RequestParam("numero") String numero,
            @RequestParam("localite") String localite,
            @RequestParam("codePostal") String codePostal,
            @RequestParam("ville") String ville,
            @RequestParam("pays") String pays,
            @RequestParam(value = "departement", required = false) String departement,
            Authentication authentication) {


        User userTest = userService.getUserById(userId);

        // Récupération de l'utilisateur connecté
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails && userTest != null) {
            UserDetails userDetails = (UserDetails) principal;
            String username = userDetails.getUsername();
            User user = userService.getUserByEmail(username);
            Adresse adresse = user.getAdresse();

            if (user.getId() == userTest.getId()) {
                if (adresse == null) {
                    adresse = new Adresse(localite, rue, numero, codePostal, departement, ville, pays);
                    adresse.setUtilisateur(user);
                    user.setAdresse(adresse);  // Associez la nouvelle adresse à l'utilisateur
                }
                adresse.setRue(rue);
                adresse.setNumero(numero);
                adresse.setLocalite(localite);
                adresse.setCodePostal(codePostal);
                adresse.setVille(ville);
                adresse.setPays(pays);
                adresse.setDepartement(departement);

                adresseRepository.save(adresse);
                userService.save(user);
                return "redirect:/user/" + user.getId() + "/profile";
            } else {
                return "redirect:/user/" + user.getId() + "/adresse/edit";
            }

        } else {
            // Optionally log or handle the case where principal is not a UserDetails instance
            throw new IllegalStateException("L'utilisateur connecté n'est pas une instance de UserDetails");
        }
    }


    @PostMapping("/user/{userId}/profile/edit")
    public String updateUser(@RequestParam("id") long id,
                             @RequestParam("nom") String nom,
                             @RequestParam("prenom") String prenom,
                             @RequestParam("sexe") String sexe,
                             @RequestParam("telephone") String telepone) {

        String erroMessage = "";
        User user = userService.getUserById(id);

        if (user != null) {
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setSexe(Sexe.valueOf(sexe));
            user.setTelephone(telepone);
            user.setDateModification(LocalDateTime.now());
            userService.save(user);
            return "redirect:/user/" + user.getId() + "/profile";
        } else {
            erroMessage = "Utilisateur vide";
        }

        return erroMessage;
    }

    @PutMapping("/admin/user/{userId}/profile/edit")
    public String adminUpdateUser(@PathVariable("userId") long userId, @RequestBody User updatedUser) {
        String errorMessage = userService.updateUser(userId, updatedUser);
        if (errorMessage.length() == 0) {
            return "redirect:/user/" + updatedUser.getId() + "/profile";
        } else {
            return errorMessage;
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
    

    /**
     * Cette méthode permet de récupérer les informations d'un utilisateur spécifique.
     * Elle utilise l'ID de l'utilisateur pour rechercher ses informations dans la base de données.
     *
     * @param id l'ID de l'utilisateur à récupérer.
     * @return une réponse HTTP contenant les informations de l'utilisateur ou un statut 404 si non trouvé.
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> obtenirUtilisateurParId(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }


    /**
     * Cette méthode permet de récupérer les adresses associées à un utilisateur spécifique.
     * Elle utilise l'ID de l'utilisateur pour récupérer toutes ses adresses.
     *
     * @param id l'ID de l'utilisateur dont on souhaite récupérer les adresses.
     * @return une réponse HTTP contenant la liste des adresses ou un statut 404 si l'utilisateur n'est pas trouvé.
     */
    @GetMapping("/users/{id}/addresses")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Adresse> obtenirAdresseUtilisateur(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user.getAdresse()); // Suppose que User a une méthode getAdresses()
    }

}
