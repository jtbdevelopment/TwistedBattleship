package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
@ReadingConverter
public class StringToGameFeatureConverter implements MongoConverter<String, GameFeature> {

    @Override
    public GameFeature convert(final String source) {
        //noinspection ConstantConditions
        return source != null ? GameFeature.valueOf(source) : null;
    }

}
