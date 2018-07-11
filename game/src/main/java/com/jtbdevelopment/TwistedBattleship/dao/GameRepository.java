package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.mongo.dao.AbstractMongoMultiPlayerGameRepository;

/**
 * Date: 4/2/15
 * Time: 10:16 PM
 */
public interface GameRepository extends AbstractMongoMultiPlayerGameRepository<GameFeature, TBGame> {
}
