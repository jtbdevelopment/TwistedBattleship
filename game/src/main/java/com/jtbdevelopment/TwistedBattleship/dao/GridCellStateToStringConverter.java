package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
@WritingConverter
public class GridCellStateToStringConverter implements MongoConverter<GridCellState, String> {

    @Override
    public String convert(final GridCellState source) {
        //noinspection ConstantConditions
        return source != null ? source.toString() : null;
    }

}
