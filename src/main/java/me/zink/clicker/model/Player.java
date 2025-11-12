package me.zink.clicker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.HashMap;

@Data
@Entity
@Table(name = "players")
public class Player {

    //User data
    @Id
    private long id;
    private String name;
    private String password;

    //Player entity
    private int level;
    private int upgrade_points;
    private int exp;
    private int money;
    //private HashMap<String, Integer> abilities_map;
    //private int health;

    //Location data
    private int location_level;


}
