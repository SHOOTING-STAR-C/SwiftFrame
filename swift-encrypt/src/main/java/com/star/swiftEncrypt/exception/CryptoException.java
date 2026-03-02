package com.star.swiftEncrypt.exception;

import com.star.swiftCommon.constant.ResultCode;
import lombok.Getter;

/**
 * 加密 | 解密异常
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public class CryptoException extends RuntimeException {
    private final String errorCode;

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ResultCode.ENCRYPT_ERROR.getCode();
    }

    public CryptoException(String message) {
        super(message);
        this.errorCode = ResultCode.ENCRYPT_ERROR.getCode();
    }

    public CryptoException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CryptoException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}