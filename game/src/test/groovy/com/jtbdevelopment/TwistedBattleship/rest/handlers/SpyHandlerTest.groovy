package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 5/15/15
 * Time: 6:56 AM
 */
class SpyHandlerTest extends MongoGameCoreTestCase {
    SpyHandler handler = new SpyHandler()

    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesNoSpiesRemain() {
        TBGame game = new TBGame(
                playerDetails: [
                        (PONE.id): new TBPlayerState(spysRemaining: 1)
                ]
        )
        handler.validateMoveSpecific(PONE, game, PTWO, null)
        game.playerDetails[PONE.id].spysRemaining = 0
        shouldFail(NoSpyActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PTWO, null)
        })
    }

    /*
    void testDrawGrid() {
        TBGame game = new TBGame(features: [GameFeature.Grid20x20])
        Grid grid = new Grid(20)
        GridCoordinate start = new GridCoordinate(1, 1)
        GridSizeUtil util = new GridSizeUtil()
        def x = SpyHandler.SPY_CIRCLE.collectMany {
            int size, ArrayList<GridCoordinate> adjust ->
                adjust.collect {
                    GridCoordinate adjustCoord ->
                        start.add(adjustCoord)
                }
        }.findAll {GridCoordinate it->  util.isValidCoordinate(game, it)}.each {
            GridCoordinate coordinate ->
                grid.set(coordinate, GridCellState.KnownShip)
        }
        (0..19).each {
            int row ->
                (0..19).each {
                    int col ->
                        if(row == start.row && col ==start.column) {
                            print "C"
                        } else {
                            if (grid.get(row, col) == GridCellState.Unknown) {
                                print "U"
                            } else {
                                print "X"
                            }
                        }
                }
                println ""
        }
    }
    */
}
