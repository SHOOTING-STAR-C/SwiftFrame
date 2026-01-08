package com.star.swiftAi.core.annotation;

import java.lang.annotation.*;

/**
 * 提供商适配器注解
 * 用于自动注册提供商到注册表
 *
 * @author SHOOTING_STAR_C
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProviderAdapter {
    
    /**
     * 提供商类型名称
     */
    String typeName();
    
    /**
     * 描述
     */
    String desc() default "";
    
    /**
     * 提供商显示名称
     */
    String displayName();
}
