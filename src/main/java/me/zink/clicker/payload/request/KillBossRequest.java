package me.zink.clicker.payload.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class KillBossRequest {
    @NotBlank
    private int location_level;

    @NotBlank
    private long timestamp;

    private String actions;

    public List<Map<String, Object>> getActions(){
        return new Gson().fromJson(actions, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }
}
