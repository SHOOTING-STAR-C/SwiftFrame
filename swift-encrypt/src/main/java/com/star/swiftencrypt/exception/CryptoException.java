package com.star.swiftencrypt.exception;

/**
 * 加密|解密异常
 *
 * @author SHOOTING_STAR_C
 */
public class CryptoException extends RuntimeException {
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
