package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
/**
@Configuration
public class DatabaseLoader {

    private UserRepository userRepository;

    public DatabaseLoader(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Bean
    public CommandLineRunner initializeDatabase() {
        return args -> {
          User user1 = new User("user1","user1","user1@mail.com","123456789",Sexe.HOMME);

          user1.setDateCreation(LocalDateTime.now());

          User user2 = new User("user2","user2","user2@mail.com","123456789",Sexe.HOMME);

          user2.setDateCreation(LocalDateTime.now());

          User user3 = new User("user3","user3","user3@mail.com","123456789",Sexe.HOMME);
          //user3.getRoles().add(new Role("Employe"));
          user3.setDateCreation(LocalDateTime.now());

          User user4 = new User("user4","user4","user4@mail.com","123456789",Sexe.HOMME);
          //user4.getRoles().add(new Role("Gerant"));
          user4.setDateCreation(LocalDateTime.now());

          userRepository.saveAll(List.of(user1,user2,user3,user4));

            //user1.getRoles().add(new Role("Admin"));
            //user2.getRoles().add(new Role("User"));
          System.out.println("Ajout dans la bd test");
        };
    }
}**/
