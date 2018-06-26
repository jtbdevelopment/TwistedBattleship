package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
@ReadingConverter
public class StringToGridCellStateConverter implements MongoConverter<String, GridCellState> {

    @Override
    public GridCellState convert(final String source) {
        //noinspection ConstantConditions
        return source != null ? GridCellState.valueOf(source) : null;
    }

}
