package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.player.tracking.AbstractPlayerGameTrackingAttributes
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.players.PlayerPayLevel
import groovy.transform.CompileStatic
import org.springframework.data.annotation.AccessType
import org.springframework.data.annotation.Transient

/**
 * Date: 1/30/15
 * Time: 6:34 PM
 */
@CompileStatic
class TBPlayerAttributes extends AbstractPlayerGameTrackingAttributes {
    private static final String DEFAULT_THEME = 'default-theme'
    private static final String PIRATE_THEME = 'pirate-theme'
    private static final Set<String> FREE_THEMES = [DEFAULT_THEME, PIRATE_THEME] as Set
    public static final int DEFAULT_FREE_GAMES_PER_DAY = 50;
    public static final int DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY = 100;

    @Transient
    int maxDailyFreeGames

    String theme = DEFAULT_THEME

    @AccessType(AccessType.Type.PROPERTY)
    Set<String> availableThemes = FREE_THEMES

    @SuppressWarnings("GroovyUnusedDeclaration")
    void setAvailableThemes(final Set<String> availableThemes) {
        this.availableThemes.addAll(availableThemes)
    }

//  TODO - title, win streak, max points etc

    @Transient
    @Override
    void setPlayer(final Player player) {
        super.setPlayer(player)
        maxDailyFreeGames = (player.payLevel == PlayerPayLevel.FreeToPlay ? DEFAULT_FREE_GAMES_PER_DAY : DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY)
    }
}
