package me.zink.clicker.controller;

import jakarta.validation.Valid;
import me.zink.clicker.model.Action;
import me.zink.clicker.model.EAction;
import me.zink.clicker.payload.request.KillBossRequest;
import me.zink.clicker.payload.request.ResetRequest;
import me.zink.clicker.payload.request.SaveProgressRequest;
import me.zink.clicker.payload.request.UpgradeAbilityRequest;
import me.zink.clicker.payload.response.JwtResponse;
import me.zink.clicker.repo.ActionRepository;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.ActionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static me.zink.clicker.util.ActionUtils.updateActionList;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/game", method = {
        RequestMethod.GET, RequestMethod.POST
})
public class GameController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRepository actionRepository;

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

        if(upgradeAbilityRequest.getLocation_level() > userDetails.getLocationLevel()){
            userDetails.setLocationLevel(userRepository, upgradeAbilityRequest.getLocation_level());
        }

        ActionUtils.updateActionList(userRepository, userDetails, upgradeAbilityRequest.getActions());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/boss")
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> killBoss(@Valid @RequestBody KillBossRequest killBossRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(killBossRequest.getLocation_level() + 1 > userDetails.getLocationLevel()){
            userDetails.setLocationLevel(userRepository, killBossRequest.getLocation_level() + 1);
        }

        ActionUtils.updateActionList(userRepository, userDetails, killBossRequest.getActions());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public @ResponseBody ResponseEntity<?> delete(@Valid @RequestBody ResetRequest resetRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Long> actionIds = userDetails.getActions().stream().map(action -> action.getId().longValue()).toList();
        userDetails.setActions(userRepository, new ArrayList<>());
        actionIds.forEach(actionRepository::deleteById);
        userDetails.setLocationLevel(userRepository, 1);
        userDetails.genMobSeed(userRepository);

        userDetails.addAction(userRepository, new Action(EAction.INIT, null, 1, System.currentTimeMillis(), resetRequest.getTimestamp()));

        return ResponseEntity.ok(
                new JwtResponse(null, 1, userDetails.getMobSeed(), userDetails.getActions())
        );
    }
}
