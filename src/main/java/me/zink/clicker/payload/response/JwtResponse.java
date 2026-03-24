package me.zink.clicker.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.Action;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";

    //Player entity
    private int location_level;
    private List<Action> actions;

    //Mob info
    private long mob_seed;

    public JwtResponse(String accessToken, int location_level, long mob_seed, List<Action> actions) {
        this.token = accessToken;

        //Game data
        this.location_level = location_level;
        this.mob_seed = mob_seed;
        this.actions = actions;
    }
}