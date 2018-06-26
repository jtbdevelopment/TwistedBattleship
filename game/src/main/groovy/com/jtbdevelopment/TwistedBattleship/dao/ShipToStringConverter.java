package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
@WritingConverter
public class ShipToStringConverter implements MongoConverter<Ship, String> {

    @Override
    public String convert(final Ship source) {
        //noinspection ConstantConditions
        return source != null ? source.toString() : null;
    }

}
