package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.User;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.Upgrade;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
public class UpgradeAbilityRequest {
    @NotBlank
    private String ability;

    @NotBlank
    private int location_level;

    @NotBlank
    private long timestamp;
}
