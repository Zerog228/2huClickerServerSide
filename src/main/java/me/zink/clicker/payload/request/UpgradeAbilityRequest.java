package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;

public class UpgradeAbilityRequest {
    @NotBlank
    private String ability;

    public String getAbility() {
        return ability;
    }
}
