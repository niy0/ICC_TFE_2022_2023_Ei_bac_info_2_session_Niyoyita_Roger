package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.panier.PanierRepository;
import be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private User user;

    @Autowired
    private  UserRepository userRepository;


    public CustomUserDetails (User user) {
        this.user = user;
    }

    public void save(User user) {
        this.userRepository.save(user);
    }

    public void update(User user) {
        user.setDateModification(LocalDateTime.now());
        this.userRepository.save(user);
    }

    public String getEmailUser(){
        return this.user.getEmail();
    }
    public String getFullName(){
        return user.getNom() + " "+ user.getPrenom();
    }

    public  Long getId(){
        return this.user.getId();
    }

    public void setFirstName(String firstName) {
        this.user.setNom(firstName);
    }

    public void setLastName(String lastName) {
        this.user.setPrenom(lastName);
    }

    public boolean hasRole(String role) {
        return this.user.hasRole(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = this.user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach( role -> authorities.add(new SimpleGrantedAuthority(role.getNom())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser(String emailUser) {
        return userRepository.findByEmail(user.getEmail());
    }

}
