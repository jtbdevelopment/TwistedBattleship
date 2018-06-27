package com.jtbdevelopment.TwistedBattleship.state.grid;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.LinkedHashMap;

import static groovy.util.GroovyTestCase.assertEquals;

/**
 * Date: 9/29/2015
 * Time: 10:07 PM
 */
public class ConsolidateGridViewsTest extends MongoGameCoreTestCase {
    private ConsolidateGridViews consolidateGridViews = new ConsolidateGridViews();

    @Test
    public void testConsolidate() {
        TBGame game = new TBGame();

        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(3);
        TBPlayerState state1 = new TBPlayerState();
        LinkedHashMap<ObjectId, Grid> map1 = new LinkedHashMap<>(2);
        map1.put(PTWO.getId(), new Grid(10));
        map1.put(PTHREE.getId(), new Grid(10));
        state1.setOpponentViews(map1);
        map.put(PONE.getId(), state1);
        map.put(PTWO.getId(), new TBPlayerState());
        map.put(PTHREE.getId(), new TBPlayerState());
        game.setGridSize(10);
        game.setPlayerDetails(map);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 0, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 1, GridCellState.KnownByMiss);

        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 2, GridCellState.Unknown);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 2, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 3, GridCellState.KnownShip);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 3, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTWO.getId()).set(5, 4, GridCellState.KnownByHit);
        game.getPlayerDetails().get(PONE.getId()).getOpponentViews().get(PTHREE.getId()).set(5, 4, GridCellState.KnownByRehit);

        Grid consolidatedOpponentView = consolidateGridViews.createConsolidatedView(game, ((LinkedHashMap<ObjectId, Grid>) game.getPlayerDetails().get(PONE.getId()).getOpponentViews()).values());
        for (int row = 0; row < 10; row = ++row) {
            for (int col = 0; col < 10; col = ++col) {
                GridCellState state = consolidatedOpponentView.get(row, col);
                if (row != 5 || col > 4) {
                    assert state.equals(GridCellState.Unknown);
                } else {
                    switch (col) {
                        case 0:
                            assertEquals(GridCellState.KnownByHit, state);
                            break;
                        case 1:
                            assertEquals(GridCellState.KnownByMiss, state);
                            break;
                        case 2:
                            assertEquals(GridCellState.KnownShip, state);
                            break;
                        case 3:
                            assertEquals(GridCellState.KnownByHit, state);
                            break;
                        case 4:
                            assertEquals(GridCellState.KnownByHit, state);
                            break;
                    }
                }

            }

        }


    }
}
