package com.star.swiftMail.service;

import com.star.swiftMail.dto.MailRequest;
import com.star.swiftMail.dto.MailResponse;

/**
 * 邮件服务接口
 *
 * @author SHOOTING_STAR_C
 */
public interface MailService {

    /**
     * 发送简单文本邮件
     *
     * @param request 邮件请求
     * @return 邮件响应
     */
    MailResponse sendSimpleMail(MailRequest request);

    /**
     * 发送HTML邮件
     *
     * @param request 邮件请求
     * @return 邮件响应
     */
    MailResponse sendHtmlMail(MailRequest request);

    /**
     * 发送验证码邮件
     *
     * @param to 收件人邮箱
     * @param code 验证码
     * @param purpose 用途（如：重置密码、注册验证等）
     * @return 邮件响应
     */
    MailResponse sendVerificationCode(String to, String code, String purpose);
}
