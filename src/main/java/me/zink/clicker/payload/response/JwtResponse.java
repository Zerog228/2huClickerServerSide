package me.zink.clicker.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    @Setter
    @Getter
    private Long id;
    @Getter
    @Setter
    private String username;
    @Setter
    @Getter
    private String email;
    @Getter
    private List<String> roles;

    //Player entity
    @Getter
    @Setter
    private int level, upgrade_points, exp, money, bombs, health, location_level;
    @Getter
    @Setter
    private HashMap<String, Integer> abilities_map;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles,
                       int level, int upgrade_points, int exp, int money, int bombs, int health, HashMap<String, Integer> abilities_map) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;

        //Game data
        this.level = level;
        this.upgrade_points = upgrade_points;
        this.exp = exp;
        this.money = money;
        this.bombs = bombs;
        this.health = health;
        this.location_level = level;
        this.abilities_map = abilities_map;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }
}