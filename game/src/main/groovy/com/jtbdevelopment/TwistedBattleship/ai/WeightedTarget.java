package com.jtbdevelopment.TwistedBattleship.ai;

import com.jtbdevelopment.TwistedBattleship.rest.Target;

/**
 * Date: 9/19/2015
 * Time: 7:11 PM
 */
public class WeightedTarget extends Target {
    private Action action;
    private int weight;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public enum Action {
        Fire, ECM, Spy, Repair, Move, CruiseMissile
    }
}
