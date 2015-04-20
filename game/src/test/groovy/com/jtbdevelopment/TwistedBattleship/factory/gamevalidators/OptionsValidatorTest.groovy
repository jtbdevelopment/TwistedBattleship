package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame

/**
 * Date: 4/20/15
 * Time: 6:49 PM
 */
class OptionsValidatorTest extends GroovyTestCase {
    OptionsValidator validator = new OptionsValidator()

    void testGameFailsIfMissingAnOptionFromAGroup() {
        Random random = new Random()
        GameFeature.groupedFeatures.each {
                //  This is the group we will skip
            GameFeature skipGroup, List<GameFeature> ignoredOptions ->
                Set<GameFeature> features = [] as Set
                GameFeature.groupedFeatures.findAll {
                    GameFeature group, List<GameFeature> options ->
                        group != skipGroup
                }.each {
                    GameFeature group, List<GameFeature> options ->
                        features.add(options[random.nextInt(options.size() - 1)])
                }
                TBGame game = new TBGame(features: features)
                assertFalse validator.validateGame(game)
        }
    }

    void testGameFailsIfUsingGroupingOptions() {
        Random random = new Random()
        GameFeature.groupedFeatures.each {
                //  This is the group we will use grouping key
            GameFeature skipGroup, List<GameFeature> ignoredOptions ->
                Set<GameFeature> features = [] as Set
                features.add(skipGroup)
                GameFeature.groupedFeatures.findAll {
                    GameFeature group, List<GameFeature> options ->
                        group != skipGroup
                }.each {
                    GameFeature group, List<GameFeature> options ->
                        features.add(options[random.nextInt(options.size() - 1)])
                }
                TBGame game = new TBGame(features: features)
                assertFalse validator.validateGame(game)
        }
    }

    void testGameFailsIfOneGroupHasMultipleOptionsFromOneGroup() {
        GameFeature.groupedFeatures.each {
                //  This is the group we will double
            GameFeature doubleGroup, List<GameFeature> doubleOptions ->
                Set<GameFeature> features = [] as Set
                features.add(doubleOptions[0])
                features.add(doubleOptions[-1])
                GameFeature.groupedFeatures.findAll {
                    GameFeature group, List<GameFeature> options ->
                        group != doubleGroup
                }.each {
                    GameFeature group, List<GameFeature> options ->
                        features.add(options[0])
                }
                TBGame game = new TBGame(features: features)
                assertFalse validator.validateGame(game)
        }
    }

    void testAValidGame() {
        Random random = new Random()
        Set<GameFeature> features = [] as Set
        GameFeature.groupedFeatures.each {
            GameFeature group, List<GameFeature> options ->
                features.add(options[random.nextInt(options.size() - 1)])
        }
        TBGame game = new TBGame(features: features)
        assert validator.validateGame(game)
    }

    void testErrorMessage() {
        assert validator.errorMessage() == "Invalid combination of options!"
    }
}
