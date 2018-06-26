package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
@ReadingConverter
public class StringToShipConverter implements MongoConverter<String, Ship> {

    @Override
    public Ship convert(final String source) {
        //noinspection ConstantConditions
        return source != null ? Ship.valueOf(source) : null;
    }

}
