package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@Order(1)
public class AdminSecurityConfig {

    @Autowired
    private LoginSuccessHandler successHadeler;

    @Autowired
    private LogoutSuccessHandeler logoutSuccessHandeler;


    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * @Bean public PasswordEncoder passwordEncoder(){
     * return  NoOpPasswordEncoder.getInstance(); //BCryptPasswordEncoder();
     * }
     **/

    @Bean
    public PasswordEncoder passwordEncoder1() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder1());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/addToCart", "/api/checkout/**", "/user/add/favoris", "/produit",
                                "https://bf9a-2a02-2788-2b8-3ad-61a6-2f05-76f9-f26f.ngrok-free.app/**")) // Ignorer CSRF pour les endpoints OAuth2
                .authorizeHttpRequests(auth -> {
                    // Les routes accessibles à tous
                    auth.requestMatchers("/images/**", "/js/**", "/webjars/**", "/api/products", "/api/**").permitAll();
                    auth.requestMatchers("/panier/api/**", "/favicon.ico/**", "/js/manifest.json").permitAll();
                    auth.requestMatchers("/favicon.ico", "/panier/**", "/users/**", "/forgot-password/**", "/reset-password/**").permitAll();
                    auth.requestMatchers("/viderPanier/**", "/deleteElemPanier", "/commandes", "/commande/**").permitAll();
                    auth.requestMatchers("/", "/display/**", "/produit", "/produit/**", "/user/signup", "/lignedecommande/update2", "/deleteFirstElemPanier", "/auth/debug").permitAll()
                            .requestMatchers("/a-propos", "/info-contact", "/faq", "/livraison", "/retour", "/conditions", "/politique-de-confidentialite", "/addToCart").permitAll()
                            .requestMatchers("/checkout/infos_de_commande/**", "/checkout/success", "/checkout/cancel").permitAll();

                    // Les routes réservées aux admins/employés
                    auth.requestMatchers("/admin/**", "/produit/create").hasAuthority("Admin");
                    auth.requestMatchers("/user/**").hasAnyAuthority("User", "Employee", "Admin");
                    auth.requestMatchers("/employe/**").hasAnyAuthority("Employee", "Admin");

                    // Les routes pour la gestion des utilisateurs
                    auth.requestMatchers("/user/add/favoris", "/user/deleteAccount").hasAnyAuthority("User", "Employee", "Admin")
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider())
                // Configuration pour les utilisateurs normaux avec la page de login standard
                .formLogin(formLogin -> formLogin.loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(successHadeler) // Handler personnalisé pour la connexion réussie
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandeler)
                        .logoutSuccessUrl("/"));
        return httpSecurity.build();
    }
}