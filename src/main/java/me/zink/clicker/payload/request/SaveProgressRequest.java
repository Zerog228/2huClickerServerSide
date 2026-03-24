package me.zink.clicker.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.zink.clicker.model.Action;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class SaveProgressRequest {

    @NotBlank
    private List<Map<String, Object>> actions;

    @NotBlank
    private int locationLevel;

}
