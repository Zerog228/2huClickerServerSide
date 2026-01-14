package me.zink.clicker.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.User;
import me.zink.clicker.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;

    private String username;

    @Getter
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    //Player entity
    @Getter
    @Setter
    private int level, upgrade_points, exp, money, bombs, health, location_level;
    @Getter
    @Setter
    private HashMap<String, Integer> abilities_map;

    private User user;

    private String last_mob_name;

    public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities,
                           int level, int upgrade_points, int exp, int money, int bombs, int health, HashMap<String, Integer> abilities_map, String last_mob_name,
                           User user) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;

        //Game data
        this.level = level;
        this.upgrade_points = upgrade_points;
        this.exp = exp;
        this.money = money;
        this.bombs = bombs;
        this.health = health;
        this.location_level = level;
        this.abilities_map = abilities_map;

        this.last_mob_name = last_mob_name;

        this.user = user;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,

                //Game data
                user.getLevel(),
                user.getUpgrade_points(),
                user.getExp(),
                user.getMoney(),
                user.getBombs(),
                user.getHealth(),
                user.getAbilities_map(),

                user.getLastMobName(),

                //User
                user
                );
    }

    public void setLastMobName(UserRepository repo, String last_mob_name){
        user.setLastMobName(last_mob_name);
        repo.save(user);
    }

    public void increaseExp(UserRepository repo){
        user.increaseExp();
        repo.save(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
