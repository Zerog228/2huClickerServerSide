package me.zink.clicker.payload.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.User;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.Upgrade;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UpgradeAbilityRequest {
    @NotBlank
    private String ability;

    @NotBlank
    private int location_level;

    @NotBlank
    private long timestamp;

    @NotBlank
    private String actions;

    public List<Map<String, Object>> getActions(){
        return new Gson().fromJson(actions, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }
}
