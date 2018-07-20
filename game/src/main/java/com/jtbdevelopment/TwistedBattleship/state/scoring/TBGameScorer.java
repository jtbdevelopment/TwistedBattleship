package com.jtbdevelopment.TwistedBattleship.state.scoring;

import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.SystemPlayer;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import com.jtbdevelopment.games.state.scoring.GameScorer;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 4/27/15
 * Time: 6:25 PM
 */
@Component
public class TBGameScorer implements GameScorer<TBGame> {
    public static final int SCORE_FOR_HIT = 1;
    public static final int SCORE_FOR_SINK = 5;
    private static final int SCORE_FOR_VICTORY = 10;
    private final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository;
    private final PlayerPublisher playerPublisher;

    TBGameScorer(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final PlayerPublisher playerPublisher) {
        this.playerRepository = playerRepository;
        this.playerPublisher = playerPublisher;
    }

    @Override
    public TBGame scoreGame(final TBGame game) {
        game.getPlayerDetails().values()
                .stream()
                .filter(TBPlayerState::isAlive)
                .forEach(e -> e.setScoreFromLiving(e.getScoreFromLiving() + SCORE_FOR_VICTORY));

        game.getPlayers()
                .stream()
                .filter(p -> !(p instanceof SystemPlayer))
                .map(p -> playerRepository.findById(p.getId()).get())
                .peek(player -> {
                    TBPlayerAttributes attributes = player.getGameSpecificPlayerAttributes();
                    TBPlayerState state = game.getPlayerDetails().get(player.getId());
                    if (state.isAlive()) {
                        attributes.setWins(attributes.getWins() + 1);
                        attributes.setCurrentWinStreak(attributes.getCurrentWinStreak() + 1);

                    } else {
                        attributes.setLosses(attributes.getLosses() + 1);
                        attributes.setCurrentWinStreak(0);
                    }

                    attributes.setHighestScore(Math.max(attributes.getHighestScore(), state.getTotalScore()));
                })
                .map(p -> playerRepository.save(p))
                .forEach(p -> playerPublisher.publish(p));

        return game;
    }

}
