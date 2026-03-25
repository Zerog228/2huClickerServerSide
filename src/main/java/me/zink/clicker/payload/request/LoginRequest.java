package me.zink.clicker.payload.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.Action;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String actions;

    @NotBlank
    private int locationLevel;

    public List<Map<String, Object>> getActions(){
        return new Gson().fromJson(actions, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }
}
