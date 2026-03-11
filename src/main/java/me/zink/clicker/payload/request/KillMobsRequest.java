package me.zink.clicker.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class KillMobsRequest {

    //TODO
    //Get current money amount. Money on client < Money on server -> Save lower value
    //When sending response send synced value
    @NotBlank
    private int currentMoney;

    //Same as with money
    @NotBlank
    private int currentEXP;

    //Tricky part on server-client desynchronization
    //Client can only have access to current and next mobs.
    //  Client Current mobs == Server current mobs -> OK (Calc rewards based on 1 pool, gen and send only next mobs)
    //  Client Current mobs == Server next mobs -> 50/50. NEXT MOBS FROM CLIENT THEN MUST NE EMPTY (Calc rewards based on both pools, gen and send both pools.)
    //  Client Current mobs != Server current/next mobs -> NOT OK
    @NotBlank
    private List<String> currentMobs;

    @NotBlank
    private List<String> nextMobs;
}
