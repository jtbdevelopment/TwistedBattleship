package com.jtbdevelopment.TwistedBattleship;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;

import java.util.List;

/**
 * Date: 6/28/18
 * Time: 7:10 PM
 */
public class TBSCoreTestCase extends MongoGameCoreTestCase {
  protected TBActionLogEntry getLastEntry(final List<TBActionLogEntry> entries) {
    return entries.get(entries.size() - 1);
  }
}
