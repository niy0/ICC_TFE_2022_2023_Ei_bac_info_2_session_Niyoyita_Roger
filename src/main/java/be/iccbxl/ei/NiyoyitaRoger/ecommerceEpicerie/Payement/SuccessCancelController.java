package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuccessCancelController {

    @GetMapping("/checkout/success")
    public String success() {
        return "stripe/success";
    }

    @GetMapping("/checkout/cancel")
    public String cancel() {
        return "stripe/cancel";
    }
}
