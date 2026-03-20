package me.zink.clicker.util;

import jakarta.annotation.Nullable;
import lombok.Getter;
import me.zink.clicker.repo.UserRepository;
import me.zink.clicker.security.service.UserDetailsImpl;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.zink.clicker.util.IntercontinentalMobInfo.*;

public class MobUtils {

    private static final int LEVEL_HP_MULT = 10;
    @Getter
    private static final int LOCATION_LEVELS_PER_BOSS = 20;

    /**
     * @return Returns a pair of if the mob was killed and new mob
     * */
    /*public static Pair<Boolean, String> kill(UserRepository repo, UserDetailsImpl user){
        int loc_level = user.getLocationLevel();
        try{
            MobType type = MobType.valueOf(user.getLastMobName());
            user.addExp(repo, (int) (user.getExpMult() * getTrueLocLevel(loc_level) * type.getExpMult()));
            user.addMoney(repo, (int) (user.getMoneyMult() * getTrueLocLevel(loc_level) * type.getMoneyMult()));

            //Increase loc level, set new mob type
            user.increaseLocationLevel(repo);
            user.setLastMobName(repo, genType(user.getLocationLevel()));
            return new Pair<>(true, user.getLastMobName());
        }catch (Exception e){
            System.out.println("Failed to kill a mob! Mob type does not exist!");
            e.printStackTrace();
            return new Pair<>(false, null);
        }
    }*/

    /**
     * Generates expected rewards based on mob list.
     * If mob does not exist then rewards for it will be set to 0
     * @param mobs Mobs to kill
     * @return Pair of MONEY and EXP
     * */
    public static Pair<Integer, Integer> getRewards(List<String> mobs, UserDetailsImpl userDetails){
        return getRewards(mobs, userDetails, userDetails.getLocationLevel());
    }

    /**
     * Generates expected rewards based on mob list.
     * If mob does not exist then rewards for it will be set to 0
     * @param mobs Mobs to kill
     * @return Pair of MONEY and EXP
     * */
    public static Pair<Integer, Integer> getRewards(List<String> mobs, UserDetailsImpl userDetails, int location_level){
        final double EXP_MULT = userDetails.getExpMult(), MONEY_MULT = userDetails.getMoneyMult();
        int money_reward = 0, exp_reward = 0;

        final int TRUE_LOCATION_LEVEL = getTrueLocLevel(location_level);

        for(String mob_name : mobs){
            MobType mob = getMob(mob_name);
            if(mob != null){
                money_reward += (int) (mob.getMoneyMult() * MONEY_MULT * TRUE_LOCATION_LEVEL);
                exp_reward += (int) (mob.getExpMult() * EXP_MULT * TRUE_LOCATION_LEVEL);
            }
        }

        return new Pair<>(money_reward, exp_reward);
    }

    private static int getTrueLocLevel(int locationLevel){
        return (int) (locationLevel / LOCATION_LEVELS_PER_BOSS) + 1;
    }

    public static String genType(int locationLevel){
        MobType type;
        //Check if boss
        if(locationLevel % LOCATION_LEVELS_PER_BOSS == 0){
            type = getBoss(locationLevel);
            return type.name();
        }

        //Indoor or Outdoor. First 3 bosses will be outdoor, other 4 - indoor
        if(locationLevel < LOCATION_LEVELS_PER_BOSS * 3){
            type = getOutdoorEnemies().get(new Random().nextInt(getOutdoorEnemies().size()));
            return type.name();
        }else if(locationLevel < LOCATION_LEVELS_PER_BOSS * 7){
            type = getIndoorEnemies().get(new Random().nextInt(getIndoorEnemies().size()));
            return type.name();
        }else{ // Final location mobs and then boss
            //TODO Generate mobs and boss for final location
        }

        //If none passes
        //CAN BE DANGEROUS BECAUSE OF THE NPCs and bosses? Maybe not really.
        type = MobType.values()[new Random().nextInt(MobType.values().length)];
        return type.name();
    }

