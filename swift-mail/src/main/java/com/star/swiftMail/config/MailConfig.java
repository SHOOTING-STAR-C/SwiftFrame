package com.star.swiftMail.config;

import com.star.swiftConfig.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 邮件配置类
 * 从配置表读取邮件配置并创建JavaMailSender
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final SysConfigService sysConfigService;

    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        try {
            // 从配置表读取邮件配置
            String host = sysConfigService.getConfigValue("spring.mail.host");
            String port = sysConfigService.getConfigValue("spring.mail.port");
            String username = sysConfigService.getConfigValue("spring.mail.username");
            String password = sysConfigService.getConfigValue("spring.mail.password");

            if (host == null || host.isEmpty()) {
                log.warn("邮件服务器主机未配置，邮件功能将不可用");
                return mailSender;
            }

            // 设置基本配置
            mailSender.setHost(host);
            mailSender.setPort(port != null ? Integer.parseInt(port) : 587);
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            // 设置SMTP属性
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", host);
            props.put("mail.debug", "false");

            log.info("邮件配置从配置表加载成功，主机: {}, 用户: {}", host, username);
        } catch (Exception e) {
            log.error("从配置表加载邮件配置失败", e);
        }

        return mailSender;
    }

    /**
     * 获取发件人邮箱地址
     * 从配置表读取spring.mail.from，如果未配置则使用username
     */
    @Bean
    @ConditionalOnMissingBean
    public String mailFrom() {
        try {
            String from = sysConfigService.getConfigValue("spring.mail.from");
            if (from != null && !from.isEmpty()) {
                log.info("发件人邮箱: {}", from);
                return from;
            }
            
            // 如果没有配置from，则使用username
            String username = sysConfigService.getConfigValue("spring.mail.username");
            if (username != null && !username.isEmpty()) {
                log.info("发件人邮箱（使用username）: {}", username);
                return username;
            }
            
            log.warn("未配置发件人邮箱");
            return "";
        } catch (Exception e) {
            log.error("获取发件人邮箱失败", e);
            return "";
        }
    }
}
