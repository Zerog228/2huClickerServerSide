package me.zink.clicker.controller;

import me.zink.clicker.util.MobUtils;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.Upgrade;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private UserRepository repo;

    @GetMapping("/getmob")
    @PreAuthorize("hasRole('USER')")
    public String getMob() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mob = userDetails.getLastMobName();
        if(mob == null || mob.isEmpty()){
            mob = MobUtils.genType(userDetails.getLocationLevel());
            userDetails.setLastMobName(repo, mob);
        }
        return mob;
    }

    @GetMapping("/killmob")
    @PreAuthorize("hasRole('USER')")
    public Pair<Boolean, String> killMob() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return MobUtils.kill(repo, userDetails);
    }

    @GetMapping("/upgrade/{id}")
    @PreAuthorize("hasRole('USER')")
    public Pair<Boolean, String> upgradeAbility(@PathVariable(value="id") int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            return userDetails.upgradeAbility(repo, Upgrade.values()[id]).asPair();
        }catch (Exception ignored){
            return new Pair<>(false, Upgrade.Message.F_ABILITY_NOT_FOUND.getMessage());
        }
    }
}
