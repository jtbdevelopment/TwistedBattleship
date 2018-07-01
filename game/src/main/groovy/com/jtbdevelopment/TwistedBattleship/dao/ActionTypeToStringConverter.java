package com.jtbdevelopment.TwistedBattleship.dao;

import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.core.mongo.spring.converters.MongoConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Date: 3/7/15 Time: 3:01 PM
 */
@Component
@WritingConverter
public class ActionTypeToStringConverter implements MongoConverter<TBActionLogEntry.TBActionType, String> {

    @Override
    public String convert(final TBActionLogEntry.TBActionType source) {
        //noinspection ConstantConditions
        return source != null ? source.toString() : null;
    }

}
