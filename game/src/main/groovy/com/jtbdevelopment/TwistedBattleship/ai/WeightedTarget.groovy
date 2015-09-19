package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import org.bson.types.ObjectId

/**
 * Date: 9/19/2015
 * Time: 7:11 PM
 */
class WeightedTarget {
    ObjectId player
    GridCoordinate coordinate
    int weight
}
