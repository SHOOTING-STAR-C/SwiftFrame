package com.star.swiftMonitor.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

/**
 * SwiftFrame 系统健康检查指示器
 * 监控系统级指标：内存、CPU、系统负载等
 *
 * @author SwiftFrame
 * @since 1.0
 */
@Component("swiftSystem")
public class SwiftSystemHealthIndicator implements HealthIndicator {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    @Override
    public Health health() {
        try {
            // 获取堆内存信息
            long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
            double heapUsagePercent = (double) heapUsed / heapMax * 100;

            // 获取非堆内存信息
            long nonHeapUsed = memoryMXBean.getNonHeapMemoryUsage().getUsed();
            long nonHeapMax = memoryMXBean.getNonHeapMemoryUsage().getMax();

            // 获取系统信息
            int processors = osMXBean.getAvailableProcessors();
            String osName = osMXBean.getName();
            String osVersion = osMXBean.getVersion();
            String osArch = osMXBean.getArch();

            // 判断健康状态
            Health.Builder builder = (heapUsagePercent > 90) 
                    ? Health.down() 
                    : Health.up();

            return builder
                    .withDetail("heapMemory", buildMemoryDetail(heapUsed, heapMax))
                    .withDetail("nonHeapMemory", buildMemoryDetail(nonHeapUsed, nonHeapMax))
                    .withDetail("operatingSystem", buildOsDetail(processors, osName, osVersion, osArch))
                    .withDetail("jvm", buildJvmDetail())
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    /**
     * 构建内存详情
     */
    private MemoryDetail buildMemoryDetail(long used, long max) {
        MemoryDetail detail = new MemoryDetail();
        detail.setUsed(formatBytes(used));
        detail.setMax(formatBytes(max));
        detail.setPercent(max > 0 ? String.format("%.2f%%", (double) used / max * 100) : "N/A");
        return detail;
    }

    /**
     * 构建操作系统详情
     */
    private OperatingSystemDetail buildOsDetail(int processors, String name, String version, String arch) {
        OperatingSystemDetail detail = new OperatingSystemDetail();
        detail.setProcessors(processors);
        detail.setName(name);
        detail.setVersion(version);
        detail.setArch(arch);
        return detail;
    }

    /**
     * 构建JVM详情
     */
    private JvmDetail buildJvmDetail() {
        JvmDetail detail = new JvmDetail();
        detail.setName(System.getProperty("java.vm.name"));
        detail.setVersion(System.getProperty("java.version"));
        detail.setVendor(System.getProperty("java.vendor"));
        detail.setUptime(formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
        return detail;
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 格式化运行时间
     */
    private String formatUptime(long uptimeMillis) {
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%d天 %d小时 %d分钟", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d小时 %d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟", minutes);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    /**
     * 内存详情内部类
     */
    private static class MemoryDetail {
        private String used;
        private String max;
        private String percent;

        public String getUsed() {
            return used;
        }

        public void setUsed(String used) {
            this.used = used;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }
    }

    /**
     * 操作系统详情内部类
     */
    private static class OperatingSystemDetail {
        private int processors;
        private String name;
        private String version;
        private String arch;

        public int getProcessors() {
            return processors;
        }

        public void setProcessors(int processors) {
            this.processors = processors;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getArch() {
            return arch;
        }

        public void setArch(String arch) {
            this.arch = arch;
        }
    }

    /**
     * JVM详情内部类
     */
    private static class JvmDetail {
        private String name;
        private String version;
        private String vendor;
        private String uptime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getUptime() {
            return uptime;
        }

        public void setUptime(String uptime) {
            this.uptime = uptime;
        }
    }
}
