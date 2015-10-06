package com.jtbdevelopment.TwistedBattleship.ai.simple

import com.jtbdevelopment.TwistedBattleship.ai.common.AbstractAIPlayerCreator
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 9/18/15
 * Time: 6:48 AM
 */
@Component
@CompileStatic
class SimpleAIPlayerCreator extends AbstractAIPlayerCreator {
    private static final String DISPLAY_NAME_BASE = "Simple AI #"
    public static final String ICON = "images/avatars/robot4.png"

    @PostConstruct
    void setup() {
        loadOrCreateAIPlayers(DISPLAY_NAME_BASE, ICON)
    }
}
