package com.jtbdevelopment.TwistedBattleship.state.masked;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.ConsolidateGridViews;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.masking.AbstractMultiPlayerGameMasker;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Date: 4/2/15
 * Time: 6:44 PM
 */
@Component
public class TBGameMasker extends AbstractMultiPlayerGameMasker<ObjectId, GameFeature, TBGame, TBMaskedGame> {
    @Autowired
    private ConsolidateGridViews consolidateGridViews;

    @Override
    protected TBMaskedGame newMaskedGame() {
        return new TBMaskedGame();
    }

    @Override
    public Class<ObjectId> getIDClass() {
        return ObjectId.class;
    }

    @Override
    protected void copyUnmaskedData(final TBGame game, final TBMaskedGame masked) {
        super.copyUnmaskedData(game, masked);
        masked.setRemainingMoves(game.getRemainingMoves());
        masked.setMovesForSpecials(game.getMovesForSpecials());
        masked.setGridSize(game.getGridSize());
        masked.setStartingShips(game.getStartingShips());
    }

    @Override
    protected void copyMaskedData(final TBGame game, final Player<ObjectId> player, final TBMaskedGame masked, final Map<ObjectId, Player<ObjectId>> idMap) {
        super.copyMaskedData(game, player, masked, idMap);
        masked.setMaskedPlayersState(createMaskedPlayerState(game.getPlayerDetails().get(player.getId()), idMap));
        Grid consolidatedView = consolidateGridViews.createConsolidatedView(
                game,
                new HashSet<>(masked.getMaskedPlayersState().getOpponentViews().values()));
        masked.getMaskedPlayersState().setConsolidatedOpponentView(
                consolidatedView);
        masked.setCurrentPlayer(idMap.get(game.getCurrentPlayer()).getMd5());
        game.getPlayerDetails().forEach((playerId, state) -> {
            String md5 = idMap.get(playerId).getMd5();
            masked.getPlayersAlive().put(md5, state.isAlive());
            masked.getPlayersScore().put(md5, state.getTotalScore());
            masked.getPlayersSetup().put(md5, state.isSetup());
        });
        masked.setWinningPlayer(idMap.get(game.getPlayerDetails().entrySet()
                .stream().max(Comparator.comparingInt(a -> a.getValue().getTotalScore()))
                .get()
                .getKey()).getMd5());
    }

    private TBMaskedPlayerState createMaskedPlayerState(final TBPlayerState playerState, final Map<ObjectId, Player<ObjectId>> idMap) {
        TBMaskedPlayerState maskedPlayerState = new TBMaskedPlayerState();

        maskedPlayerState.setShipStates(playerState.getShipStates());
        maskedPlayerState.setActiveShipsRemaining(playerState.getActiveShipsRemaining());

        maskedPlayerState.setTotalScore(playerState.getTotalScore());
        maskedPlayerState.setAlive(playerState.isAlive());
        maskedPlayerState.setSetup(playerState.isSetup());
        maskedPlayerState.setTotalScore(playerState.getTotalScore());
        maskedPlayerState.setEcmsRemaining(playerState.getEcmsRemaining());
        maskedPlayerState.setEmergencyRepairsRemaining(playerState.getEmergencyRepairsRemaining());
        maskedPlayerState.setEvasiveManeuversRemaining(playerState.getEvasiveManeuversRemaining());
        maskedPlayerState.setSpysRemaining(playerState.getSpysRemaining());
        maskedPlayerState.setCruiseMissilesRemaining(playerState.getCruiseMissilesRemaining());
        maskedPlayerState.setStartingShips(playerState.getStartingShips());
        maskedPlayerState.setActionLog(
                playerState.getActionLog().stream()
                        .map(log -> new TBMaskedActionLogEntry(
                                convertTime(log.getTimestamp()),
                                log.getActionType(),
                                log.getDescription()))
                        .collect(Collectors.toList())
        );
        maskedPlayerState.setOpponentGrids(
                playerState.getOpponentGrids().entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> idMap.get(e.getKey()).getMd5(), Map.Entry::getValue))
        );
        maskedPlayerState.setOpponentViews(
                playerState.getOpponentViews().entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> idMap.get(e.getKey()).getMd5(), Map.Entry::getValue))
        );

        return maskedPlayerState;
    }

    void setConsolidateGridViews(ConsolidateGridViews consolidateGridViews) {
        this.consolidateGridViews = consolidateGridViews;
    }
}
