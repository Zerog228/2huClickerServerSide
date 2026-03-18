package me.zink.clicker.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
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

    //TODO Я могу сохранять данные юзеров не при каждой операции, а раз в определённое время
    @Getter
    private Long id;
    private String username;
    @Getter
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private User user;

    //Player data
    private static final int LEVEL_INCREASE_COST_MULT = 20;
    private Map<Upgrade, Integer> upgrades;

    public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities,
                          User user) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;

        this.user = user;
        this.upgrades = stringToUpgrades(user.getUpgrades());
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

    public void setLastMobName(UserRepository repo, String last_mob_name){
        user.setLast_mob_name(last_mob_name);
        repo.save(user);
    }

    public String getLastMobName(){
        return user.getLast_mob_name();
    }

    public List<String> getCurrentLocationMobs(){
        return user.getCurrent_location_mobs();
    }

    public List<String> getOrGenCurrentLocationMobs(UserRepository repo){
        user.setCurrent_location_mobs(MobUtils.genMobsForLocation(user.getLocation_level()));
        repo.save(user);

        return user.getCurrent_location_mobs();
    }

    public void setCurrentLocationMobs(UserRepository repo, List<String> current_location_mobs){
        user.setCurrent_location_mobs(current_location_mobs);
        repo.save(user);
    }

    public List<String> getNextLocationMobs(){
        return user.getNext_location_mobs();
    }

    public List<String> getOrGenNextLocationMobs(UserRepository repo){
        user.setNext_location_mobs(MobUtils.genMobsForLocation(user.getLocation_level() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));
        repo.save(user);

        return user.getNext_location_mobs();
    }

    public void setNextLocationMobs(UserRepository repo, List<String> next_location_mobs){
        user.setNext_location_mobs(next_location_mobs);
        repo.save(user);
    }

    public int getExp(){
        return user.getExp();
    }

    public void addExp(UserRepository repo, int amount){
        user.addExp(amount, LEVEL_INCREASE_COST_MULT);
        repo.save(user);
    }

    public int getLevel(){
        return user.getLevel();
    }

    private void increaseLevel(UserRepository repo){
        user.increaseLevel();
        repo.save(user);
    }

    public int getUpgradePoints(){
        return user.getUpgrade_points();
    }

    public int levelUpCost(){
        return user.levelUpCost(LEVEL_INCREASE_COST_MULT);
    }

    public int getMoney() {
        return user.getMoney();
    }

    public void addMoney(UserRepository repo, int amount){
        user.addMoney(amount);
        repo.save(user);
    }

    public boolean removeMoney(UserRepository repo, int amount){
        boolean removed = user.removeMoney(amount);
        if(removed){
            repo.save(user);
        }
        return removed;
    }

    public int getBombs(){
        return user.getBombs();
    }

    public int getHealth(){
        return user.getHealth();
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

    public String getUpgrades(){
        return user.getUpgrades();
    }

    public void setUpgrades(UserRepository repo, String upgrades){
        user.setUpgrades(upgrades);
        repo.save(user);
    }

    public float getMoneyMult() {
        return 1 + (this.upgrades.get(Upgrade.MORE_MONEY) * Upgrade.MORE_MONEY.getAbilityPower());
    }

    public float getExpMult() {
        return 1 + (this.upgrades.get(Upgrade.MORE_EXP) * Upgrade.MORE_EXP.getAbilityPower());
    }

    public static String upgradesToString(Map<Upgrade, Integer> upgrades){
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
