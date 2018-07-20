package com.jtbdevelopment.TwistedBattleship.ai;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.games.dao.AbstractMultiPlayerGameRepository;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.publish.GameListener;
import com.jtbdevelopment.games.rest.handlers.ChallengeResponseHandler;
import com.jtbdevelopment.games.rest.handlers.QuitHandler;
import com.jtbdevelopment.games.state.PlayerState;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Date: 9/18/15
 * Time: 10:17 PM
 */
@SuppressWarnings("unused")
@Component
public class AIGameListener implements GameListener<TBGame, MongoPlayer> {
    private static Logger logger = LoggerFactory.getLogger(AIGameListener.class);
    private final Map<ObjectId, Integer> problemGames = new HashMap<>();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 25, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    private final ChallengeResponseHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> challengeResponseHandler;
    private final AbstractMultiPlayerGameRepository<ObjectId, GameFeature, TBGame> gameRepository;
    private final List<AI> aiList;
    private Map<ObjectId, AI> playerIDAIMap = new HashMap<>();
    private Map<ObjectId, MongoPlayer> playerIDPlayerMap = new HashMap<>();
    private List<MongoPlayer> aiPlayers = new ArrayList<>();
    private Set<ObjectId> aiIDs = new HashSet<>();
    private int maxAttempts = 10;

    public AIGameListener(ChallengeResponseHandler<ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> challengeResponseHandler, QuitHandler quitHandler, AbstractMultiPlayerGameRepository<ObjectId, GameFeature, TBGame> gameRepository, List<AI> aiList) {
        this.challengeResponseHandler = challengeResponseHandler;
        this.gameRepository = gameRepository;
        this.aiList = aiList;
    }

    @PostConstruct
    public void setup() {
        aiList.forEach(ai -> ai.getPlayers().forEach(aiPlayer -> {
            playerIDAIMap.put(aiPlayer.getId(), ai);
            aiPlayers.add(aiPlayer);
            aiIDs.add(aiPlayer.getId());
            playerIDPlayerMap.put(aiPlayer.getId(), aiPlayer);
        }));
    }

    private ObjectId playerToAccept(final TBGame game) {
        Optional<ObjectId> first = game.getPlayerStates().entrySet()
                .stream()
                .filter(e -> e.getValue().equals(PlayerState.Pending))
                .map(Map.Entry::getKey)
                .filter(id -> aiIDs.contains(id))
                .findFirst();
        return first.orElse(null);
    }

    private ObjectId playerToSetup(final TBGame game) {
        Optional<ObjectId> first = game.getPlayerDetails().entrySet()
                .stream()
                .filter(e -> !e.getValue().isSetup())
                .map(Map.Entry::getKey)
                .filter(id -> aiIDs.contains(id))
                .findFirst();
        return first.orElse(null);
    }

    private boolean hasAIWorkToDo(final TBGame game) {
        switch (game.getGamePhase()) {
            case Challenged:
                return playerToAccept(game) != null;
            case Setup:
                return playerToSetup(game) != null;
            case Playing:
                return aiIDs.contains(game.getCurrentPlayer());
        }
        return false;
    }

    @Override
    public void gameChanged(final TBGame game, final MongoPlayer initiatingPlayer, final boolean initiatingServer) {
        if (initiatingServer) {
            if (game.getPlayers().stream().anyMatch(p -> aiIDs.contains(p.getId()))) {
                if (hasAIWorkToDo(game)) {
                    executor.execute(new Worker(game));
                }
            }
        }

    }

    private class Worker implements Runnable {
        private TBGame game;

        Worker(final TBGame game) {
            this.game = game;
        }

        @Override
        public void run() {
            try {
                //  Too fast and UI can't tell difference between updates since only looks at second level
                game = gameRepository.findById(game.getId()).get();
                while ((Instant.now().toEpochMilli() - game.getLastUpdate().toEpochMilli()) < 250) {
                    Thread.sleep(0, 250);
                    game = gameRepository.findById(game.getId()).get();
                }

                logger.debug("AI Playing " + game.getId());
                switch (game.getGamePhase()) {
                    case Challenged:
                        ObjectId aiToAccept = playerToAccept(game);
                        if (aiToAccept != null) {
                            challengeResponseHandler.handleAction(aiToAccept, game.getId(), PlayerState.Accepted);
                        }

                        break;
                    case Setup:
                        ObjectId aiToSetup = playerToSetup(game);
                        if (aiToSetup != null) {
                            playerIDAIMap.get(aiToSetup).setup(game, playerIDPlayerMap.get(aiToSetup));
                        }

                        break;
                    case Playing:
                        if (aiIDs.contains(game.getCurrentPlayer())) {
                            playerIDAIMap.get(game.getCurrentPlayer()).playOneMove(game, playerIDPlayerMap.get(game.getCurrentPlayer()));
                        }

                        break;
                }
                problemGames.remove(game.getId());
                logger.debug("AI Playing Completed " + game.getId());
            } catch (Exception ex) {
                logger.warn("There was a problem playing ", ex);
                if (problemGames.getOrDefault(game.getId(), 0) != 0) {
                    int attempts = problemGames.get(game.getId());
                    if (attempts < maxAttempts) {
                        attempts = ++attempts;
                        problemGames.put(game.getId(), attempts);
                        logger.info("Re-queueing for another attempt " + game.getId());
                        executor.execute(new Worker(game));
                    } else {
                        //  TODO - quit it?
                        logger.warn("Game has run out of attempts " + game.getId());
                        problemGames.get(game.getId());
                    }

                }

            }

        }
    }
}
