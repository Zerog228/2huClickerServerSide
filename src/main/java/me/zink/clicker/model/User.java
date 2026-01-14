package me.zink.clicker.model;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    //Player entity
    private int level;
    private int upgrade_points;
    private int exp;
    private int money;
    private int bombs;
    private HashMap<String, Integer> abilities_map;
    private int health;

    //Location data
    private int location_level;

    //Other
    private String last_mob_name;

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
        this.abilities_map = new HashMap<>();

        this.last_mob_name = "";
    }

    public void increaseExp(){
        this.exp++;
    }

    public void setLastMobName(String mobName){
        this.last_mob_name = mobName;
    }

    public String getLastMobName(){
        return this.last_mob_name;
    }

}
