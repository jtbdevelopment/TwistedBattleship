package com.jtbdevelopment.TwistedBattleship.state

import groovy.transform.CompileStatic

/**
 * Date: 3/31/15
 * Time: 4:01 PM
 *
 * TODO - check amount of text on different devices
 */
@CompileStatic
enum GameFeature {
    GridSize(1, Grid10x10, 'Grid', 'Size of the ocean each player is in.'),
    Grid10x10(1, '10x10', '10 x 10 square grid', GridSize),
    Grid15x15(2, '15x15', '15 x 15 square grid', GridSize),
    Grid20x20(3, '20x20', '20 x 20 square grid', GridSize),

    FogOfWar(2, SharedIntel, 'Fog of War', 'How much information is shared between players?'),
    SharedIntel(1, 'Shared', 'Players can see the results of all actions taken by all players.', FogOfWar),
    IsolatedIntel(2, 'Isolated', 'Each player can only see the impact of their own actions and the defensive actions of each target.', FogOfWar),

    ECM(3, ECMEnabled, 'Electronic Countermeasures', 'ECM devices allow player to scramble opponent views of their ocean.  Bigger grids hide bigger areas.'),
    ECMEnabled(1, 'Available', 'Players can use ECM device to cloak their grid.', ECM),
    ECMDisabled(2, 'Not Available', 'ECM option disabled.', ECM),

    Spy(4, SpyEnabled, 'Spying', "Allows player's drones to spy on opponents."),
    SpyEnabled(1, 'Available', "Players can use spy drones to get a glimpse of an area of an opponent's grid.  Bigger grids show more area.", Spy),
    SpyDisabled(2, 'Not Available', 'Spy drones disabled.', Spy),

    EvasiveManeuvers(5, EMEnabled, 'Evasive Maneuvers', 'Allows defensive evasive maneuvers of a ship.'),
    EMEnabled(1, 'Available', 'The captain will take emegency evasive actions, moving a few spaces away and possibly turning 90 degrees.  This also scrambles opponent records of the area somewhat randomly.', EvasiveManeuvers),
    EMDisabled(2, 'Not Available', 'Evasive maneuvers disabled.', EvasiveManeuvers),

    EmergencyRepairs(6, EREnabled, 'Emergency Repairs', 'Allows a damaged ship to be repaired.'),
    EREnabled(1, 'Available', 'Ship crew are able to salvage parts to repair a ship to full sea-worthiness.', EmergencyRepairs),
    ERDisabled(2, 'Not Available', 'Emergency repairs disabled.', EmergencyRepairs),

    ActionsPerTurn(7, PerShip, 'Actions Per Turn', 'How many actions can a player take per turn?'),
    Single(2, 'Single', 'A player can fire a single shot or take a single special action (ECM, Spying, etc) per turn.', ActionsPerTurn),
    PerShip(1, 'Per Ship', 'A player has as many action points as they have ships.  Firing takes 1 action point, specials (ECM, Spying, etc.) take 2.', ActionsPerTurn),

    Critical(8, CriticalEnabled, 'Critical Hits', 'Allow duds and critical hits (NOT IMPLEMENTED CURRENTLY).'),
    CriticalEnabled(1, 'Enabled', 'Small chance a hit can damage an adjacent boat segment OR be a dud missile and do no damage.', Critical),
    CriticalDisabled(2, 'Disabled', 'No critical hits or duds.', Critical)

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
