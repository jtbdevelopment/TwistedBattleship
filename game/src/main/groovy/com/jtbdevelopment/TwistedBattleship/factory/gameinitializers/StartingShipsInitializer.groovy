package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
@CompileStatic
class StartingShipsInitializer implements GameInitializer<TBGame> {
    final int order = DEFAULT_ORDER

    @Override
    void initializeGame(final TBGame game) {
        GameFeature ships = game.features.find { it.group == GameFeature.StartingShips }

        Ship ship;
        switch (ships) {
            case GameFeature.StandardShips:
                game.startingShips = Ship.values().toList()
                return
            case GameFeature.AllCruisers:
                ship = Ship.Cruiser
                break
            case GameFeature.AllSubmarines:
                ship = Ship.Submarine
                break
            case GameFeature.AllCarriers:
                ship = Ship.Carrier
                break
            case GameFeature.AllDestroyers:
                ship = Ship.Destroyer
                break
            case GameFeature.AllBattleships:
                ship = Ship.Battleship
                break;
        }
        game.startingShips = [ship, ship, ship, ship, ship]
    }
}
