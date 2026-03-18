package me.zink.clicker.controller;

import jakarta.validation.Valid;
import me.zink.clicker.payload.request.KillMobsRequest;
import me.zink.clicker.payload.request.LoginRequest;
import me.zink.clicker.payload.request.SaveProgressRequest;
import me.zink.clicker.payload.request.UpgradeAbilityRequest;
import me.zink.clicker.util.MobUtils;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import me.zink.clicker.util.Upgrade;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private UserRepository repo;

    /**
     * Method for getting mobs from server one by one.
     * Available only to admins because of it's inefficiency
     * */
    @GetMapping("/getmob")
    @PreAuthorize("hasRole('ADMIN')")
    public String getMob() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mob = userDetails.getLastMobName();
        if(mob == null || mob.isEmpty()){
            mob = MobUtils.genType(userDetails.getLocationLevel());
            userDetails.setLastMobName(repo, mob);
        }
        return mob;
    }

    @GetMapping("/getmobs")
    @PreAuthorize("hasRole('USER')")
    public List<String> getMobs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> mobs = new ArrayList<>(userDetails.getOrGenCurrentLocationMobs(repo));
        mobs.addAll(userDetails.getOrGenNextLocationMobs(repo));
        return mobs;
    }

    @GetMapping("/getcurrentmobs")
    @PreAuthorize("hasRole('USER')")
    public List<String> getCurrentMobs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> mobs = userDetails.getOrGenCurrentLocationMobs(repo);

        return mobs;
    }

    @GetMapping("/getnextmobs")
    @PreAuthorize("hasRole('USER')")
    public List<String> getNextMobs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> mobs = userDetails.getOrGenNextLocationMobs(repo);

        return mobs;
    }

    private static final double MAX_DIFF_PERCENT = 1.05;

    @GetMapping("/killmobs")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> killMobs(@Valid @RequestBody KillMobsRequest killMobsRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> currentMobs = userDetails.getOrGenCurrentLocationMobs(repo);
        List<String> futureMobs = userDetails.getOrGenNextLocationMobs(repo);

        //If everything synced properly
        if(killMobsRequest.getCurrentMobs().equals(currentMobs)){
            Pair<Integer, Integer> rewards = MobUtils.getRewards(currentMobs, userDetails);
            userDetails.increaseLocationLevel(repo, MobUtils.getLOCATION_LEVELS_PER_BOSS()); //Increase level

            int expected_money = rewards.a + userDetails.getMoney();
            int expected_exp = rewards.b + userDetails.getExp();

            int received_money = killMobsRequest.getCurrentMoney();
            int received_exp = killMobsRequest.getCurrentEXP();

            //Sometimes calculated (expected) values could be bigger, then actual one's (If ability was upgraded in the middle of a bunch)
            //In such cases server returns client-side money
            //TODO Add some kind of compensation for such cases?

            if(expected_money > received_money || expected_money * MAX_DIFF_PERCENT > received_money){
                //Update server player data
                //Basically we don't need to add money nor exp here, but this check is nice to have
                userDetails.addMoney(repo, Math.max(0, received_money - expected_money));
                userDetails.addExp(repo, Math.max(0, received_exp - expected_exp));
                //
                userDetails.setCurrentLocationMobs(repo, futureMobs);
                userDetails.setNextLocationMobs(repo, MobUtils.genMobsForLocation(userDetails.getLocationLevel() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));

                //Fill response. Money and EXP values are already fine
                killMobsRequest.setCurrentMobs(futureMobs);
                killMobsRequest.setNextMobs(userDetails.getNextLocationMobs());
                return ResponseEntity.ok(
                        killMobsRequest
                );
            }else{ //If for some reason money on client is much higher than on server but there were no de-syncs.
                //TODO Notify Anti-cheat
                //Update money and exp based on calculated values
                userDetails.addMoney(repo, rewards.a);
                userDetails.addExp(repo, rewards.b);
                //
                userDetails.setCurrentLocationMobs(repo, futureMobs);
                userDetails.setNextLocationMobs(repo, MobUtils.genMobsForLocation(userDetails.getLocationLevel() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));

                //Fill response. Sync money and exp based on calculated server-side value
                killMobsRequest.setCurrentEXP(userDetails.getExp());
                killMobsRequest.setCurrentMoney(userDetails.getMoney());
                killMobsRequest.setCurrentMobs(futureMobs);
                killMobsRequest.setNextMobs(userDetails.getNextLocationMobs());
                return ResponseEntity.ok(
                        killMobsRequest
                );
            }
        }

        //If de-sync happened but it's not too bad
        if(killMobsRequest.getCurrentMobs().equals(futureMobs)){
            Pair<Integer, Integer> rewards_from_last_batch = MobUtils.getRewards(currentMobs, userDetails);
            userDetails.increaseLocationLevel(repo, MobUtils.getLOCATION_LEVELS_PER_BOSS()); //First level increase
            Pair<Integer, Integer> rewards_from_current_batch = MobUtils.getRewards(futureMobs, userDetails);
            userDetails.increaseLocationLevel(repo, MobUtils.getLOCATION_LEVELS_PER_BOSS()); //Second level increase

            int expected_money = rewards_from_last_batch.a + rewards_from_current_batch.a + userDetails.getMoney();
            int expected_exp = rewards_from_current_batch.b + rewards_from_current_batch.b + userDetails.getExp();

            int received_money = killMobsRequest.getCurrentMoney();
            int received_exp = killMobsRequest.getCurrentEXP();

            if(expected_money > received_money || expected_money * MAX_DIFF_PERCENT > received_money){
                //Update server player data
                //Basically we don't need to add money nor exp here, but this check is nice to have
                userDetails.addMoney(repo, Math.max(0, received_money - expected_money));
                userDetails.addExp(repo, Math.max(0, received_exp - expected_exp));
                //
                userDetails.setCurrentLocationMobs(repo, MobUtils.genMobsForLocation(userDetails.getLocationLevel() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));
                userDetails.setNextLocationMobs(repo, MobUtils.genMobsForLocation(userDetails.getLocationLevel() + MobUtils.getLOCATION_LEVELS_PER_BOSS() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));

                //Fill response. Money and EXP values are already fine
                killMobsRequest.setCurrentMobs(userDetails.getCurrentLocationMobs());
                killMobsRequest.setNextMobs(userDetails.getNextLocationMobs());
                return ResponseEntity.ok(
                        killMobsRequest
                );
            }else{ //If for some reason money on client is much higher than on server but there were no de-syncs.
                //TODO Notify Anti-cheat
                //Update money and exp based on calculated values
                userDetails.addMoney(repo, rewards_from_last_batch.a + rewards_from_current_batch.a);
                userDetails.addExp(repo, rewards_from_last_batch.b + rewards_from_current_batch.b);
                //
                userDetails.setCurrentLocationMobs(repo, MobUtils.genMobsForLocation(userDetails.getLocationLevel() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));
                userDetails.setNextLocationMobs(repo, MobUtils.genMobsForLocation(userDetails.getLocationLevel() + MobUtils.getLOCATION_LEVELS_PER_BOSS() + MobUtils.getLOCATION_LEVELS_PER_BOSS()));

                //Fill response. Sync money and exp based on calculated server-side value
                killMobsRequest.setCurrentEXP(userDetails.getExp());
                killMobsRequest.setCurrentMoney(userDetails.getMoney());
                killMobsRequest.setCurrentMobs(futureMobs);
                killMobsRequest.setNextMobs(userDetails.getNextLocationMobs());
                return ResponseEntity.ok(
                        killMobsRequest
                );
            }
        }

        //If neither of both cases passes then something isn't right. Request should be aborted
        //TODO Implement client-side rollback
        return ResponseEntity.badRequest().body("Something wrong with mob list!");
    }

    @GetMapping("/save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> save(@Valid @RequestBody SaveProgressRequest saveProgressRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //This will work perfectly if 'CurrentQMoney' and 'CurrentQEXP' are implemented on the client-side.
        //Only one check with them should be made to determine if data is modified or not
        // because the only thing that changes between mob kills is current_location_level.
        //Everything else synchronizes on mob kill or ability upgrade
        if(saveProgressRequest.getCurrentMoney() == userDetails.getMoney()){
            userDetails.increaseLocationLevel(repo, saveProgressRequest.getLocationLevel() - userDetails.getLocationLevel());
            return ResponseEntity.ok("Saved player");
        }else{
            //TODO Add different checks based on different data and notify anti-cheat
            //If difference is small enough than saving it won't hurt, right?
            return ResponseEntity.badRequest().body("Player data was modified");
        }
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

    @GetMapping("/killmob")
    @PreAuthorize("hasRole('ADMIN')")
    public Pair<Boolean, String> killMob() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return MobUtils.kill(repo, userDetails);
    }

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
}
