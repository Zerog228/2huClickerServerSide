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

    @Getter
    private static final int LOCATION_LEVELS_PER_BOSS = 20;

    public static int getTrueLocLevel(int locationLevel){
        return (int) (locationLevel / LOCATION_LEVELS_PER_BOSS) + 1;
    }

    public static List<MobType> genMobs(int location_level, long seed){
        List<MobType> mobs = new ArrayList<>(location_level);
        Random rand = new Random(seed);
        for(int current_location = 1; current_location <= location_level; current_location++){
            //If boss
            if(current_location % LOCATION_LEVELS_PER_BOSS == 0){
                mobs.add(getBoss(current_location));
                continue;
            }

            //Indoor or Outdoor. First 3 bosses will be outdoor, other 4 - indoor
            if(current_location < LOCATION_LEVELS_PER_BOSS * 3){
                mobs.add(getOutdoorEnemies().get(rand.nextInt(getOutdoorEnemies().size())));
                continue;
            }else if(current_location < LOCATION_LEVELS_PER_BOSS * 7){
                mobs.add(getIndoorEnemies().get(rand.nextInt(getIndoorEnemies().size())));
                continue;
            }

            //If none passes
            MobType type = MobType.values()[rand.nextInt(MobType.values().length)];
            mobs.add(type);
            if(type == MobType.MIMA){
                return mobs;
            }
        }
        mobs.add(MobType.MIMA);

        return mobs;
    }

    private static List<MobType> getOutdoorEnemies(){
        return List.of(MobType.DAIYOUSEI, MobType.STAR, MobType.LUNA, MobType.SUNNY, MobType.FAIRY, MobType.KEDAMA, MobType.KAGEROU);
    }

    private static List<MobType> getIndoorEnemies(){
        return List.of(MobType.KEDAMA, MobType.FAIRY_MAID_ONE, MobType.FAIRY_MAID_TWO, MobType.FAIRY_MAID_THREE, MobType.KOAKUMA);
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
