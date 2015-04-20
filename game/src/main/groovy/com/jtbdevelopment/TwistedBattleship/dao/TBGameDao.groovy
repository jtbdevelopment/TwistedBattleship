package com.jtbdevelopment.TwistedBattleship.dao

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.dao.AbstractMongoMultiPlayerGameRepository
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable

import java.time.ZonedDateTime

/**
 * Date: 4/2/15
 * Time: 10:16 PM
 */
@CompileStatic
interface TBGameDao extends AbstractMongoMultiPlayerGameRepository<GameFeature, TBGame> {
    //  TODO - move to core - see comment there
    //  TODO - also move PlayerGamesFinderHandler then
    List<TBGame> findByPlayersIdAndGamePhaseAndLastUpdateGreaterThan(
            final ObjectId id, final GamePhase gamePhase, final ZonedDateTime cutoff, final Pageable pageable)

}