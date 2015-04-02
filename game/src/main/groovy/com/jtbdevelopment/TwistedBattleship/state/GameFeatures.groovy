package com.jtbdevelopment.TwistedBattleship.state

import groovy.transform.CompileStatic

/**
 * Date: 3/31/15
 * Time: 4:01 PM
 */
@CompileStatic
enum GameFeatures {
    GridSize(1, Grid10x10, 'Grid:', 'Playing grid size.'),
    Grid10x10(1, '10x10', '10x10 grid', GridSize),
    Grid15x15(2, '15x15', '15x15 grid', GridSize),
    Grid20x20(3, '20x20', '20x20 grid', GridSize),

    FogOfWar(2, SharedIntel, 'Fog of War:', 'How much information is shared between players.'),
    SharedIntel(1, 'Shared', 'Players sees the results of other players actions on common opponents.', FogOfWar),
    IsolatedIntel(2, 'Isolated', 'Each player plays in isolation.', FogOfWar),

    ECM(3, ECMEnabled, 'Electronic Countermeasures:', 'ECM allows player to re-hide an area of their grid from opponents.'),
    ECMEnabled(1, 'Available', 'Players can use part of turn to re-hide a chosen portion of their grid.', ECM),
    ECMDisabled(2, 'Not Available', 'ECM option disabled.', ECM),

    Spy(4, SpyEnabled, 'Spying:', 'Allows player spy on opponent grids.'),
    SpyEnabled(1, 'Available', 'Players can use part of turn to get a random glimpse of a random opponents grid.', Spy),
    SpyDisabled(2, 'Not Available', 'Spying disabled.', Spy),

    EvasiveManeuvers(5, EMEnabled, 'Evasive Maneuvers:', 'Allows defensive evasive maneuvers of a ship.'),
    EMEnabled(1, 'Available', 'Players can move a ship a short distance, re-hiding the old and new positions and some random squares in the area.', EvasiveManeuvers),
    EMDisabled(2, 'Not Available', 'Evasive maneuvers disabled.', EvasiveManeuvers),

    EmergencyRepairs(6, EREnabled, 'Emergency Repairs:', 'Allows a damaged ship to be partially repaired.'),
    EREnabled(1, 'Available', 'Players can use part of a turn to repair a damaged ship.  The square remains visible to opponents.', EmergencyRepairs),
    ERDisabled(2, 'Not Available', 'Emergency repairs disabled.', EmergencyRepairs),

    ActionsPerTurn(7, PerShip, 'Actions Per Turn:', 'How many actions can a player take per turn.'),
    Single(2, 'Single', 'A player can fire a single shot or take a single special action (ECM, Spying, etc) per turn.', ActionsPerTurn),
    PerShip(1, 'Per Ship', 'A player has as many action points as they have ships.  Firing takes 1 action point, specials (ECM, Spying, etc.) take 2.', ActionsPerTurn),

    Critical(8, CriticalEnabled, 'Critical Hits:', 'Allow duds and critical hits'),
    CriticalEnabled(1, 'Enabled', 'Small chance a hit can damage an adjacent boat segment OR be a dud missile and do no damage.', Critical),
    CriticalDisabled(2, 'Disabled', 'No critical hits or duds.', Critical)

    final GameFeatures group
    final String label
    final String description
    final GameFeatures groupDefault
    final int order

    //  Constructor for groups
    public GameFeatures(
            final int order,
            final GameFeatures groupDefault,
            final String label,
            final String description
    ) {
        this.order = order
        this.label = label
        this.description = description
        this.group = this
        this.groupDefault = groupDefault
    }

    public GameFeatures(
            final int order,
            final String label,
            final String description,
            final GameFeatures group = null
    ) {
        this.order = order
        this.description = description
        this.group = group
        this.groupDefault = null
        this.label = label
    }

    static final Map<GameFeatures, List<GameFeatures>> groupedFeatures = [:]
    static {
        values().findAll {
            GameFeatures it ->
                it.group == it
        }.each {
            GameFeatures it ->
                groupedFeatures.put(it, [])
        }

        values().findAll {
            GameFeatures it ->
                it.group != it
        }.each {
            GameFeatures it ->
                groupedFeatures[it.group].add(it)
        }

        groupedFeatures.values().each {
            List<GameFeatures> o ->
                o.sort { GameFeatures a, GameFeatures b -> a.order.compareTo(b.order) }
        }
    }
}
