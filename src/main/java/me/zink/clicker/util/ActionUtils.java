package me.zink.clicker.util;

import lombok.Getter;
import me.zink.clicker.model.Action;
import me.zink.clicker.model.EAction;
import me.zink.clicker.security.service.UserDetailsImpl;

import java.util.*;

import static me.zink.clicker.util.MobUtils.getTrueLocLevel;

public class ActionUtils {
    /**
     * When account registered timestamp is created on both server and client.
     * Timestamps will help with eliminating time manipulations on client and measuring
     * amount of time taken for killing mobs/beating the game.
     *
     * This value defines maximum acceptable amount of time inconsistency with timestamp
     * */
    private static final long MAX_TIME_DIFF_MILLS = 3000;

    /**
     * This method
     * @param userDetails User who actions will be validated
     * @return Value which shows how much user have cheated during game
     * */
    public static float validateActions(UserDetailsImpl userDetails){
        float cheat_rate = 0f;

        int exp = 0, money = 0;
        int longerStickLevel = 0, moreMoneyLevel = 0, moreEXPLevel = 0;

        Action registration = userDetails.getActions().get(0);
        long timeDiff = registration.getClientTimestamp() - registration.getServerTimestamp();

        List<MobUtils.MobType> mobs = MobUtils.genMobs(userDetails.getLocationLevel(), userDetails.getMobSeed());
        Map<Integer, List<UpgradeAction>> upgradeMap = mapUpgrades(userDetails.getActions());

        for(int i = 0; i < userDetails.getLocationLevel() - 1; i++){
            if(i < mobs.size()){
                //All upgrades that are made on location
                List<UpgradeAction> upgrades = upgradeMap.get(i);
                if (!upgrades.isEmpty()) {
                    for (UpgradeAction upgrade : upgrades) {
                        //Calc costs
                        if (upgrade.upgrade() == Upgrade.MORE_EXP) {
                            money -= calcUpgradeCost(Upgrade.MORE_EXP, moreEXPLevel++);
                        }
                        if (upgrade.upgrade() == Upgrade.MORE_MONEY) {
                            money -= calcUpgradeCost(Upgrade.MORE_MONEY, moreMoneyLevel++);
                        }
                        if (upgrade.upgrade() == Upgrade.LONGER_STICK) {
                            money -= calcUpgradeCost(Upgrade.LONGER_STICK, longerStickLevel++);
                        }

                        //Compare timestamps
                    /*long currentTimeDiff = upgrade.action.getClientTimestamp() - upgrade.action.getServerTimestamp();
                    if(Math.abs(currentTimeDiff - timeDiff) > MAX_TIME_DIFF_MILLS){
                        cheat_rate += 0.01f;
                    }*/
                    }
                }

                //If player somehow got more money than he physically could
                if (money < 0) {
                    money = 0;
                    cheat_rate += 0.05f;
                }

                //Assign rewards from mob
                money += (int) /*Location bonus*/ ((getTrueLocLevel(i) * mobs.get(i).getMoneyMult()) * /*Ability bonus*/ (1 + (moreMoneyLevel * Upgrade.MORE_MONEY.getAbilityPower())));
                exp += (int) /*Location bonus*/ ((getTrueLocLevel(i) * mobs.get(i).getExpMult()) * /*Ability bonus*/ (1 + (moreEXPLevel * Upgrade.MORE_EXP.getAbilityPower())));
            }else{
                //Seed on client differs from seed on server!
                cheat_rate += 1f;
                break;
            }
        }

        //Compare timestamps
        for(Action action : userDetails.getActions()) {
            long currentTimeDiff = action.getClientTimestamp() - action.getServerTimestamp();
            if (Math.abs(currentTimeDiff - timeDiff) > MAX_TIME_DIFF_MILLS) {
                cheat_rate += 0.01f;
            }
        }

        return cheat_rate;
    }

    private static Map<Integer, List<UpgradeAction>> mapUpgrades(List<Action> actions){
        actions = actions.stream().filter(action -> action.getAction() == EAction.UPGRADE).toList();
        Map<Integer, List<UpgradeAction>> upgradeMap = new HashMap<>();
        for(Action action : actions){
            try{
                if(upgradeMap.containsKey(action.getLocation() - 1)){
                    List<UpgradeAction> upgrades = new ArrayList<>(upgradeMap.get(action.getLocation()));
                    upgrades.add(new UpgradeAction(Upgrade.valueOf(action.getInfo()), action));
                    upgradeMap.put(action.getLocation() - 1, upgrades);
                }else{
                    upgradeMap.put(action.getLocation() - 1, Collections.singletonList(new UpgradeAction(Upgrade.valueOf(action.getInfo()), action)));
                }
            }catch (Exception ignored){}
        }
        return upgradeMap;
    }

    private static int calcUpgradeCost(Upgrade upgrade, int upgrade_level){
        return ((upgrade_level + 1) * upgrade.getCost() + upgrade_level * upgrade.getAdditionalCostPerLevel());
    }

    /**
     * Calculates estimated time on server based on initial time of server/client and current time of action on client
     * @return Estimated time in range -> [serverBase; currentTimeMills()]
     * */
    public static long calcSTime(long serverBase, long clientBase, long clientCurrent){
        return Math.max(Math.min(serverBase + clientCurrent - clientBase, System.currentTimeMillis()), serverBase);
    }

    private record UpgradeAction(Upgrade upgrade, Action action) {}
}
