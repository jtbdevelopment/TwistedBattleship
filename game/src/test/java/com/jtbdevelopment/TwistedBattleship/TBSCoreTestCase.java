package com.jtbdevelopment.TwistedBattleship;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.players.Player;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Date: 6/28/18
 * Time: 7:10 PM
 */
public class TBSCoreTestCase extends MongoGameCoreTestCase {
  protected TBActionLogEntry getLastEntry(final List<TBActionLogEntry> entries) {
    return entries.get(entries.size() - 1);
  }

    private TBActionLogEntry getLastEntry(final TBGame game, final ObjectId player) {
    return getLastEntry(game.getPlayerDetails().get(player).getActionLog());
  }

  protected TBActionLogEntry getLastEntry(final TBGame game, final Player<ObjectId> player) {
    return getLastEntry(game, player.getId());
  }
}
