package com.jtbdevelopment.TwistedBattleship.rest.services.messages;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;

import java.util.List;
import java.util.Set;

/**
 * Date: 4/30/15
 * Time: 12:11 PM
 */
public class FeaturesAndPlayers {
    private List<String> players;
    private Set<GameFeature> features;

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public Set<GameFeature> getFeatures() {
        return features;
    }

    public void setFeatures(Set<GameFeature> features) {
        this.features = features;
    }
}
