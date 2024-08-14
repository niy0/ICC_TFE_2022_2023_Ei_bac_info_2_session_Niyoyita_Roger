package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.Payement;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.commande.CommandeService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "${cors.allowedOrigins}")
public class CheckoutController {

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @Autowired
    private CommandeService commandeService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = env.getProperty("stripe.apiKey");
    }

    @PostMapping("/create-session")
    public Map<String, String> createCheckoutSession(@Valid @RequestBody CheckoutRequest checkoutRequest) throws StripeException {
        try {
            // Ajout de logs pour vérifier les valeurs
            System.out.println("Order Method: " + checkoutRequest.getOrderInfo().getOrderMethod());
            System.out.println("First Name: " + checkoutRequest.getOrderInfo().getFirstName());
            System.out.println("Last Name: " + checkoutRequest.getOrderInfo().getLastName());
            System.out.println("Email: " + checkoutRequest.getOrderInfo().getEmail());
            System.out.println("Rue: " + checkoutRequest.getOrderInfo().getRue());
            System.out.println("Numero: " + checkoutRequest.getOrderInfo().getNumero());
            System.out.println("Localite: " + checkoutRequest.getOrderInfo().getLocalite());
            System.out.println("Ville: " + checkoutRequest.getOrderInfo().getVille());
            System.out.println("Code Postal: " + checkoutRequest.getOrderInfo().getCodePostal());
            System.out.println("Departement: " + checkoutRequest.getOrderInfo().getDepartement());
            System.out.println("Pays: " + checkoutRequest.getOrderInfo().getPays());
            System.out.println("Montant Commande: " + checkoutRequest.getOrderInfo().getMontantCommande());
            System.out.println("Id panier : " + checkoutRequest.getOrderInfo().getIdPanierStripe());

            String successUrl = String.format(
                    "http://localhost:8080/checkout/success?session_id={CHECKOUT_SESSION_ID}&methodCommande=%s&prenom=%s&nom=%s&email=%s&rue=%s&numero=%s&localite=%s&ville=%s&codePostal=%s&departement=%s&pays=%s&montantCommande=%s&idPanierStripe=%s",
                    encode(checkoutRequest.getOrderInfo().getOrderMethod()),
                    encode(checkoutRequest.getOrderInfo().getFirstName()),
                    encode(checkoutRequest.getOrderInfo().getLastName()),
                    encode(checkoutRequest.getOrderInfo().getEmail()),
                    encode(checkoutRequest.getOrderInfo().getRue()),
                    encode(checkoutRequest.getOrderInfo().getNumero()),
                    encode(checkoutRequest.getOrderInfo().getLocalite()),
                    encode(checkoutRequest.getOrderInfo().getVille()),
                    encode(checkoutRequest.getOrderInfo().getCodePostal()),
                    encode(checkoutRequest.getOrderInfo().getDepartement()),
                    encode(checkoutRequest.getOrderInfo().getPays()),
                    encode(checkoutRequest.getOrderInfo().getMontantCommande().toString()),
                    encode(String.valueOf(checkoutRequest.getOrderInfo().getIdPanierStripe())) // Ajout de ce paramètre
            );

            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl("http://localhost:8080/checkout/cancel");

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
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode URL parameters", e);
        }
    }

    private String encode(String value) throws UnsupportedEncodingException {
        return value == null ? "" : URLEncoder.encode(value, "UTF-8");
    }

    public static class CheckoutRequest {
        @NotNull
        private List<@Valid CheckoutItem> items;

        @NotNull
        private OrderInfo orderInfo;

        public List<CheckoutItem> getItems() {
            return items;
        }

        public void setItems(List<CheckoutItem> items) {
            this.items = items;
        }

        public OrderInfo getOrderInfo() {
            return orderInfo;
        }

        public void setOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
        }

        @Override
        public String toString() {
            return "CheckoutRequest{" +
                    "items=" + items +
                    ", orderInfo=" + orderInfo +
                    '}';
        }

        public static class OrderInfo {
            @NotBlank
            private String firstName;
            @NotBlank
            private String lastName;
            @NotBlank
            private String email;
            @NotBlank
            private String rue;
            @NotBlank
            private String numero;
            private String localite;
            @NotBlank
            private String ville;
            @NotBlank
            private String codePostal;
            private String departement;
            @NotBlank
            private String pays;
            @NotBlank
            private String orderMethod;
            @NotNull
            private Long idPanierStripe;
            @NotNull
            private BigDecimal montantCommande;

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getLastName() {
                return lastName;
            }

            public void setLastName(String lastName) {
                this.lastName = lastName;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getRue() {
                return rue;
            }

            public void setRue(String rue) {
                this.rue = rue;
            }

            public String getNumero() {
                return numero;
            }

            public void setNumero(String numero) {
                this.numero = numero;
            }

            public String getLocalite() {
                return localite;
            }

            public void setLocalite(String localite) {
                this.localite = localite;
            }

            public String getVille() {
                return ville;
            }

            public void setVille(String ville) {
                this.ville = ville;
            }

            public String getCodePostal() {
                return codePostal;
            }

            public void setCodePostal(String codePostal) {
                this.codePostal = codePostal;
            }

            public String getDepartement() {
                return departement;
            }

            public void setDepartement(String departement) {
                this.departement = departement;
            }

            public String getPays() {
                return pays;
            }

            public void setPays(String pays) {
                this.pays = pays;
            }

            public String getOrderMethod() {
                return orderMethod;
            }

            public void setOrderMethod(String orderMethod) {
                this.orderMethod = orderMethod;
            }

            public Long getIdPanierStripe() {
                return idPanierStripe;
            }

            public void setIdPanierStripe(Long idPanierStripe) {
                this.idPanierStripe = idPanierStripe;
            }

            public BigDecimal getMontantCommande() {
                return montantCommande;
            }

            public void setMontantCommande(BigDecimal montantCommande) {
                this.montantCommande = montantCommande;
            }

            @Override
            public String toString() {
                return "OrderInfo{" +
                        "firstName='" + firstName + '\'' +
                        "lastName='" + lastName + '\'' +
                        "email='" + email + '\'' +
                        "rue='" + rue + '\'' +
                        "numero='" + numero + '\'' +
                        "localite='" + localite + '\'' +
                        "ville='" + ville + '\'' +
                        "codePostal='" + codePostal + '\'' +
                        "departement='" + departement + '\'' +
                        "pays='" + pays + '\'' +
                        "orderMethod='" + orderMethod + '\'' +
                        "idPanierStripe=" + idPanierStripe +
                        "montantCommande=" + montantCommande +
                        '}';
            }
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

        @Override
        public String toString() {
            return "CheckoutItem{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}
