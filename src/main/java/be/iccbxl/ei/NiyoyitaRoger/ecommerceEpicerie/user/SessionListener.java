package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanierRepository panierRepository;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // La session a été créée, vous pouvez effectuer des opérations si nécessaire
        System.out.println("Sessssssssssssssssion aaaaaaaaa etttttttteeeeeeeee crééeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee**********");
        // Récupérez l'utilisateur connecté depuis le contexte de sécurité Spring
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext != null && securityContext.getAuthentication() != null) {
            Object principal = securityContext.getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;

                // Créez un nouveau panier pour la session
                Panier panierTemporaire = new Panier();
                // Assurez-vous d'initialiser le panier au besoin
                panierRepository.save(panierTemporaire);

                // Associez le panier à l'utilisateur
                user.setPanier(panierTemporaire);

                // Assurez-vous de sauvegarder les modifications de l'utilisateur dans votre repository ou service
                userRepository.save(user);
            }
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // La session a été détruite (l'utilisateur s'est déconnecté ou la session a expiré)
        // Ici, vous pouvez supprimer le panier de l'utilisateur connecté s'il en a un

        // Récupérez le contexte de sécurité Spring pour obtenir l'utilisateur connecté
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            Object principal = securityContext.getAuthentication().getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;

                // Ici, vous pouvez supprimer le panier de l'utilisateur (s'il existe) ou effectuer d'autres opérations nécessaires
                if (user.getPanier() != null) {
                    Panier panierToDelete = user.getPanier();
                    user.setPanier(null);
                    // Assurez-vous de sauvegarder les modifications de l'utilisateur dans votre repository ou service
                    userRepository.save(user);
                    //Suppresion du panier dans la bd
                    panierRepository.delete(panierToDelete);
                }
            }
        }
    }

}
