package me.zink.clicker.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class KillMobsRequest {
    @NotBlank
    private int currentMoney;

    @NotBlank
    private int currentEXP;
}
