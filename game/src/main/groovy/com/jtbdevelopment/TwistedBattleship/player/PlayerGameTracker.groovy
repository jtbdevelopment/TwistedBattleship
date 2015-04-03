package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.dao.caching.CacheConstants
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import com.jtbdevelopment.games.publish.PlayerPublisher
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import com.jtbdevelopment.games.tracking.PlayerGameEligibility
import com.jtbdevelopment.games.tracking.PlayerGameEligibilityResult
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

/**
 * Date: 2/4/15
 * Time: 7:02 PM
 */
@Component
@CompileStatic
class PlayerGameTracker implements GameEligibilityTracker<PlayerGameEligibilityResult> {
    private static final Update UPDATE_FREE_GAMES = new Update().inc(TBPlayerAttributes.FREE_GAMES_FIELD, 1)
    private static
    final Update REVERT_FREE_GAMES = new Update().inc(TBPlayerAttributes.FREE_GAMES_FIELD, -1)
    private static
    final Update UPDATE_PAID_GAMES = new Update().inc(TBPlayerAttributes.PAID_GAMES_FIELD, -1)
    private static final Update REVERT_PAID_GAMES = new Update().inc(TBPlayerAttributes.PAID_GAMES_FIELD, 1)
    private static final FindAndModifyOptions RETURN_NEW_OPTION = new FindAndModifyOptions().returnNew(true)

    @Autowired
    MongoOperations mongoOperations

    @Autowired
    PlayerPublisher playerPublisher

    /**
     * Checks eligibility for player and decrements appropriate value
     * Caller should retain value until all actions completed in case rollback is needed
     *
     * @param player
     * @return result that can be checked if player is eligible and to pass in case reverting needed
     */
    @Caching(
            evict = [
                    @CacheEvict(value = CacheConstants.PLAYER_ID_CACHE, key = '#result.player.id'),
                    @CacheEvict(value = CacheConstants.PLAYER_MD5_CACHE, key = '#result.player.md5'),
                    @CacheEvict(value = CacheConstants.PLAYER_S_AND_SID_CACHE, key = '#result.player.sourceAndSourceId')
            ]
    )
    @Override
    PlayerGameEligibilityResult getGameEligibility(final Player player) {
        int freeGames = (player.payLevel == PlayerPayLevel.FreeToPlay ? TBPlayerAttributes.DEFAULT_FREE_GAMES_PER_DAY : TBPlayerAttributes.DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY)

        //  Try free game first
        MongoPlayer updated = (MongoPlayer) mongoOperations.findAndModify(
                Query.query(Criteria.where('_id').is(player.id).and(TBPlayerAttributes.FREE_GAMES_FIELD).lt(freeGames)),
                UPDATE_FREE_GAMES,
                RETURN_NEW_OPTION,
                player.class
        )
        //  TODO - verify if quantity compare necessary and test if so
        if (updated != null &&
                ((TBPlayerAttributes) updated.gameSpecificPlayerAttributes).freeGamesUsedToday != ((TBPlayerAttributes) player.gameSpecificPlayerAttributes).freeGamesUsedToday) {
            playerPublisher.publish(updated)
            return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.FreeGameUsed, player: updated)
        }

        updated = (MongoPlayer) mongoOperations.findAndModify(
                Query.query(Criteria.where('_id').is(player.id).and(TBPlayerAttributes.PAID_GAMES_FIELD).gt(0)),
                UPDATE_PAID_GAMES,
                RETURN_NEW_OPTION,
                player.class
        )
        //  TODO - verify if quantity compare necessary and test if so
        if (updated != null &&
                ((TBPlayerAttributes) updated.gameSpecificPlayerAttributes).availablePurchasedGames != ((TBPlayerAttributes) player.gameSpecificPlayerAttributes).availablePurchasedGames) {
            playerPublisher.publish(updated)
            return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.PaidGameUsed, player: updated)
        }


        return new PlayerGameEligibilityResult(eligibility: PlayerGameEligibility.NoGamesAvailable, player: player)
    }

    /**
     * Revert the result of getGameEligibility
     *
     * @param gameEligibilityResult returned from getGameEligibility
     */
    @Caching(
            evict = [
                    @CacheEvict(value = CacheConstants.PLAYER_ID_CACHE, key = '#p0.player.id'),
                    @CacheEvict(value = CacheConstants.PLAYER_MD5_CACHE, key = '#p0.player.md5'),
                    @CacheEvict(value = CacheConstants.PLAYER_S_AND_SID_CACHE, key = '#p0.player.sourceAndSourceId')
            ]
    )
    @Override
    void revertGameEligibility(final PlayerGameEligibilityResult gameEligibilityResult) {
        Update revertToUse;
        switch (gameEligibilityResult.eligibility) {
            case PlayerGameEligibility.FreeGameUsed:
                revertToUse = REVERT_FREE_GAMES
                break;
            case PlayerGameEligibility.PaidGameUsed:
                revertToUse = REVERT_PAID_GAMES
                break;
        }

        if (revertToUse) {
            MongoPlayer updated = (MongoPlayer) mongoOperations.findAndModify(
                    Query.query(Criteria.where('_id').is(gameEligibilityResult.player.id)),
                    revertToUse,
                    RETURN_NEW_OPTION,
                    gameEligibilityResult.player.class
            )
            if (updated) {
                playerPublisher.publish(updated)
            }
        }
    }
}
