package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Synchronizing progress on mob kill
 * */
@Getter
public class SaveProgressRequest {

    @NotBlank
    private int currentMoney;

    @NotBlank
    private int currentEXP;

    @NotBlank
    private String upgrades;

    @NotBlank
    private int bombs;

    @NotBlank
    private int health;

    @NotBlank
    private int upgradePoints;

    @NotBlank
    private int locationLevel;

    @NotBlank
    private int playerLevel;


}
