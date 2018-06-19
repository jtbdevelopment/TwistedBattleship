import ch.qos.logback.classic.encoder.PatternLayoutEncoder

// always a good idea to add an on console status listener
statusListener(OnConsoleStatusListener)

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

logger("org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver", WARN)
logger("com.jtbdevelopment.games.push", TRACE)
logger("org.springframework", TRACE)
//logger("com.jtbdevelopment.games.websocket.AtmosphereListener", TRACE)
root(INFO, ["CONSOLE"])
