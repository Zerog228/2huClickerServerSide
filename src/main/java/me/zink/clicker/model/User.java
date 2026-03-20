package me.zink.clicker.model;

import com.google.gson.Gson;
import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.util.Upgrade;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20, min = 1)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 40, min = 8)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    //Mobs
    /*@OneToMany(fetch = FetchType.LAZY, cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "current_mobs",
            joinColumns = {
                    @JoinColumn(name = "user_id"),
            },
            inverseJoinColumns = @JoinColumn(name = "mob_value"),
            foreignKey = @ForeignKey(name = "mob_index", value = ConstraintMode.NO_CONSTRAINT)
    )
    private List<Mob> current_mobs = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "next_mobs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "mob_id"),
            indexes = {
                    @Index(name = "mob_index", columnList = "mob_id")
            }
    )
    private List<Mob> next_mobs = new ArrayList<>();*/

    //Player entity
    private int level;
    private int upgrade_points;
    private int exp;
    private int money;
    private int bombs;
    @Getter
    @Setter
    private String upgrades;
    private int health;
    private int location_level;
    private long mob_seed;


    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;

        this.level = 1;
        this.upgrade_points = 0;
        this.exp = 0;
        this.money = 0;
        this.bombs = 3;
        this.health = 10;
        this.location_level = 1;
        this.upgrades = upgradesToString();

        this.mob_seed = new Random().nextLong();
    }

    public void addExp(int amount, int level_up_cost){
        exp += amount;
        while(exp >= levelUpCost(level_up_cost)){
            addLevel(1);
        }
    }

    private void addLevel(int amount){
        this.level += amount;
        this.upgrade_points++;
    }

    public int levelUpCost(int level_up_cost){
        return level_up_cost * level * level;
    }

    public void addMoney(int amount){
        this.money+=amount;
    }

    public void increaseLocationLevel(){
        this.location_level++;
    }

    public void increaseLocationLevel(int levels){
        this.location_level += levels;
    }

    public boolean removeMoney(int amount){
        if(money >= amount){
            money -= amount;
            return true;
        }else {
            return false;
        }
    }

    public void increaseLevel(){
        this.level++;
    }

    public static String upgradesToString(){
        return upgradesToString(Arrays.stream(Upgrade.values()).collect(Collectors.toMap(value -> value, value -> 0)));
    }

    public static String upgradesToString(Map<Upgrade, Integer> upgrades){
        return new Gson().toJson(upgrades);
    }
}
