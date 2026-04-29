package me.zink.clicker.util;

import me.zink.clicker.model.Action;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CheatReport {

    private final long user_id;
    private final String username;
    private final int location_level;
    private Set<Action> actions = new HashSet<>();
    private final Set<Pair<Integer, Upgrade>> upgrades = new HashSet<>();
    private final List<CheatStamp> cheatStamps = new ArrayList<>();
    private final List<InfoStamp> infoStamps = new ArrayList<>();

    private float cheat_rate = 0;

    public CheatReport(long user_id, String username, int max_location_level){
        this.user_id = user_id;
        this.username = username;
        this.location_level = max_location_level;
    }

    public void addAction(Action action){
        actions.add(action);
    }

    public void addAction(Action ... action){
        actions.addAll(Arrays.stream(action).toList());
    }

    public void addAction(List<Action> action){
        actions.addAll(action);
    }

    private void sortActions(){
        actions = actions.stream().sorted((o1, o2) -> {
            if(o1.getLocation() == o2.getLocation()){
                return 0;
            }else{
                return o1.getLocation() < o2.getLocation() ? -1 : 1;
            }
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void addUpgrade(Upgrade upgrade, int level){
        upgrades.add(Pair.of(level, upgrade));
    }

    public void addCheatRate(int location_level, CheatType cheatType, String comment){
        cheat_rate += cheatType.severity;
        cheatStamps.add(new CheatStamp(location_level, cheatType, comment));
    }

    public void addInfoStamp(int location_level, String ... info){
        infoStamps.add(new InfoStamp(location_level, info));
    }

    private void sortInfoStamps(){
        infoStamps.sort((s1, s2) -> {
            if(s1.location_level == s2.location_level()){
                return 0;
            }else{
                return s1.location_level() < s2.location_level() ? -1 : 1;
            }
        });
    }

    public String genReport() {
        return genReport(false);
    }

    public String genReport(boolean genChronology) {
        sortActions();
        sortInfoStamps();

        StringBuilder builder = new StringBuilder();
        builder.append("======================\n");
        builder.append("ID: ").append(user_id).append("\n");
        builder.append("Username: ").append(username).append("\n");
        builder.append("Location level: ").append(location_level).append("\n");
        builder.append("Cheat rate: ").append(cheat_rate).append("\n");
        if(genChronology){
            builder.append("------Chronology------\n");
            for(InfoStamp stamp : infoStamps){
                builder.append("--------- location: ").append(stamp.location_level).append(" ----------\n");
                for(String info : stamp.info){
                    builder.append(info).append("\n");
                }
            }
        }
        builder.append("--------Actions-------\n");
        for(Action action : actions){
            builder.append("--------- id: ").append(action.getId()).append(" ----------\n");
            builder.append("Type: ").append(action.getAction()).append(" | Location: ").append(action.getLocation()).append(" | Info: ").append(action.getInfo()).append("\n");
            builder.append("Client Timestamp: ").append(action.getClientTimestamp()).append("\n");
            builder.append("Server Timestamp: ").append(action.getServerTimestamp()).append("\n");
            builder.append("Timestamp diff: ").append((action.getServerTimestamp() - action.getClientTimestamp())).append("\n");
        }
        builder.append("----------------------\n");
        builder.append("-----Cheat Stamps-----\n");
        for(CheatStamp stamp : cheatStamps){
            builder.append("Location: ").append(stamp.location_level).append(" | Rate: ").append(stamp.cheatType.severity).append(" | Comment: ").append(stamp.comment).append("\n");
        }

        return builder.toString();
    }

    private record CheatStamp(int location_level, CheatType cheatType, String comment){
        CheatStamp(int location_level, CheatType cheatType){
            this(location_level, cheatType, "No additional comment");
        }

        public String getMessage(){
            return cheatType.message + " " + comment;
        }
    }

    private record InfoStamp(int location_level, String ... info){

    }

    public enum CheatType{
        TIME(0.1, "Time manipulation!"),
        MONEY(0.2, "Money manipulation!"),
        TIMESTAMP_ORDER(0.5, "Incorrect timestamp order!"),
        TIMESTAMP_CONTENTS(0.5, "Incorrect timestamp content!"),
        CLICKER(0.1, "Auto-clicker!"),

        ERROR(100, "Fatal error!"),
        SEED(100, "Seed manipulation!");

        private final double severity;
        private final String message;

        CheatType(double severity, String message){
            this.severity = severity;
            this.message = message;
        }
    }

}
