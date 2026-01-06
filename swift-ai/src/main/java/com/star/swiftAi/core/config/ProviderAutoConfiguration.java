package com.star.swiftAi.core.config;

import com.star.swiftAi.core.annotation.ProviderAdapter;
import com.star.swiftAi.core.registry.ProviderRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 * 提供商自动配置
 * 扫描并注册所有带有@ProviderAdapter注解的提供商
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Configuration
public class ProviderAutoConfiguration {
    
    public ProviderAutoConfiguration() {
        scanAndRegisterProviders();
    }
    
    /**
     * 扫描并注册所有提供商
     */
    private void scanAndRegisterProviders() {
        try {
            // 创建扫描器
            ClassPathScanningCandidateComponentProvider scanner = 
                new ClassPathScanningCandidateComponentProvider(false);
            
            // 添加注解过滤器
            scanner.addIncludeFilter(new AnnotationTypeFilter(ProviderAdapter.class));
            
            // 扫描com.star.swiftAi包及其子包
            String basePackage = "com.star.swiftAi";
            scanner.findCandidateComponents(basePackage).forEach(beanDefinition -> {
                try {
                    String className = beanDefinition.getBeanClassName();
                    Class<?> clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
                    
                    // 获取注解
                    ProviderAdapter annotation = clazz.getAnnotation(ProviderAdapter.class);
                    if (annotation != null) {
                        // 获取默认配置模板
                        Map<String, Object> defaultConfigTmpl = getDefaultConfigTemplate(clazz);
                        
                        // 注册到注册表
                        ProviderRegistry.registerProviderAdapter(
                            annotation.typeName(),
                            annotation.desc(),
                            annotation.providerType(),
                            defaultConfigTmpl,
                            annotation.displayName(),
                            clazz
                        );
                    }
                } catch (Exception e) {
                    log.error("注册提供商失败: {}", beanDefinition.getBeanClassName(), e);
                }
            });
            
            log.info("提供商自动配置完成，共注册 {} 个提供商", 
                ProviderRegistry.getProviderRegistry().size());
            
        } catch (Exception e) {
            log.error("扫描提供商失败", e);
        }
    }
    
    /**
     * 获取默认配置模板
     * 子类可以通过实现getDefaultConfigTemplate方法来提供默认配置
     *
     * @param clazz 提供商类
     * @return 默认配置模板
     */
    private Map<String, Object> getDefaultConfigTemplate(Class<?> clazz) {
        try {
            // 尝试调用静态方法getDefaultConfigTemplate
            java.lang.reflect.Method method = clazz.getMethod("getDefaultConfigTemplate");
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) method.invoke(null);
            return result;
        } catch (Exception e) {
            // 如果没有该方法，返回空Map
            return Map.of();
        }
    }
}
