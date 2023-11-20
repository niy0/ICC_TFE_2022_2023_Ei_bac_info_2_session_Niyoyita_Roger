package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public void save(User user) {
        userRepository.save(user);
    }


    public List<Role> listRoles () {
        return roleRepository.findAll();
    }


    public UserDetails loadUserByEmail(String userEmail) throws UserEmailNotFoundException {
        User user = userRepository.findByEmail(userEmail);
        if(user == null) {
            throw new UserEmailNotFoundException("User avec cet login, pas trouvé");
        }

        return new CustomUserDetails(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userEmail);
        if(user == null) {
            throw new UsernameNotFoundException("User avec cet email : "+ userEmail+ " pas trouvé");
        }
        return new CustomUserDetails(user);
    }
}
