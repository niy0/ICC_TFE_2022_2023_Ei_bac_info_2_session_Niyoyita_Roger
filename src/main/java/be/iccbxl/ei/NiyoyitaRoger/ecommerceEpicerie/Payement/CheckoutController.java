package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "${cors.allowedOrigins}") 
public class CheckoutController {

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        Stripe.apiKey = env.getProperty("stripe.apiKey");
    }

    @PostMapping("/create-session")
    public Map<String, String> createCheckoutSession(@Valid @RequestBody CheckoutRequest checkoutRequest) throws StripeException {
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://bf9a-2a02-2788-2b8-3ad-61a6-2f05-76f9-f26f.ngrok-free.app/checkout/success")
                .setCancelUrl("https://bf9a-2a02-2788-2b8-3ad-61a6-2f05-76f9-f26f.ngrok-free.app/checkout/cancel");

        for (CheckoutItem item : checkoutRequest.getItems()) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("eur")
                                            .setUnitAmount(item.getPrice()) // price in cents
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(item.getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .setQuantity(item.getQuantity())
                            .build()
            );
        }

        SessionCreateParams params = paramsBuilder.build();
        Session session = Session.create(params);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("id", session.getId());
        return responseData;
    }

    public static class CheckoutRequest {
        @NotNull
        private List<@Valid CheckoutItem> items;

        public List<CheckoutItem> getItems() {
            return items;
        }

        public void setItems(List<CheckoutItem> items) {
            this.items = items;
        }
    }

    public static class CheckoutItem {
        @NotBlank
        private String name;

        @Min(1)
        private long price; // price in cents

        @Min(1)
        private long quantity;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }
    }
}
