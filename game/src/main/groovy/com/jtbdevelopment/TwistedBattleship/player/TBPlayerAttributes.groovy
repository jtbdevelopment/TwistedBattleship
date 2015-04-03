package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes
import groovy.transform.CompileStatic
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Date: 1/30/15
 * Time: 6:34 PM
 */
@CompileStatic
@Document
class TBPlayerAttributes implements GameSpecificPlayerAttributes {
    public static final String FREE_GAMES_FIELD = 'gameSpecificPlayerAttributes.freeGamesUsedToday'
    public static final String PAID_GAMES_FIELD = 'gameSpecificPlayerAttributes.availablePurchasedGames'
    public static final int DEFAULT_FREE_GAMES_PER_DAY = 10;
    public static final int DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY = 25;

    int freeGamesUsedToday = 0
    int availablePurchasedGames = 0
}
