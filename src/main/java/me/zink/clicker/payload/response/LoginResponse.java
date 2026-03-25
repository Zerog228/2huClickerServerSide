package me.zink.clicker.payload.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    @NotBlank
    private int locationLevel;

    public LoginResponse(int locationLevel){
        this.locationLevel = locationLevel;
    }
}
