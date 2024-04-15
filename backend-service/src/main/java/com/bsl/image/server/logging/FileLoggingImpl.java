package com.bsl.image.server.logging;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Log4j2
public class FileLoggingImpl implements LoggingService {

    @Override
    public void logInfo(String msg) {
        log.info(msg);
    }

    @Override
    public void logWarning(String msg) {
        log.warn(msg);
    }

    @Override
    public void logError(String msg, Exception ex) {
        log.error(msg, ex);
    }
}
