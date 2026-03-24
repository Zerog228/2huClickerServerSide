package me.zink.clicker.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import me.zink.clicker.model.Action;
import me.zink.clicker.model.User;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.util.MobUtils;
import me.zink.clicker.util.Upgrade;
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

    private User user;

    public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities,
                          User user) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;

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

                user
        );
    }

    public long getMobSeed(){
        return user.getMob_seed();
    }

    public List<Action> getActions(){
        return user.getActions();
    }

    public void setActions(UserRepository repo, List<Action> actions){
        user.setActions(actions);
        repo.save(user);
    }

    public void addAction(UserRepository repo, Action action){
        user.addAction(action);
        repo.save(user);
    }

    public void addAction(UserRepository repo, List<Action> actions){
        user.addAction(actions);
        repo.save(user);
    }

    public int getLocationLevel(){
        return user.getLocation_level();
    }

    public void increaseLocationLevel(UserRepository repo){
        user.increaseLocationLevel();
        repo.save(user);
    }

    public void increaseLocationLevel(UserRepository repo, int levels){
        user.increaseLocationLevel(levels);
        repo.save(user);
    }

    public void setLocationLevel(UserRepository repo, int location_level){
        user.setLocationLevel(location_level);
        repo.save(user);
    }

    //Upgrades
    /*public static String upgradesToString(Map<Upgrade, Integer> upgrades){
        return new Gson().toJson(upgrades, new TypeToken<Map<Upgrade, Integer>>() {}.getType());
    }

    public static HashMap<Upgrade, Integer> stringToUpgrades(String upgrades){
        return new Gson().fromJson(upgrades, new TypeToken<Map<Upgrade, Integer>>(){}.getType());
    }

    public Upgrade.Message upgradeAbility(UserRepository repo, Upgrade upgradeType){
        try {
            int upgrade_level = upgrades.getOrDefault(upgradeType, 0);
            if(upgrade_level < upgradeType.getMaxLevel()){
                if (removeMoney(repo, (upgrade_level + 1) * levelUpCost() + upgrade_level * upgradeType.getAdditionalCostPerLevel())) {
                    upgrades.put(upgradeType, ++upgrade_level);
                    setUpgrades(repo, upgradesToString(upgrades));
                    return Upgrade.Message.SUCCESS;
                } else {
                    return Upgrade.Message.F_NOT_ENOUGH_MONEY;
                }
            }else {
                return Upgrade.Message.F_MAX_LEVEL;
            }
        }catch (Exception e){
            return Upgrade.Message.F_ABILITY_NOT_FOUND;
        }
    }*/

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
