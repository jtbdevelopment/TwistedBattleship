package com.jtbdevelopment.TwistedBattleship.state

import groovy.transform.CompileStatic

/**
 * Date: 3/31/15
 * Time: 4:01 PM
 */
@CompileStatic
enum GameFeature {
    GridSize(1, Grid10x10, 'Grid', 'Size of the ocean each player is in.'),
    Grid10x10(1, '10x10', '10 x 10 square grid', GridSize),
    Grid15x15(2, '15x15', '15 x 15 square grid', GridSize),
    Grid20x20(3, '20x20', '20 x 20 square grid', GridSize),

    ActionsPerTurn(2, PerShip, 'Actions Per Turn', 'How many actions can a player take per turn?'),
    Single(2, 'Single Action', 'A player can fire a single shot or take a single special action (ECM, Spying, etc) per turn.', ActionsPerTurn),
    PerShip(1, 'Per Ship', 'A player has as many action points as they have ships.  Firing takes 1 action point, specials (ECM, Spying, etc.) take 2.', ActionsPerTurn),

    FogOfWar(3, SharedIntel, 'Fog of War', 'How much information is shared between players?'),
    SharedIntel(1, 'Shared Intel', 'Players can see the results of all actions taken by all players.', FogOfWar),
    IsolatedIntel(2, 'Isolated Views', 'Each player can only see the impact of their own actions and the defensive actions of each target.', FogOfWar),

    ECM(4, ECMEnabled, 'Electronic Countermeasures', 'ECM devices allow player to scramble opponent views of their ocean.'),
    ECMEnabled(1, 'Enabled', 'Players can use ECM device to cloak their grid.  Bigger grids hide bigger areas.', ECM),
    ECMDisabled(2, 'Disabled', 'ECM option disabled.', ECM),

    Spy(5, SpyEnabled, 'Spying', "Allows player's drones to spy on opponents."),
    SpyEnabled(1, 'Enabled', "Players can use spy drones to get a glimpse of an area of an opponent's grid.  Bigger grids show more area.", Spy),
    SpyDisabled(2, 'Disabled', 'Spy drones disabled.', Spy),

    EvasiveManeuvers(6, EMEnabled, 'Evasive Maneuvers', 'Allows defensive evasive maneuvers of a ship.'),
    EMEnabled(1, 'Enabled', 'The captain will take emegency evasive actions, moving a few spaces away and possibly turning 90 degrees.  This also scrambles opponent records of the area somewhat randomly.', EvasiveManeuvers),
    EMDisabled(2, 'Disabled', 'Evasive maneuvers disabled.', EvasiveManeuvers),

    EmergencyRepairs(7, EREnabled, 'Emergency Repairs', 'Allows a damaged ship to be repaired.'),
    EREnabled(1, 'Enabled', 'Ship crew are able to salvage parts to repair a ship to full sea-worthiness.', EmergencyRepairs),
    ERDisabled(2, 'Disabled', 'Emergency repairs disabled.', EmergencyRepairs),

    //  TODO - implement or completely remove
    /*
    Critical(8, CriticalEnabled, 'Critical Hits', 'Allow duds and critical hits (NOT IMPLEMENTED CURRENTLY).'),
    CriticalEnabled(1, 'Criticals', 'Small chance a hit can damage an adjacent boat segment OR be a dud missile and do no damage.', Critical),
    CriticalDisabled(2, '', 'No critical hits or duds.', Critical)
    */

    //  TODO - cruise missiles?

    final GameFeature group
    final String label
    final String description
    final GameFeature groupDefault
    final int order

    //  Constructor for groups
    public GameFeature(
            final int order,
            final GameFeature groupDefault,
            final String label,
            final String description
    ) {
        this.order = order
        this.label = label
        this.description = description
        this.group = this
        this.groupDefault = groupDefault
    }

    public GameFeature(
            final int order,
            final String label,
            final String description,
            final GameFeature group = null
    ) {
        this.order = order
        this.description = description
        this.group = group
        this.groupDefault = null
        this.label = label
    }

    static final Map<GameFeature, List<GameFeature>> groupedFeatures = [:]
    static {
        values().findAll {
            GameFeature it ->
                it.group == it
        }.each {
            GameFeature it ->
                groupedFeatures.put(it, [])
        }

        values().findAll {
            GameFeature it ->
                it.group != it
        }.each {
            GameFeature it ->
                groupedFeatures[it.group].add(it)
        }

        groupedFeatures.values().each {
            List<GameFeature> o ->
                o.sort { GameFeature a, GameFeature b -> a.order.compareTo(b.order) }
        }
    }
}
