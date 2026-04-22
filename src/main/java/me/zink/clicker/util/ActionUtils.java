package me.zink.clicker.util;

import me.zink.clicker.model.Action;
import me.zink.clicker.model.EAction;
import me.zink.clicker.repo.UserRepository;
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
    public static CheatReport validateActions(UserDetailsImpl userDetails){
        CheatReport report = new CheatReport(userDetails.getId(), userDetails.getUsername(), userDetails.getLocationLevel());
        report.addAction(userDetails.getActions());

        int exp = 0, money = 0;
        int longerStickLevel = 0, moreMoneyLevel = 0, moreEXPLevel = 0;

        Action registration = userDetails.getActions().get(0);
        long timeDiff = registration.getClientTimestamp() - registration.getServerTimestamp();

        List<MobUtils.MobType> mobs = MobUtils.genMobs(userDetails.getLocationLevel(), userDetails.getMobSeed());
        Map<Integer, List<UpgradeAction>> upgradeMap = mapUpgrades(userDetails.getActions());
        //System.out.println("Upgrade map: "+upgradeMap);
        //System.out.println("Max loc level: "+userDetails.getLocationLevel());

        for(int i = 0; i < userDetails.getLocationLevel() - 1; i++){
            //System.out.println("Location level: "+i);
            if(i < mobs.size()){
                //Assign rewards from mob
                money += (int) /*Location bonus*/ ((getTrueLocLevel(i + 1) * mobs.get(i).getMoneyMult()) * /*Ability bonus*/ (1 + (moreMoneyLevel * Upgrade.MORE_MONEY.getAbilityPower())));
                exp += (int) /*Location bonus*/ ((getTrueLocLevel(i + 1) * mobs.get(i).getExpMult()) * /*Ability bonus*/ (1 + (moreEXPLevel * Upgrade.MORE_EXP.getAbilityPower())));

                int money_increase = (int) /*Location bonus*/ ((getTrueLocLevel(i + 1) * mobs.get(i).getMoneyMult()) * /*Ability bonus*/ (1 + (moreMoneyLevel * Upgrade.MORE_MONEY.getAbilityPower())));
                int exp_increase = (int) /*Location bonus*/ ((getTrueLocLevel(i + 1) * mobs.get(i).getExpMult()) * /*Ability bonus*/ (1 + (moreEXPLevel * Upgrade.MORE_EXP.getAbilityPower())));
                report.addInfoStamp(i,
                        "Killed mob: "+mobs.get(i).name()+" | Current money: "+money+" | Current exp: "+exp,
                        "Money inc.: "+money_increase+" | Exp inc.: "+exp_increase,
                        "Loc. Exp. bonus: "+(((getTrueLocLevel(i + 1) * mobs.get(i).getExpMult()))+" | Ability Exp. bonus: "+((1 + (moreEXPLevel * Upgrade.MORE_EXP.getAbilityPower())))),
                        "Loc. Money. bonus: "+(((getTrueLocLevel(i + 1) * mobs.get(i).getMoneyMult()))+" | Ability Money bonus: "+((1 + (moreMoneyLevel * Upgrade.MORE_MONEY.getAbilityPower()))))
                );

                //All upgrades that are made on location
                List<UpgradeAction> upgrades = upgradeMap.get(i + 1);
                if (upgrades != null && !upgrades.isEmpty()) {
                    for (UpgradeAction upgrade : upgrades) {
                        int money_before = money;
                        //Calc costs
                        if (upgrade.upgrade() == Upgrade.MORE_EXP) {
                            money -= calcUpgradeCost(Upgrade.MORE_EXP, moreEXPLevel++);
                            report.addInfoStamp(i,
                                    "Upgraded ability: MORE_EXP | Previous level: "+(moreEXPLevel - 1)+" | Current level: "+moreEXPLevel,
                                    "Money before: "+money_before+" | Money after: "+money
                            );
                        }
                        if (upgrade.upgrade() == Upgrade.MORE_MONEY) {
                            money -= calcUpgradeCost(Upgrade.MORE_MONEY, moreMoneyLevel++);
                            report.addInfoStamp(i,
                                    "Upgraded ability: MORE_MONEY | Previous level: "+(moreMoneyLevel - 1)+" | Current level: "+moreMoneyLevel,
                                    "Money before: "+money_before+" | Money after: "+money
                            );
                        }
                        if (upgrade.upgrade() == Upgrade.LONGER_STICK) {
                            money -= calcUpgradeCost(Upgrade.LONGER_STICK, longerStickLevel++);
                            report.addInfoStamp(i,
                                    "Upgraded ability: LONGER_STICK | Previous level: "+(longerStickLevel - 1)+" | Current level: "+longerStickLevel,
                                    "Money before: "+money_before+" | Money after: "+money
                            );
                        }

                        //Checking time manipulations by comparing timestamps
                        long currentTimeDiff = upgrade.action.getClientTimestamp() - upgrade.action.getServerTimestamp();
                        if (Math.abs(currentTimeDiff - timeDiff) > MAX_TIME_DIFF_MILLS) {
                            report.addCheatRate(i, 0.01f, "Time manipulation (Upgrades)! Max diff: "+MAX_TIME_DIFF_MILLS+" | Current diff: "+Math.abs(currentTimeDiff - timeDiff));
                        }

                        //System.out.println("UPGRADE! Location: "+i+" | Type: "+upgrade.upgrade.name());
                        //If player somehow got more money than he physically could
                        if (money < 0) {
                            report.addCheatRate(i, 0.05f, "Money manipulation! Money before: "+money_before+"; Money after upgrades: "+money);
                            money = 0;
                        }
                    }
                }
            }else{
                //Seed on client differs from seed on server!
                report.addCheatRate(i, 1f, "Seed on client differs from seed on server!");
                break;
            }
        }

        //Compare timestamps
        for(Action action : userDetails.getActions()) {
            long currentTimeDiff = action.getClientTimestamp() - action.getServerTimestamp();
            if (Math.abs(currentTimeDiff - timeDiff) > MAX_TIME_DIFF_MILLS) {
                report.addCheatRate(-1, 0.01f, "Time manipulation (Actions)! Max diff: "+MAX_TIME_DIFF_MILLS+" | Current diff: "+Math.abs(currentTimeDiff - timeDiff));
            }
        }

        return report;
    }

    private static Map<Integer, List<UpgradeAction>> mapUpgrades(List<Action> actions){
        actions = actions.stream().filter(action -> action.getAction() == EAction.UPGRADE).toList();
        Map<Integer, List<UpgradeAction>> upgradeMap = new HashMap<>();
        for(Action action : actions){
            try{
                if(upgradeMap.containsKey(action.getLocation() - 1)){
                    //System.out.println("Adding upgrade! "+action.getInfo()+" | Loc: "+action.getLocation());
                    List<UpgradeAction> upgrades = new ArrayList<>(upgradeMap.get(action.getLocation() - 1));
                    //System.out.println("TEST MESSAGE ---------");
                    //System.out.println("Current upgrades: "+upgrades);
                    upgrades.add(new UpgradeAction(Upgrade.valueOf(action.getInfo()), action));
                    //System.out.println("New upgrades: "+upgrades);
                    upgradeMap.put(action.getLocation() - 1, upgrades);
                }else{
                    //System.out.println("Created new list for: "+action.getAction()+" | Loc: "+action.getLocation());
                    upgradeMap.put(action.getLocation() - 1, Collections.singletonList(new UpgradeAction(Upgrade.valueOf(action.getInfo()), action)));
                }
            }catch (Exception ignored){
                ignored.printStackTrace();
            }
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

    /**
     * Calculate time between killing first 7 bosses.
     * Useful for discovering auto-clickers
     * */
    public static List<Float> calcTimeBetweenBosses(){
        return null;
    }

    /**
     * Calculate estimated final time based on given mobs and amount of clicks per second
     * */
    public static float estimateFinalTime(){
        return 0;
    }

    public static void updateActionList(UserRepository userRepository, UserDetailsImpl userDetails, List<Map<String, Object>> comparedActions){
        /*try{
            System.out.println("Comparable: "+comparedActions.size()+" | Actual: "+userDetails.getActions().size());
        }catch (Exception ignored){
            System.out.println("Got null actions!");
        }*/
        if(comparedActions != null && comparedActions.size() > userDetails.getActions().size()){
            for(int i = userDetails.getActions().size(); i < comparedActions.size(); i++){
                try{
                    Map<String, Object> actionMap = comparedActions.get(i);
                    long estimatedTimestamp = ActionUtils.calcSTime(userDetails.getActions().get(0).getServerTimestamp(), userDetails.getActions().get(0).getClientTimestamp(), Long.parseLong((String) actionMap.get("clientTimestamp")));
                    Action action = new Action(EAction.valueOf((String) actionMap.get("action")), (String) actionMap.get("info"), Integer.parseInt((String) actionMap.get("location")), estimatedTimestamp, Long.parseLong((String) actionMap.get("clientTimestamp")));
                    //System.out.println("Created action "+action.getAction()+" with id: "+ action.getId());
                    userDetails.addAction(userRepository, action);
                    //System.out.println("Added action "+action.getAction()+" with id: "+ action.getId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private record UpgradeAction(Upgrade upgrade, Action action) {}
}
