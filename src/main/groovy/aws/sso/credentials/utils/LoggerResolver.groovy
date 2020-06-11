package aws.sso.credentials.utils

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.util.FileSize
import org.slf4j.LoggerFactory

/**
 * From SciJava Common library
 */
class LoggerResolver {
    static Logger getLogger(Class clazz) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory()

        RollingFileAppender rfAppender = new RollingFileAppender()
        rfAppender.setContext(loggerContext)
        rfAppender.setFile("credentials-fetcher.log")
        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy()
        rollingPolicy.setContext(loggerContext)
        // rolling policies need to know their parent
        // it's one of the rare cases, where a sub-component knows about its parent
        rollingPolicy.setParent(rfAppender)
        rollingPolicy.setFileNamePattern("credentials-fetcher.%i.log.zip")
        rollingPolicy.start()

        SizeBasedTriggeringPolicy triggeringPolicy = new ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy()
        triggeringPolicy.setMaxFileSize(FileSize.valueOf("20KB"))
        triggeringPolicy.start()

        PatternLayoutEncoder encoder = new PatternLayoutEncoder()
        encoder.setContext(loggerContext)
        encoder.setPattern("%d [%thread] %-5level %logger{35} - %msg%n")
        encoder.start()

        rfAppender.setEncoder(encoder)
        rfAppender.setRollingPolicy(rollingPolicy)
        rfAppender.setTriggeringPolicy(triggeringPolicy)

        rfAppender.start()
        // attach the rolling file appender to the logger of your choice
        Logger logbackLogger = loggerContext.getLogger(clazz)
        logbackLogger.addAppender(rfAppender)

        logbackLogger
    }
}
