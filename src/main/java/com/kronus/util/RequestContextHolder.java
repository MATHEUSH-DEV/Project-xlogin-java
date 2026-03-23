package com.kronus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

/**
 * Request Context Holder for MDC (Mapped Diagnostic Context).
 *
 * Tracks request_id across logging contexts for request tracing.
 * Per Constitution Principle V (Documentation & Code Clarity):
 * - Every request has unique request_id for correlation
 * - Enables debugging across log files
 */
public class RequestContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(RequestContextHolder.class);

    private static final ThreadLocal<String> requestIdThreadLocal = new ThreadLocal<>();

    /**
     * Generates and sets a new request_id for current thread.
     *
     * @return The generated request_id
     */
    public static String generateRequestId() {
        String requestId = "req_" + UUID.randomUUID().toString().substring(0, 8);
        requestIdThreadLocal.set(requestId);
        logger.debug("Request context initialized: {}", requestId);
        return requestId;
    }

    /**
     * Gets the current request_id.
     *
     * @return Request ID or null if not set
     */
    public static String getRequestId() {
        return requestIdThreadLocal.get();
    }

    /**
     * Sets the request_id for current thread.
     *
     * @param requestId The request ID to set
     */
    public static void setRequestId(String requestId) {
        requestIdThreadLocal.set(requestId);
    }

    /**
     * Clears the request context (should be called at end of request).
     */
    public static void clear() {
        requestIdThreadLocal.remove();
    }
}
