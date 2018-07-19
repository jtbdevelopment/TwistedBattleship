package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Date: 4/20/15
 * Time: 6:49 PM
 */
public class OptionsValidatorTest {
    private OptionsValidator validator = new OptionsValidator();

    @Test
    public void testGameFailsIfMissingAnOptionFromAGroup() {
        GameFeature.getGroupedFeatures().keySet()
                .forEach(groupToExclude ->
                {
                    Set<GameFeature> features = GameFeature.getGroupedFeatures().values()
                            .stream()
                            .filter(group -> !group.get(0).getGroup().equals(groupToExclude))
                            .map(group -> group.get(0))
                            .collect(Collectors.toSet());
                    TBGame game = new TBGame();
                    game.setFeatures(features);
                    assertFalse(validator.validateGame(game));
                });
    }

    @Test
    public void testGameFailsIfUsingGroupingOptions() {
        GameFeature.getGroupedFeatures().keySet()
                .forEach(groupToMessUp ->
                {
                    Set<GameFeature> features = GameFeature.getGroupedFeatures().values()
                            .stream()
                            .filter(group -> !group.get(0).getGroup().equals(groupToMessUp))
                            .map(group -> group.get(0))
                            .collect(Collectors.toSet());
                    TBGame game = new TBGame();
                    features.add(groupToMessUp);
                    game.setFeatures(features);
                    assertFalse(validator.validateGame(game));
                });
    }

    @Test
    public void testGameFailsIfOneGroupHasMultipleOptionsFromOneGroup() {
        GameFeature.getGroupedFeatures().keySet()
                .forEach(groupToMessUp ->
                {
                    Set<GameFeature> features = GameFeature.getGroupedFeatures().values()
                            .stream()
                            .filter(group -> !group.get(0).getGroup().equals(groupToMessUp))
                            .map(group -> group.get(0))
                            .collect(Collectors.toSet());
                    TBGame game = new TBGame();
                    features.addAll(GameFeature.getGroupedFeatures().get(groupToMessUp));
                    game.setFeatures(features);
                    assertFalse(validator.validateGame(game));
                });
    }

    @Test
    public void testAValidGame() {
        Set<GameFeature> features = GameFeature.getGroupedFeatures().entrySet().stream().map(e -> e.getValue().get(0)).collect(Collectors.toSet());
        TBGame game = new TBGame();
        game.setFeatures(features);
        assertTrue(validator.validateGame(game));
    }

    @Test
    public void testErrorMessage() {
        assertEquals(validator.errorMessage(), "Invalid combination of options!");
    }
}
