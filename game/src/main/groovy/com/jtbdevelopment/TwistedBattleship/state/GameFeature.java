package com.jtbdevelopment.TwistedBattleship.state;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date: 3/31/15
 * Time: 4:01 PM
 */
public enum GameFeature {
    GridSize(1, GameFeatureGroupType.Difficulty, "Grid", "Size of the ocean each player is in."),
    Grid10x10(1, "10x10", "10 x 10 square grid", GridSize),
    Grid15x15(2, "15x15", "15 x 15 square grid", GridSize),
    Grid20x20(3, "20x20", "20 x 20 square grid", GridSize),

    ActionsPerTurn(2, GameFeatureGroupType.Difficulty, "Actions Per Turn", "How many actions can a player take per turn?"),
    Single(2, "Single Action", "A player can fire a single shot or take a single special action (ECM, Spying, etc) per turn.", ActionsPerTurn),
    PerShip(1, "Per Ship", "A player has as many action points as they have ships.  Firing takes 1 action point, specials (ECM, Spying, etc.) take 2.", ActionsPerTurn),

    FogOfWar(3, GameFeatureGroupType.Difficulty, "Fog of War", "How much information is shared between players?"),
    SharedIntel(1, "Shared Intel", "Players can see the results of all actions taken by all players.", FogOfWar),
    IsolatedIntel(2, "Isolated Views", "Each player can only see the impact of their own actions and the defensive actions of other players.", FogOfWar),

    StartingShips(4, GameFeatureGroupType.Difficulty, "Ships", "What ships are there?"),
    StandardShips(1, "Standard Ships", "Players get 5 ships of varying types and sizes.", StartingShips),
    AllCarriers(2, "All Carriers", "Each player gets 5 aircraft carriers.", StartingShips),
    AllDestroyers(3, "All Destroyers", "Each player gets 5 destroyers.", StartingShips),
    AllSubmarines(4, "All Submarines", "Each player gets 5 submarines.", StartingShips),
    AllCruisers(5, "All Cruisers", "Each player gets 5 cruisers.", StartingShips),
    AllBattleships(6, "All Battleships", "Each player gets 5 battleships.", StartingShips),

    ECM(5, GameFeatureGroupType.Defensive, "Electronic Countermeasures", "ECM devices allow player to scramble opponent views of their ocean."),
    ECMEnabled(1, "Enabled", "Players can use ECM device to cloak their grid.  Bigger grids hide bigger areas.", ECM),
    ECMDisabled(2, "Disabled", "ECM option disabled.", ECM),

    EvasiveManeuvers(6, GameFeatureGroupType.Defensive, "Evasive Maneuvers", "Allows defensive evasive maneuvers of a ship."),
    EMEnabled(1, "Enabled", "The captain will take emegency evasive actions, moving a few spaces away and possibly turning 90 degrees.  This also scrambles opponent records of the area somewhat randomly.", EvasiveManeuvers),
    EMDisabled(2, "Disabled", "Evasive maneuvers disabled.", EvasiveManeuvers),

    EmergencyRepairs(7, GameFeatureGroupType.Defensive, "Emergency Repairs", "Allows a damaged ship to be repaired."),
    EREnabled(1, "Enabled", "Ship crew are able to salvage parts to repair a ship to full sea-worthiness.", EmergencyRepairs),
    ERDisabled(2, "Disabled", "Emergency repairs disabled.", EmergencyRepairs),

    Spy(8, GameFeatureGroupType.Offensive, "Spying", "Allows player's drones to spy on opponents."),
    SpyEnabled(1, "Enabled", "Players can use spy drones to get a glimpse of an area of an opponent's grid.  Bigger grids show more area.", Spy),
    SpyDisabled(2, "Disabled", "Spy drones disabled.", Spy),

    CruiseMissile(9, GameFeatureGroupType.Offensive, "Cruise Missile", "Single use attack that sinks a ship with a single hit on any location."),
    CruiseMissileEnabled(1, "Enabled", "Single use per game, sinks a ship by hitting any single ship location.", CruiseMissile),
    CruiseMissileDisabled(2, "Disabled", "Cruise missile disabled.", CruiseMissile);

    private static final Map<GameFeature, List<GameFeature>> groupedFeatures;

    static {
        groupedFeatures = new HashMap<>();
        List<GameFeature> groups = Arrays.stream(GameFeature.values()).filter(f -> f.getGroup().equals(f)).collect(Collectors.toList());
        groups.forEach(group -> groupedFeatures.put(group, new LinkedList<>()));
        Arrays.stream(GameFeature.values()).filter(f -> !f.equals(f.group)).forEach(feature -> groupedFeatures.get(feature.group).add(feature));

        groupedFeatures.values().forEach(group -> group.sort(Comparator.comparingInt(a -> a.order)));

    }

    private final GameFeatureGroupType groupType;
    private final GameFeature group;
    private final String label;
    private final String description;
    private final int order;

    GameFeature(final int order, final GameFeatureGroupType groupType, final String label, final String description) {
        this.order = order;
        this.label = label;
        this.description = description;
        this.group = this;
        this.groupType = groupType;
    }

    GameFeature(final int order, final String label, final String description, final GameFeature group) {
        this.order = order;
        this.description = description;
        this.group = group;
        this.label = label;
        this.groupType = group.getGroupType();
    }

    public static Map<GameFeature, List<GameFeature>> getGroupedFeatures() {
        return groupedFeatures;
    }

    public final GameFeatureGroupType getGroupType() {
        return groupType;
    }

    public final GameFeature getGroup() {
        return group;
    }

    public final String getLabel() {
        return label;
    }

    public final String getDescription() {
        return description;
    }

    public final int getOrder() {
        return order;
    }
}
