package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;
import me.zink.clicker.model.User;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.Upgrade;
import org.springframework.security.core.userdetails.UserDetails;

public class UpgradeAbilityRequest {
    @NotBlank
    private String ability;

    public Upgrade getAbility() {
        try{
            return Upgrade.valueOf(ability);
        }catch (Exception ignored){
            return null;
        }
    }
}
