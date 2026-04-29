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

        //Check if first timestamp is INIT
        if(userDetails.getActions().getFirst().getAction() != EAction.INIT){
            report.addCheatRate(1, CheatReport.CheatType.ERROR, "First timestamp is not INIT!");
            return report;
        }

        PlayerInfo info = new PlayerInfo();
        Action registration = userDetails.getActions().get(0);
        long timeDiff = registration.getClientTimestamp() - registration.getServerTimestamp();

        List<MobUtils.MobType> mobs = MobUtils.genMobs(userDetails.getLocationLevel(), userDetails.getMobSeed());
        Map<Integer, List<UpgradeAction>> upgradeMap = mapUpgrades(userDetails.getActions());

        for(; info.location_level < userDetails.getLocationLevel() - 1; info.addLocationLevel()){
            if(info.location_level < mobs.size()){

                //Assign rewards from mob
                int money_increase = (int) /*Location bonus*/ ((getTrueLocLevel(info.location_level + 1) * mobs.get(info.location_level).getMoneyMult()) * /*Ability bonus*/ (1 + (info.more_money_lvl * Upgrade.MORE_MONEY.getAbilityPower())));
                int exp_increase = (int) /*Location bonus*/ ((getTrueLocLevel(info.location_level + 1) * mobs.get(info.location_level).getExpMult()) * /*Ability bonus*/ (1 + (info.more_exp_lvl * Upgrade.MORE_EXP.getAbilityPower())));

                info.addMoney(money_increase);
                info.addExp(exp_increase);

                report.addInfoStamp(info.location_level,
                        "Killed mob: "+mobs.get(info.location_level).name()+" | Current money: "+info.money+" | Current exp: "+info.exp,
                        "Money inc.: "+money_increase+" | Exp inc.: "+exp_increase,
                        "Loc. Exp. bonus: "+(((getTrueLocLevel(info.location_level + 1) * mobs.get(info.location_level).getExpMult()))+" | Ability Exp. bonus: "+((1 + (info.more_exp_lvl * Upgrade.MORE_EXP.getAbilityPower())))),
                        "Loc. Money. bonus: "+(((getTrueLocLevel(info.location_level + 1) * mobs.get(info.location_level).getMoneyMult()))+" | Ability Money bonus: "+((1 + (info.more_money_lvl * Upgrade.MORE_MONEY.getAbilityPower()))))
                );

                //Validate all upgrades that are made on location
                validateUpgrades(upgradeMap, info, report);

            }else{
                //Seed on client differs from seed on server!
                report.addCheatRate(info.location_level, CheatReport.CheatType.SEED, "Seed on client differs from seed on server!");
                return report;
            }
        }

        //Timestamp-related logic
        validateTimestamps(report, userDetails, info, timeDiff);

        return report;
    }

    private static void validateUpgrades(Map<Integer, List<UpgradeAction>> upgradeMap, PlayerInfo info, CheatReport report){
        List<UpgradeAction> upgrades = upgradeMap.get(info.location_level + 1);
        if (upgrades != null && !upgrades.isEmpty()) {
            for (UpgradeAction upgrade : upgrades) {
                int money_before = info.money;
                info.upgradeAbility(upgrade.upgrade, report);
                report.addInfoStamp(info.location_level,
                        "Upgraded ability: "+upgrade.upgrade.name()+" | Previous level: "+(info.getUpgradeLevel(upgrade.upgrade) - 1)+" | Current level: "+info.getUpgradeLevel(upgrade.upgrade),
                        "Money before: "+money_before+" | Money after: "+info.money
                );

                //Check for money manipulation
                //If player somehow got more money than he physically could
                if (info.money < 0) {
                    report.addCheatRate(info.location_level, CheatReport.CheatType.MONEY, "Money before: "+money_before+"; Money after upgrades: "+info.money);
                    info.money = 0;
                }
            }
        }
    }

    private static void validateTimestamps(CheatReport report, UserDetailsImpl userDetails, PlayerInfo info, long timeDiff){
        int checked_amount = 0, killed_bosses = 0, last_location = 1;

        //Compare timestamps
        for(Action action : userDetails.getActions()) {
            //Time consistency check
            long currentTimeDiff = action.getClientTimestamp() - action.getServerTimestamp();
            if (Math.abs(currentTimeDiff - timeDiff) > MAX_TIME_DIFF_MILLS) {
                report.addCheatRate(-1, CheatReport.CheatType.TIME, "Time manipulation (Actions)! Max diff: "+MAX_TIME_DIFF_MILLS+" | Current diff: "+Math.abs(currentTimeDiff - timeDiff));
            }

            //Timestamp order manipulation check
            if(action.getLocation() < last_location){
                report.addCheatRate(-1, CheatReport.CheatType.TIMESTAMP_ORDER, "Incorrect timestamp order on stamp "+action.getAction()+" "+action.getInfo()+" | Recorded location: "+action.getLocation()+" | Last location: "+last_location);
            }
            last_location = action.getLocation();

            //Incorrect boss locations
            //if(){

            //}

            //Auto-clicker detection


            checked_amount++;
        }
    }

    private static Map<Integer, List<UpgradeAction>> mapUpgrades(List<Action> actions){
        actions = actions.stream().filter(action -> action.getAction() == EAction.UPGRADE).toList();
        Map<Integer, List<UpgradeAction>> upgradeMap = new HashMap<>();
        for(Action action : actions){
            try{
                if(upgradeMap.containsKey(action.getLocation() - 1)){
                    List<UpgradeAction> upgrades = new ArrayList<>(upgradeMap.get(action.getLocation() - 1));
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

    /**
     * Calculate time between killing first 7 bosses.
     * Useful for discovering auto-clickers
     * */
    public static List<Float> calcTimeBetweenBosses(){
        return null; //TODO
    }

    /**
     * Calculate estimated final time based on given mobs and amount of clicks per second
     * */
    public static float estimateFinalTime(){
        return 0; //TODO
    }

    public static void updateActionList(UserRepository userRepository, UserDetailsImpl userDetails, List<Map<String, Object>> comparedActions){
        if(comparedActions != null && comparedActions.size() > userDetails.getActions().size()){
            for(int i = userDetails.getActions().size(); i < comparedActions.size(); i++){
                try{
                    Map<String, Object> actionMap = comparedActions.get(i);
                    long estimatedTimestamp = ActionUtils.calcSTime(userDetails.getActions().get(0).getServerTimestamp(), userDetails.getActions().get(0).getClientTimestamp(), Long.parseLong((String) actionMap.get("clientTimestamp")));
                    Action action = new Action(EAction.valueOf((String) actionMap.get("action")), (String) actionMap.get("info"), Integer.parseInt((String) actionMap.get("location")), estimatedTimestamp, Long.parseLong((String) actionMap.get("clientTimestamp")));
                    userDetails.addAction(userRepository, action);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private record UpgradeAction(Upgrade upgrade, Action action) {}

    private static class PlayerInfo{

        private int exp = 0, money = 0, location_level = 0, longer_stick_lvl = 0, more_money_lvl = 0, more_exp_lvl = 0;
        private PlayerInfo(int exp, int money, int location_level, int longer_stick_lvl, int more_money_lvl, int more_exp_lvl){
            this.exp = exp;
            this.money = money;
            this.location_level = location_level;
            this.longer_stick_lvl = longer_stick_lvl;
            this.more_money_lvl = more_money_lvl;
            this.more_exp_lvl = more_exp_lvl;
        }

        /**
         * @return Money left after operation
         * */
        private int upgradeAbility(Upgrade upgrade){
            return switch (upgrade) {
                case MORE_EXP -> addMoney(-calcUpgradeCost(Upgrade.MORE_EXP, more_exp_lvl++));
                case MORE_MONEY -> addMoney(-calcUpgradeCost(Upgrade.MORE_MONEY, more_money_lvl++));
                case LONGER_STICK -> addMoney(-calcUpgradeCost(Upgrade.LONGER_STICK, longer_stick_lvl++));
                default -> money;
            };
        }

        /**
         * Same as 'upgradeAbility()', but caps upgraded at max level
         * and sends report if tried upgrading to higher levels then possible
         * */
        private int upgradeAbility(Upgrade upgrade, CheatReport report){
            switch (upgrade) {
                case MORE_EXP -> {
                    if(more_exp_lvl + 1 >= upgrade.getMaxLevel()){
                        report.addCheatRate(location_level, CheatReport.CheatType.TIMESTAMP_CONTENTS, "Tried upgrading "+upgrade.name()+" to level higher than max!");
                    }else{
                        addMoney(-calcUpgradeCost(Upgrade.MORE_EXP, more_exp_lvl++));
                    }
                }
                case MORE_MONEY -> {
                    if(more_money_lvl + 1 >= upgrade.getMaxLevel()){
                        report.addCheatRate(location_level, CheatReport.CheatType.TIMESTAMP_CONTENTS, "Tried upgrading "+upgrade.name()+" to level higher than max!");
                    }else{
                        addMoney(-calcUpgradeCost(Upgrade.MORE_MONEY, more_money_lvl++));
                    }
                }
                case LONGER_STICK -> {
                    if(longer_stick_lvl + 1 >= upgrade.getMaxLevel()){
                        report.addCheatRate(location_level, CheatReport.CheatType.TIMESTAMP_CONTENTS, "Tried upgrading "+upgrade.name()+" to level higher than max!");
                    }else{
                        addMoney(-calcUpgradeCost(Upgrade.LONGER_STICK, longer_stick_lvl++));
                    }
                }
            };

            return money;
        }

        private int getUpgradeLevel(Upgrade upgrade){
            return switch (upgrade) {
                case MORE_EXP -> more_exp_lvl;
                case MORE_MONEY -> more_money_lvl;
                case LONGER_STICK -> longer_stick_lvl;
                default -> 0;
            };
        }

        private void setUpgradeLevel(Upgrade upgrade, int level){
            switch (upgrade) {
                case MORE_EXP -> more_exp_lvl = level;
                case MORE_MONEY -> more_money_lvl = level;
                case LONGER_STICK -> longer_stick_lvl = level;
            };
        }

        private PlayerInfo(){}

        public void addExp(int exp){
            this.exp += exp;
        }

        public int addMoney(int money){
            this.money += money;
            return this.money;
        }

        public void addLocationLevel(){
            this.location_level++;
        }

        public void addLongerStick(){
            this.longer_stick_lvl++;
        }

        public void addMoreMoney(){
            this.more_money_lvl++;
        }

        public void addMoreExp(){
            this.more_exp_lvl++;
        }
    }
}
