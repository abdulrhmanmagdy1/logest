package com.edham.logistics.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    public void log(String action, String user, String details) {
        logger.info("AUDIT: Action={}, User={}, Details={}", action, user, details);
    }
}
