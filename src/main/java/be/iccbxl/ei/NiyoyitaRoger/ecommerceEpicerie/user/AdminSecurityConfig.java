package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


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
    public SecurityFilterChain filterChain1(HttpSecurity httpSecurity) throws Exception {

        // .requestMatchers("/user/**").access("hasAuthority('User') or hasAuthority('Admin')")
        //AbstractHttpConfigurer::disable
        httpSecurity.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/addToCart", "/api/checkout/**", "/user/add/favoris", "/produit",
                                "https://bf9a-2a02-2788-2b8-3ad-61a6-2f05-76f9-f26f.ngrok-free.app/**"))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/images/**", "/js/**", "/webjars/**", "/api/**", "/panier/api/**", "/favicon.ico/**", "/js/manifest.json").permitAll();
                    auth.requestMatchers("/favicon.ico", "/panier/**", "/viderPanier/**", "/deleteElemPanier", "/commandes", "/commande/**").permitAll();
                    auth.requestMatchers("/", "/display/**", "/produit", "/user/signup", "/lignedecommande/**", "/deleteFirstElemPanier", "/auth/debug").permitAll() //changer nouveau produit
                            .requestMatchers("/a-propos", "/info-contact", "/faq", "/livraison", "/retour", "/conditions", "/politique-de-confidentialite", "/addToCart").permitAll()
                            .requestMatchers("/checkout/infos_de_commande/**", "/checkout/success", "/checkout/cancel").permitAll()
                            .requestMatchers("/admin/**", "/produit/create").hasAuthority("Admin")
                            .requestMatchers("/user/**").hasAnyAuthority("User", "Employee", "Admin")
                            .requestMatchers("/employee/**").hasAnyAuthority("Employee", "Admin")
                            .requestMatchers("/user/add/favoris").hasAnyAuthority("User", "Employee", "Admin") // Ajout de l'autorisation pour cette URL
                            .anyRequest().authenticated();
                })
                //.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider())
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //.ignoringRequestMatchers("/addToCart")
                // Autres configurations CSRF ici si nécessaire
                .formLogin(formLogin -> formLogin.loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(successHadeler)
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandeler)
                        .logoutSuccessUrl("/"));
        return httpSecurity.build();
    }
}
