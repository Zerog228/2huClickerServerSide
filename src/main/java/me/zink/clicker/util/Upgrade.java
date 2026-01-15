package me.zink.clicker.util;

import lombok.Getter;
import org.antlr.v4.runtime.misc.Pair;

public enum Upgrade {
    LONGER_STICK(5, 15, 1, 15, 1f), //Damage upgrade
    MORE_EXP(3, 50, 1, 50, 0.1f),
    MORE_MONEY(3, 100, 1, 100, 0.1f);

    private int max_level, cost, cost_in_points, additional_cost_per_level;
    private float ability_power;

    Upgrade(int max_level, int cost, int cost_in_points, int additional_cost_per_level, float ability_power){
        this.max_level = max_level;
        this.cost = cost;
        this.cost_in_points = cost_in_points;
        this.additional_cost_per_level = additional_cost_per_level;

        this.ability_power = ability_power;
    }

    public int getMaxLevel() {
        return max_level;
    }

    public int getCost() {
        return cost;
    }

    public int getCostInPoints() {
        return cost_in_points;
    }

    public int getAdditionalCostPerLevel() {
        return additional_cost_per_level;
    }

    public float getAbilityPower() {
        return ability_power;
    }

    @Getter
    public enum Message{
        SUCCESS("Upgraded successfully!", true),
        F_NOT_ENOUGH_MONEY("Not enough money!"),
        F_MAX_LEVEL("Already upgraded to max level!"),
        F_ABILITY_NOT_FOUND("Ability not found!"),
        ;

        private final String message;
        private final boolean success;

        Message(String message){
            this(message, false);
        }

        Message(String message, boolean success){
            this.message = message;
            this.success = success;
        }

        public Pair<Boolean, String> asPair(){
            return new Pair<>(success, message);
        }
    }
};
