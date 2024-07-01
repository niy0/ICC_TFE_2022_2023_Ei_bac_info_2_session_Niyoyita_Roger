package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**@Bean
    public PasswordEncoder passwordEncoder(){
        return  NoOpPasswordEncoder.getInstance(); //BCryptPasswordEncoder();
    }**/

    @Bean
    public PasswordEncoder passwordEncoder1(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder1());
        return authenticationProvider;
    }

    /**
    @Bean
    //authentication
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("Basant")
                .password(encoder.encode("Pwd1"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("John")
                .password(encoder.encode("Pwd2"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
        return new UserInfoUserDetailsService();
    }**/



    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity httpSecurity) throws Exception {

        // .requestMatchers("/user/**").access("hasAuthority('User') or hasAuthority('Admin')")
        //AbstractHttpConfigurer::disable
        httpSecurity.csrf( csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/addToCart"))
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers("/images/**","/js/**","/webjars/**","/api/**","/panier/api/**","/favicon.ico/**").permitAll();
                    auth.requestMatchers("/favicon.ico","/panier/**","/viderPanier/**","/deleteElemPanier").permitAll();
                    auth.requestMatchers("/","/signup","/display/**", "/produit","/produit/**","/user/signup","/produit/nouveau","/auth/debug").permitAll() //changer nouveau produit
                    .requestMatchers("/info-contact","/info-livraison","/info-retour","/info-utilisation","/info-confidentialite","/addToCart").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("Admin")
                            .requestMatchers("/user/**").hasAnyAuthority("User", "Employee", "Admin")
                                .requestMatchers("/employee/**").hasAnyAuthority( "Employee", "Admin")
                                    .anyRequest().authenticated();
                })
                //.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider())
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //.ignoringRequestMatchers("/addToCart")
                // Autres configurations CSRF ici si nécessaire
                .formLogin( formLogin -> formLogin.loginPage("/login/test")
                        .usernameParameter("email")
                        .successHandler(successHadeler)
                        .permitAll())
                .logout( logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandeler)
                        .logoutSuccessUrl("/"));
        return httpSecurity.build();
    }
}
