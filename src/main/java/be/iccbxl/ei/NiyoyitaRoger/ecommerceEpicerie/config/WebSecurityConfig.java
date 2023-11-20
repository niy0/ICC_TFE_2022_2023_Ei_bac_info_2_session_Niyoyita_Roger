package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.config;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.CustomUserDetailsService;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.LoginSuccessHandler;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.LogoutSuccessHandeler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

/**
@Configuration
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LoginSuccessHandler successHadeler;

    @Autowired
    private LogoutSuccessHandeler logoutSuccessHandeler;

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChainConfig(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authenticationProvider(authenticationProvider());
        httpSecurity.authorizeRequests()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/", "/signup", "/process_signup", "/produit", "/display/**","/login/test").permitAll()
                .requestMatchers("/admin/**").hasAuthority("Admin")
                .requestMatchers("/user/**").hasAuthority("User")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("email")
                .successHandler(successHadeler)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandeler)
                .permitAll();

        // CSRF activé par défaut

        return httpSecurity.build();
    }


}**/

