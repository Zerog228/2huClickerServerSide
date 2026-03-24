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

    //Actions
    @OneToMany(fetch = FetchType.LAZY, cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(
            name = "user_actions",
            joinColumns = {
                    @JoinColumn(name = "user_id"),
            },
            inverseJoinColumns = @JoinColumn(name = "action")
    )
    private List<Action> actions = new ArrayList<>();


    //Player entity
    private int location_level;
    private long mob_seed;


    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;

        this.location_level = 1;
        this.mob_seed = new Random().nextLong();
    }

    public void addAction(Action action){
        actions.add(action);
    }

    public void addAction(List<Action> actions){
        this.actions.addAll(actions);
    }

    public void setAction(List<Action> actions){
        this.actions = actions;
    }

    public void increaseLocationLevel(){
        this.location_level++;
    }

    public void increaseLocationLevel(int levels){
        this.location_level += levels;
    }

    public void setLocationLevel(int location_level){
        this.location_level = location_level;
    }
}
