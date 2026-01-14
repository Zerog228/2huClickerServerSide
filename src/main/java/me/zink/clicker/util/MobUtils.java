package me.zink.clicker.util;

import jakarta.annotation.Nullable;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;
import java.util.Random;

import static me.zink.clicker.util.IntercontinentalMobInfo.*;

public class MobUtils {

    private static final int LEVEL_HP_MULT = 10;
    private static final int LOCATION_LEVELS_PER_BOSS = 20;

    //TODO Return money and exp amount based on killed mob and mults
    /**
     * @param /mob_name Name of the mob to kill
     * @param /upgrades player upgrades
     * @return Returns a pair of gained exp and money
     * */
    /*private static @Nullable Pair<Integer, Integer> kill(String mob_name, double money_mult){
        try(){

        }catch (Exception ignored){
            System.out.println("Failed to convert mob name to mob instance! Mob name - "+mob_name);
            return null;
        }
    }*/

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

        }

        //If none passes
        //CAN BE DANGEROUS BECAUSE OF THE NPC'S?
        type = MobType.values()[new Random().nextInt(MobType.values().length)];
        return type.name();
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


    enum MobType {
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