    /**
     * Automatically decides player's REAL location level and generates list of 'LOCATION_LEVELS_PER_BOSS' mobs for it.
     * Note that there will always be boss at the end
     * */
    public static List<String> genMobsForLocation(int location){
        int realLocation = location / LOCATION_LEVELS_PER_BOSS; //1 2 3 4 5 6
        List<String> mobIds = new ArrayList<>(LOCATION_LEVELS_PER_BOSS);
        for(int i = realLocation * LOCATION_LEVELS_PER_BOSS + 1; i <= realLocation * LOCATION_LEVELS_PER_BOSS + LOCATION_LEVELS_PER_BOSS; i++){
            mobIds.add(genType(i));
        }
        return mobIds;
    }

    private static List<MobType> getOutdoorEnemies(){
        return List.of(MobType.DAIYOUSEI, MobType.STAR, MobType.LUNA, MobType.SUNNY, MobType.FAIRY, MobType.KEDAMA, MobType.KAGEROU);
    }

    private static List<MobType> getIndoorEnemies(){
        return List.of(MobType.KEDAMA, MobType.FAIRY_MAID_ONE, MobType.FAIRY_MAID_TWO, MobType.FAIRY_MAID_THREE, MobType.KOAKUMA);
    }

    private static List<MobType> getAllBosses(){
        return List.of(MobType.RUMIA, MobType.CIRNO, MobType.MEILING, MobType.PATCHOULI, MobType.SAKUYA, MobType.REMILIA, MobType.FLANDRE, MobType.MIMA);
    }

    private static MobType getBoss(int locationLevel){
        if(locationLevel == LOCATION_LEVELS_PER_BOSS)
            return MobType.RUMIA;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 2)
            return MobType.CIRNO;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 3)
            return MobType.MEILING;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 4)
            return MobType.PATCHOULI;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 5)
            return MobType.SAKUYA;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 6)
            return MobType.REMILIA;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 7)
            return MobType.FLANDRE;

        return MobType.KEDAMA;
    }

    @Nullable
    private static MobType getMob(String mobType){
        try{
            return MobType.valueOf(mobType);
        }catch (Exception ignored){}
        return null;
    }

    public enum MobType {
        //Outdoor mobs
        DAIYOUSEI,
        STAR,
        LUNA,
        SUNNY,
        FAIRY(FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        WRIGGLE,
        KAGEROU, //To add or not to add?

        //Both indoor and outdoor
        KEDAMA(KEDAMA_HEALTH, KEDAMA_EXP, KEDAMA_MONEY),

        //Indoor mobs
        FAIRY_MAID_ONE(FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        FAIRY_MAID_TWO(FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        FAIRY_MAID_THREE(FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        KOAKUMA,
        KOISHI(KOISHI_HEALTH, KOISHI_EXP, KOISHI_MONEY),
        SATORI(SATORI_HEALTH, SATORI_EXP, SATORI_MONEY),

        //Bosses
        RUMIA(RUMIA_HEALTH, RUMIA_EXP, RUMIA_MONEY),
        CIRNO(CIRNO_HEALTH, CIRNO_EXP, CIRNO_MONEY),
        MEILING(MEILING_HEALTH, MEILING_EXP, MEILING_MONEY),
        PATCHOULI(PATCHOULI_HEALTH, PATCHOULI_EXP, PATCHOULI_MONEY),
        SAKUYA(SAKUYA_HEALTH, SAKUYA_EXP, SAKUYA_MONEY),
        REMILIA(REMILIA_HEALTH, REMILIA_EXP, REMILIA_MONEY),
        FLANDRE(FLANDRE_HEALTH, FLANDRE_EXP, FLANDRE_MONEY),

        //Additional bosses
        MIMA(MIMA_HEALTH, MIMA_EXP, MIMA_MONEY),

        //NPC's
        NITORI, //Merchant in the shop

        ;

        private float hp_mult = 1, exp_mult = 1, money_mult = 1;
        MobType(){}

        MobType(float hp_mult, float exp_mult, float money_mult){
            this.hp_mult = hp_mult;
            this.exp_mult = exp_mult;
            this.money_mult = money_mult;
        }

        public float getHpMult() {
            return hp_mult;
        }

        public float getExpMult() {
            return exp_mult;
        }

        public float getMoneyMult() {
            return money_mult;
        }
    }
}
