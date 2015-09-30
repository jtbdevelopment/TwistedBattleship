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

    @PostConstruct
    void setup() {
        String baseName = DISPLAY_NAME_BASE;
        String icon = ""
        loadOrCreateAIPlayers(baseName)
    }
}
