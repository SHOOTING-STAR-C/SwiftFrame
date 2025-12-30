package com.star.swiftAi.exception;

/**
 * AI 调用异常基类
 */
public class AiException extends RuntimeException {
    
    private final String provider;
    private final String errorCode;
    private final Integer statusCode;
    
    public AiException(String message) {
        super(message);
        this.provider = "unknown";
        this.errorCode = "AI_ERROR";
        this.statusCode = null;
    }
    
    public AiException(String message, String provider) {
        super(message);
        this.provider = provider;
        this.errorCode = "AI_ERROR";
        this.statusCode = null;
    }
    
    public AiException(String message, String provider, String errorCode) {
        super(message);
        this.provider = provider;
        this.errorCode = errorCode;
        this.statusCode = null;
    }
    
    public AiException(String message, String provider, String errorCode, Integer statusCode) {
        super(message);
        this.provider = provider;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
    
    public AiException(String message, Throwable cause) {
        super(message, cause);
        this.provider = "unknown";
        this.errorCode = "AI_ERROR";
        this.statusCode = null;
    }
    
    public AiException(String message, String provider, Throwable cause) {
        super(message, cause);
        this.provider = provider;
        this.errorCode = "AI_ERROR";
        this.statusCode = null;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
}
