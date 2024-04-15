package com.bsl.image.server.logging;

import com.bsl.image.server.entity.Logging;
import com.bsl.image.server.repository.LoggingRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!local")
@Component
public class DatabaseLoggingImpl implements LoggingService {

    private LoggingRepository loggingRepository;

    @Override
    public void logInfo(String msg) {
        loggingRepository.loggingMessage(new Logging(msg, null));
    }

    @Override
    public void logWarning(String msg) {
        loggingRepository.loggingMessage(new Logging(msg, null));
    }

    @Override
    public void logError(String msg, Exception ex) {
        loggingRepository.loggingMessage(new Logging(msg, ex.getMessage()));
    }
}
