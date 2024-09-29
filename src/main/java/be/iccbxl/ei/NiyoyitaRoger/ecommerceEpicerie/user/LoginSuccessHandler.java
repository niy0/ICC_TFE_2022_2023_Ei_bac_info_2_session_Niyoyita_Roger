package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.Panier;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private PanierRepository panierRepository;

    private SessionListener sessionListener;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (authentication != null) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            System.out.println("Nom :" + customUserDetails.getFullName());
            System.out.println("roles:" + customUserDetails.getAuthorities());
            String redirecturl = request.getContextPath();

            if (customUserDetails.hasRole("Admin")) {
                redirecturl += "/user/" + customUserDetails.getId() + "/profile";
                System.out.println("je suis connecté");
            } else if (customUserDetails.hasRole("User")) {
                redirecturl += "/user/" + customUserDetails.getId() + "/profile";
            } else if (customUserDetails.hasRole("Employee")) {
                redirecturl += "/user/" + customUserDetails.getId() + "/profile";
            } else {
                redirecturl += "/";
            }
            response.sendRedirect(redirecturl);
        } else {
            System.out.println("Authentication failed");
            // handle failed authentication here
        }
    }

}
