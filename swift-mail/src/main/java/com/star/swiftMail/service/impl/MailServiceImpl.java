package com.star.swiftMail.service.impl;

import com.star.swiftMail.dto.MailRequest;
import com.star.swiftMail.dto.MailResponse;
import com.star.swiftMail.service.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final String from;

    /**
     * 发送简单文本邮件
     *
     * @param request 邮件请求
     * @return 邮件响应
     */
    @Override
    public MailResponse sendSimpleMail(MailRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(request.getTo());
            message.setSubject(request.getSubject());
            message.setText(request.getText());

            mailSender.send(message);

            log.info("简单邮件发送成功: to={}, subject={}", request.getTo(), request.getSubject());

            return MailResponse.builder()
                    .success(true)
                    .message("邮件发送成功")
                    .build();
        } catch (Exception e) {
            log.error("简单邮件发送失败: to={}, subject={}, error={}", 
                    request.getTo(), request.getSubject(), e.getMessage(), e);
            return MailResponse.builder()
                    .success(false)
                    .message("邮件发送失败")
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * 发送HTML邮件
     *
     * @param request 邮件请求
     * @return 邮件响应
     */
    @Override
    public MailResponse sendHtmlMail(MailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getHtml(), true);

            mailSender.send(message);

            log.info("HTML邮件发送成功: to={}, subject={}", request.getTo(), request.getSubject());

            return MailResponse.builder()
                    .success(true)
                    .message("邮件发送成功")
                    .build();
        } catch (Exception e) {
            log.error("HTML邮件发送失败: to={}, subject={}, error={}", 
                    request.getTo(), request.getSubject(), e.getMessage(), e);
            return MailResponse.builder()
                    .success(false)
                    .message("邮件发送失败")
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * 发送验证码邮件
     *
     * @param to 收件人邮箱
     * @param code 验证码
     * @param purpose 用途
     * @return 邮件响应
     */
    @Override
    public MailResponse sendVerificationCode(String to, String code, String purpose) {
        try {
            String subject = String.format("【SwiftFrame】%s验证码", purpose);

            String htmlContent = buildVerificationCodeHtml(code, purpose);

            MailRequest request = MailRequest.builder()
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            return sendHtmlMail(request);
        } catch (Exception e) {
            log.error("验证码邮件发送失败: to={}, purpose={}, error={}", 
                    to, purpose, e.getMessage(), e);
            return MailResponse.builder()
                    .success(false)
                    .message("验证码邮件发送失败")
                    .error(e.getMessage())
                    .build();
        }
    }

    /**
     * 构建验证码HTML内容
     *
     * @param code 验证码
     * @param purpose 用途
     * @return HTML内容
     */
    private String buildVerificationCodeHtml(String code, String purpose) {
        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>验证码</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: Arial, sans-serif;" +
                "            line-height: 1.6;" +
                "            color: #333;" +
                "            max-width: 600px;" +
                "            margin: 0 auto;" +
                "            padding: 20px;" +
                "        }" +
                "        .container {" +
                "            background-color: #f9f9f9;" +
                "            border-radius: 8px;" +
                "            padding: 30px;" +
                "            box-shadow: 0 2px 4px rgba(0,0,0,0.1);" +
                "        }" +
                "        .header {" +
                "            text-align: center;" +
                "            margin-bottom: 30px;" +
                "        }" +
                "        .header h1 {" +
                "            color: #007bff;" +
                "            margin: 0;" +
                "        }" +
                "        .content {" +
                "            margin-bottom: 20px;" +
                "        }" +
                "        .code-box {" +
                "            background-color: #e7f3ff;" +
                "            border: 2px dashed #007bff;" +
                "            border-radius: 8px;" +
                "            padding: 20px;" +
                "            text-align: center;" +
                "            margin: 20px 0;" +
                "        }" +
                "        .code {" +
                "            font-size: 32px;" +
                "            font-weight: bold;" +
                "            color: #007bff;" +
                "            letter-spacing: 5px;" +
                "        }" +
                "        .footer {" +
                "            text-align: center;" +
                "            color: #666;" +
                "            font-size: 12px;" +
                "            margin-top: 30px;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>SwiftFrame</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>您好，</p>" +
                "            <p>您正在进行" + purpose + "操作，验证码如下：</p>" +
                "            <div class=\"code-box\">" +
                "                <div class=\"code\">" + code + "</div>" +
                "            </div>" +
                "            <p><strong>注意事项：</strong></p>" +
                "            <ul>" +
                "                <li>验证码有效期为 <strong>5分钟</strong></li>" +
                "                <li>请勿将验证码告知他人</li>" +
                "                <li>如非本人操作，请忽略此邮件</li>" +
                "            </ul>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>此邮件由系统自动发送，请勿回复</p>" +
                "            <p>&copy; 2024 SwiftFrame. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
}
