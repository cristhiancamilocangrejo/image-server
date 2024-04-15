package com.bsl.image.server.logging;

public interface LoggingService {

    void logInfo(String msg);

    void logWarning(String msg);

    void logError(String msg, Exception ex);
}
