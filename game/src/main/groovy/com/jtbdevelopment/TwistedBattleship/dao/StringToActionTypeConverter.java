package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:02 PM
 */
@Component
@ReadingConverter
public class StringToActionTypeConverter implements MongoConverter<String, TBActionLogEntry.TBActionType> {

    @Override
    public TBActionLogEntry.TBActionType convert(final String source) {
        //noinspection ConstantConditions
        return source != null ? TBActionLogEntry.TBActionType.valueOf(source) : null;
    }

}
