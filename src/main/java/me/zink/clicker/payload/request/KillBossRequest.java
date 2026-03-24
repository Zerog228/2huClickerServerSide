package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KillBossRequest {
    @NotBlank
    private int location_level;

    @NotBlank
    private long timestamp;
}
