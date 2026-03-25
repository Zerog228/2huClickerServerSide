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

import static me.zink.clicker.util.ActionUtils.updateActionList;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/game", method = {
        RequestMethod.GET, RequestMethod.POST
})
public class GameController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/save")
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> save(@Valid @RequestBody SaveProgressRequest saveProgressRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        updateActionList(userRepository, userDetails, saveProgressRequest.getActions());
        if(saveProgressRequest.getLocation_level() > userDetails.getLocationLevel()){
            userDetails.setLocationLevel(userRepository, saveProgressRequest.getLocation_level());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/upgrade")
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> upgrade(@Valid @RequestBody UpgradeAbilityRequest upgradeAbilityRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ActionUtils.updateActionList(userRepository, userDetails, upgradeAbilityRequest.getActions());

        Action action = new Action(EAction.UPGRADE, upgradeAbilityRequest.getAbility(), upgradeAbilityRequest.getLocation_level(), System.currentTimeMillis(), upgradeAbilityRequest.getTimestamp());
        userDetails.addAction(userRepository, action);

        if(upgradeAbilityRequest.getLocation_level() > userDetails.getLocationLevel()){
            userDetails.setLocationLevel(userRepository, upgradeAbilityRequest.getLocation_level());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/boss")
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> killBoss(@Valid @RequestBody KillBossRequest killBossRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ActionUtils.updateActionList(userRepository, userDetails, killBossRequest.getActions());


        Action action = new Action(EAction.KILL_BOSS, "null", killBossRequest.getLocation_level(), System.currentTimeMillis(), killBossRequest.getTimestamp());
        userDetails.addAction(userRepository, action);

        if(killBossRequest.getLocation_level() + 1 > userDetails.getLocationLevel()){
            userDetails.setLocationLevel(userRepository, killBossRequest.getLocation_level() + 1);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> delete(@Valid @RequestBody UpgradeAbilityRequest upgradeAbilityRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //TODO Delete user

        return ResponseEntity.ok("Deleted successfully!");
    }
}
