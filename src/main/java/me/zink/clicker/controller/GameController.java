package me.zink.clicker.controller;

import jakarta.validation.Valid;
import me.zink.clicker.payload.request.SaveProgressRequest;
import me.zink.clicker.payload.request.UpgradeAbilityRequest;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.Upgrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private UserRepository repo;

    /**
     * Method for getting current mob from server
     * */
    @GetMapping("/getmob")
    @PreAuthorize("hasRole('ADMIN')")
    public String getMob() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        /*String mob = userDetails.getLastMobName();
        if(mob == null || mob.isEmpty()){
            mob = MobUtils.genType(userDetails.getLocationLevel());
            userDetails.setLastMobName(repo, mob);
        }
        return mob;*/
    }

    /*@GetMapping("/getmobs")
    @PreAuthorize("hasRole('USER')")
    public List<String> getMobs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> mobs = new ArrayList<>(userDetails.getOrGenCurrentLocationMobs(repo, mobRepository));
        mobs.addAll(userDetails.getOrGenNextLocationMobs(repo, mobRepository));
        return mobs;
    }

    @GetMapping("/getcurrentmobs")
    @PreAuthorize("hasRole('USER')")
    public List<String> getCurrentMobs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> mobs = userDetails.getOrGenCurrentLocationMobs(repo, mobRepository);

        return mobs;
    }

    @GetMapping("/getnextmobs")
    @PreAuthorize("hasRole('USER')")
    public List<String> getNextMobs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> mobs = userDetails.getOrGenNextLocationMobs(repo, mobRepository);

        return mobs;
    }*/

    @GetMapping("/save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@Valid @RequestBody SaveProgressRequest saveProgressRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.badRequest().body("Test");
    }

    @GetMapping("/upgrade")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> upgradeAbility(@Valid @RequestBody UpgradeAbilityRequest upgradeRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            Upgrade.Message message = userDetails.upgradeAbility(repo, upgradeRequest.getAbility());
            if(message.isSuccess()){
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.badRequest().body(message.getMessage());
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*@GetMapping("/killmob")
    @PreAuthorize("hasRole('ADMIN')")
    public Pair<Boolean, String> killMob() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return MobUtils.kill(repo, userDetails);
    }*/

    /*@GetMapping("/upgrade/{id}")
    @PreAuthorize("hasRole('USER')")
    public Pair<Boolean, String> upgradeAbility(@PathVariable(value="id") int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            return userDetails.upgradeAbility(repo, Upgrade.values()[id]).asPair();
        }catch (Exception ignored){
            return new Pair<>(false, Upgrade.Message.F_ABILITY_NOT_FOUND.getMessage());
        }
    }*/

    /*public List<Mob> getMobs(List<String> mobs){
        List<Mob> fillable = new ArrayList<>();
        for(String mob_name : mobs){
            try{
                fillable.add(mobRepository.findByType(MobUtils.MobType.valueOf(mob_name)).orElse(null));
            }catch (Exception ignored){
                fillable.add(null);
            }
        }
        return fillable;
    }*/
}
