package com.jtbdevelopment.TwistedBattleship.player;

import com.jtbdevelopment.games.player.tracking.AbstractPlayerGameTrackingAttributes;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.players.PlayerPayLevel;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 1/30/15
 * Time: 6:34 PM
 */
public class TBPlayerAttributes extends AbstractPlayerGameTrackingAttributes {
    private static final String DEFAULT_THEME = "default-theme";
    private static final String PIRATE_THEME = "pirate-theme";
    private static final Set<String> FREE_THEMES = new HashSet<>(Arrays.asList(DEFAULT_THEME, PIRATE_THEME));
    private static final int DEFAULT_FREE_GAMES_PER_DAY = 50;
    private static final int DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY = 100;
    @Transient
    private int maxDailyFreeGames;
    private String theme = DEFAULT_THEME;
    @AccessType(AccessType.Type.PROPERTY)
    private Set<String> availableThemes = new HashSet<>(FREE_THEMES);
    private int wins;
    private int losses;
    private int currentWinStreak;
    private int highestScore;

    @Transient
    @java.beans.Transient
    @Override
    public void setPlayer(final Player player) {
        super.setPlayer(player);
        maxDailyFreeGames = (player.getPayLevel().equals(PlayerPayLevel.FreeToPlay) ? DEFAULT_FREE_GAMES_PER_DAY : DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY);
    }

    @Transient
    @java.beans.Transient
    public int getMaxDailyFreeGames() {
        return maxDailyFreeGames;
    }

    public void setMaxDailyFreeGames(int maxDailyFreeGames) {
        this.maxDailyFreeGames = maxDailyFreeGames;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @AccessType(AccessType.Type.PROPERTY)
    public Set<String> getAvailableThemes() {
        return availableThemes;
    }

    public void setAvailableThemes(final Set<String> availableThemes) {
        this.availableThemes.addAll(availableThemes);
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public void setCurrentWinStreak(int currentWinStreak) {
        this.currentWinStreak = currentWinStreak;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }
}
