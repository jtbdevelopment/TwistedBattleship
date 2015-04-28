package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.dao.GameRepository
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameGetterHandler
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import com.jtbdevelopment.games.state.masking.MultiPlayerGameMasker
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Date: 11/19/14
 * Time: 7:08 AM
 */
@Component
@CompileStatic
class PlayerGamesFinderHandler extends AbstractGameGetterHandler {
    private static int DEFAULT_PAGE_SIZE = 20;
    private static int DEFAULT_PAGE = 0;
    public static final ZoneId GMT = ZoneId.of("GMT")
    public static final Sort SORT = new Sort(Sort.Direction.DESC, ["lastUpdate", "created"])
    public static final PageRequest PAGE = new PageRequest(DEFAULT_PAGE, DEFAULT_PAGE_SIZE, SORT)

    @Autowired
    protected MultiPlayerGameMasker gameMasker

    public List<MaskedMultiPlayerGame> findGames(final ObjectId playerID) {
        Player<ObjectId> player = loadPlayer(playerID);
        ZonedDateTime now = ZonedDateTime.now(GMT)

        List<MaskedMultiPlayerGame> result = [];
        //  TODO - Would be nice to be parallel
        //  GPars and compile static not nice
        //  JDK1.8 streams and this build seemed to have issues
        //  shelving for now
        GamePhase.values().each {
            GamePhase phase ->
                def days = now.minusDays(phase.historyCutoffDays)
                result.addAll(((GameRepository) gameRepository).findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(
                        (ObjectId) player.id,
                        phase,
                        days,
                        PAGE
                ).collect {
                    TBGame game ->
                        gameMasker.maskGameForPlayer(game, player)
                })
        }
        result
    }
}
