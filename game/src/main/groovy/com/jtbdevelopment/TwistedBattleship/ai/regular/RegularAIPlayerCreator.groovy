package com.jtbdevelopment.TwistedBattleship.ai.regular

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
class RegularAIPlayerCreator extends AbstractAIPlayerCreator {
    private static final String DISPLAY_NAME_BASE = "Regular AI #"

    @PostConstruct
    void setup() {
        String baseName = DISPLAY_NAME_BASE;
        String icon = ""
        loadOrCreateAIPlayers(baseName)
    }
}
