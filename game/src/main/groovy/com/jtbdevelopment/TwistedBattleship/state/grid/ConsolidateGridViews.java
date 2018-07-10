package com.jtbdevelopment.TwistedBattleship.state.grid;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Date: 9/29/15
 * Time: 6:46 AM
 */
@Component
public class ConsolidateGridViews {
    public Grid createConsolidatedView(final TBGame game, final Collection<Grid> views) {
        final int size = game.getGridSize();
        final Grid consolidatedView = new Grid(size);
        consolidatedView.stream().forEach(coordinate -> {
            GridCellState firstGridCellState = views.stream()
                    .map(view -> view.get(coordinate))
                    .sorted((a, b) -> b.getRank() - a.getRank()/* reverse sort*/)
                    .collect(Collectors.toList()).get(0);
            consolidatedView.set(coordinate, firstGridCellState);
        });
        return consolidatedView;
    }

}
