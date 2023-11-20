package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.config;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user.SessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {
    @Bean
    public SessionListener sessionListenerConfig() {
        return new SessionListener();
    }
}
