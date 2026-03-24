package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.Action;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private List<Map<String, Object>> actions;
}
