package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Récupérer le code d'état HTTP pour déterminer le type d'erreur
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = "Une erreur est survenue erreur n° "+ statusCode;

        if(statusCode != null) {
            switch (statusCode) {
                case 404:
                    errorMessage = "Page non trouvée";
                    break;
                case 500:
                    errorMessage = "Erreur interne du serveur";
                    break;
                case 405:
                    errorMessage = "Méthode non autorisée";
                    break;
                default:
                    errorMessage = "Une erreur est survenue erreur n° " + statusCode;
                    break;
            }
        }

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);

        return "errorPage"; //"errorPage.html"
    }

}
