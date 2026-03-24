package me.zink.clicker.controller;

import jakarta.validation.Valid;
import me.zink.clicker.model.Action;
import me.zink.clicker.model.EAction;
import me.zink.clicker.payload.request.KillBossRequest;
import me.zink.clicker.payload.request.SaveProgressRequest;
import me.zink.clicker.payload.request.UpgradeAbilityRequest;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.ActionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@Valid @RequestBody SaveProgressRequest saveProgressRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(saveProgressRequest.getActions() != null && saveProgressRequest.getActions().size() > userDetails.getActions().size()){
            for(int i = userDetails.getActions().size(); i < saveProgressRequest.getActions().size(); i++){
                try{
                    Map<String, Object> actionMap = saveProgressRequest.getActions().get(i);
                    long estimatedTimestamp = ActionUtils.calcSTime(userDetails.getActions().get(0).getServerTimestamp(), userDetails.getActions().get(0).getClientTimestamp(), (long) actionMap.get("clientTimestamp"));
                    Action action = new Action(EAction.valueOf((String) actionMap.get("action")), (String) actionMap.get("info"), (int) actionMap.get("location"), estimatedTimestamp, (long) actionMap.get("clientTimestamp"));
                    userDetails.addAction(userRepository, action);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/upgrade")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> upgrade(@Valid @RequestBody UpgradeAbilityRequest upgradeAbilityRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Action action = new Action(EAction.UPGRADE, upgradeAbilityRequest.getAbility(), upgradeAbilityRequest.getLocation_level(), System.currentTimeMillis(), upgradeAbilityRequest.getTimestamp());
        userDetails.addAction(userRepository, action);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/boss")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> killBoss(@Valid @RequestBody KillBossRequest killBossRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Action action = new Action(EAction.KILL_BOSS, "null", killBossRequest.getLocation_level(), System.currentTimeMillis(), killBossRequest.getTimestamp());
        userDetails.addAction(userRepository, action);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> delete(@Valid @RequestBody UpgradeAbilityRequest upgradeAbilityRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //TODO Delete user

        return ResponseEntity.ok("Deleted successfully!");
    }
}
