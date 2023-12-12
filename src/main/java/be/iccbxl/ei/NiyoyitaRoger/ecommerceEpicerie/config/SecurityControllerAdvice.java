package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.config;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SecurityControllerAdvice {

    @ModelAttribute("isAnonymousUser")
    public boolean isAnonymousUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && "anonymousUser".equals(authentication.getName());
    }
}

