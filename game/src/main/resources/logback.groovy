import ch.qos.logback.core.status.OnConsoleStatusListener

// always a good idea to add an on console status listener
statusListener(OnConsoleStatusListener)

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

logger("com.jtbdevelopment.games.push", TRACE)
//logger("com.jtbdevelopment.games.websocket.AtmosphereListener", TRACE)
root(INFO, ["CONSOLE"])
