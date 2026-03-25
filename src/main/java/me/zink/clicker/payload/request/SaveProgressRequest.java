package me.zink.clicker.payload.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SaveProgressRequest {

    @NotBlank
    @Size(min = 1, max = 20)
    private int location_level;

    @NotBlank
    private String actions;

    public List<Map<String, Object>> getActions(){
        return new Gson().fromJson(actions, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }

}
