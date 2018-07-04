package com.jtbdevelopment.TwistedBattleship.state;

import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import com.jtbdevelopment.games.state.transition.AbstractMPGamePhaseTransitionEngine;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 4/22/15
 * Time: 9:07 PM
 */
@Component
public class GamePhaseTransitionEngine extends AbstractMPGamePhaseTransitionEngine<ObjectId, GameFeature, TBGame> {
    GamePhaseTransitionEngine(final GameScorer<TBGame> gameScorer) {
        super(gameScorer);
    }

    @Override
    protected TBGame evaluateSetupPhase(final TBGame game) {
        if (game.getPlayerDetails().values().stream().allMatch(TBPlayerState::isSetup)) {
            TBActionLogEntry entry = new TBActionLogEntry(
                    TBActionLogEntry.TBActionType.Begin,
                    "Game ready to play."
            );
            game.getPlayerDetails().values().forEach(ps -> ps.getActionLog().add(entry));
            return changeStateAndReevaluate(GamePhase.Playing, game);
        } else {
            return game;
        }
    }

    @Override
    protected TBGame evaluatePlayingPhase(final TBGame game) {
        if (game.getPlayerDetails().values().stream().filter(TBPlayerState::isAlive).count() != 1) {
            return game;
        }

        ObjectId winnerId = game.getPlayerDetails().entrySet().stream().filter(e -> e.getValue().isAlive()).findFirst().get().getKey();
        Player<ObjectId> winner = game.getPlayers().stream().filter(p -> p.getId().equals(winnerId)).findFirst().get();
        TBActionLogEntry entry = new TBActionLogEntry(
                TBActionLogEntry.TBActionType.Victory,
                winner.getDisplayName() + " defeated all challengers!"
        );
        game.getPlayerDetails().values().forEach(ps -> ps.getActionLog().add(entry));

        game.getPlayerDetails().forEach((myId, myState) -> {
            myState.getOpponentGrids().forEach((opponent, myGrid) -> {
                TBPlayerState theirState = game.getPlayerDetails().get(opponent);
                Grid theirGrid = theirState.getOpponentViews().get(myId);
                theirState.getShipStates().forEach((shipState) -> {
                    for (int i = 0; i < shipState.getShip().getGridSize(); ++i) {
                        GridCellState cellState = shipState.getShipSegmentHit().get(i) ? GridCellState.HiddenHit : GridCellState.RevealedShip;
                        GridCoordinate coordinate = shipState.getShipGridCells().get(i);
                        if (myGrid.get(coordinate).getRank() < cellState.getRank()) {
                            myGrid.set(coordinate, cellState);
                            theirGrid.set(coordinate, cellState);
                        }
                    }
                });
            });
        });
        return changeStateAndReevaluate(GamePhase.RoundOver, game);
    }
}
