package com.jtbdevelopment.TwistedBattleship.dao

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.dao.AbstractMongoMultiPlayerGameRepository
import groovy.transform.CompileStatic

/**
 * Date: 4/2/15
 * Time: 10:16 PM
 */
@CompileStatic
interface TBGameDao extends AbstractMongoMultiPlayerGameRepository<GameFeature, TBGame> {

}