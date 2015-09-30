package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.rest.Target

/**
 * Date: 9/19/2015
 * Time: 7:11 PM
 */
class WeightedTarget extends Target {
    enum Action {
        Fire,
        ECM,
        Spy,
        Repair,
        Move
    }
    Action action
    int weight
}
