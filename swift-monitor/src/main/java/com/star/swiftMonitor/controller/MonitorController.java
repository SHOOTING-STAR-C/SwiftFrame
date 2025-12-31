package com.star.swiftMonitor.controller;

import com.star.swiftCommon.domain.PubResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 监控数据接口
 * 提供系统监控数据供前端展示
 *
 * @author SwiftFrame
 * @since 1.0
 */
@RestController
@RequestMapping("/monitor")
@Tag(name = "系统监控", description = "系统监控数据接口")
public class MonitorController {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    /**
     * 获取系统概览信息
     *
     * @return 系统概览数据
     */
    @GetMapping("/overview")
    @Operation(summary = "系统概览", description = "获取系统整体概览信息")
    public PubResult<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 内存信息
        long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
        overview.put("heapMemoryUsage", (double) heapUsed / heapMax * 100);
        overview.put("heapMemoryUsed", formatBytes(heapUsed));
        overview.put("heapMemoryMax", formatBytes(heapMax));
        
        // CPU信息
        overview.put("availableProcessors", osMXBean.getAvailableProcessors());
        overview.put("systemLoadAverage", osMXBean.getSystemLoadAverage());
        
        // JVM信息
        overview.put("jvmUptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
        overview.put("javaVersion", System.getProperty("java.version"));
        
        return PubResult.success(overview);
    }

    /**
     * 获取内存详细信息
     *
     * @return 内存数据
     */
    @GetMapping("/memory")
    @Operation(summary = "内存详情", description = "获取系统内存使用详情")
    public PubResult<Map<String, Object>> getMemoryDetails() {
        Map<String, Object> memory = new HashMap<>();
        
        // 堆内存
        Map<String, Object> heapMemory = new HashMap<>();
        heapMemory.put("init", formatBytes(memoryMXBean.getHeapMemoryUsage().getInit()));
        heapMemory.put("used", formatBytes(memoryMXBean.getHeapMemoryUsage().getUsed()));
        heapMemory.put("committed", formatBytes(memoryMXBean.getHeapMemoryUsage().getCommitted()));
        heapMemory.put("max", formatBytes(memoryMXBean.getHeapMemoryUsage().getMax()));
        heapMemory.put("usagePercent", 
            (double) memoryMXBean.getHeapMemoryUsage().getUsed() / 
            memoryMXBean.getHeapMemoryUsage().getMax() * 100);
        memory.put("heapMemory", heapMemory);
        
        // 非堆内存
        Map<String, Object> nonHeapMemory = new HashMap<>();
        nonHeapMemory.put("init", formatBytes(memoryMXBean.getNonHeapMemoryUsage().getInit()));
        nonHeapMemory.put("used", formatBytes(memoryMXBean.getNonHeapMemoryUsage().getUsed()));
        nonHeapMemory.put("committed", formatBytes(memoryMXBean.getNonHeapMemoryUsage().getCommitted()));
        nonHeapMemory.put("max", formatBytes(memoryMXBean.getNonHeapMemoryUsage().getMax()));
        memory.put("nonHeapMemory", nonHeapMemory);
        
        return PubResult.success(memory);
    }

    /**
     * 获取JVM详细信息
     *
     * @return JVM数据
     */
    @GetMapping("/jvm")
    @Operation(summary = "JVM详情", description = "获取JVM运行时详细信息")
    public PubResult<Map<String, Object>> getJvmDetails() {
        Map<String, Object> jvm = new HashMap<>();
        
        jvm.put("name", System.getProperty("java.vm.name"));
        jvm.put("version", System.getProperty("java.version"));
        jvm.put("vendor", System.getProperty("java.vendor"));
        jvm.put("home", System.getProperty("java.home"));
        jvm.put("uptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
        jvm.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());
        jvm.put("inputArguments", ManagementFactory.getRuntimeMXBean().getInputArguments());
        
        return PubResult.success(jvm);
    }

    /**
     * 获取操作系统信息
     *
     * @return 操作系统数据
     */
    @GetMapping("/os")
    @Operation(summary = "操作系统信息", description = "获取操作系统详细信息")
    public PubResult<Map<String, Object>> getOsDetails() {
        Map<String, Object> os = new HashMap<>();
        
        os.put("name", osMXBean.getName());
        os.put("version", osMXBean.getVersion());
        os.put("arch", osMXBean.getArch());
        os.put("availableProcessors", osMXBean.getAvailableProcessors());
        os.put("systemLoadAverage", osMXBean.getSystemLoadAverage());
        
        return PubResult.success(os);
    }

    /**
     * 获取线程信息
     *
     * @return 线程数据
     */
    @GetMapping("/thread")
    @Operation(summary = "线程信息", description = "获取线程相关信息")
    public PubResult<Map<String, Object>> getThreadDetails() {
        Map<String, Object> thread = new HashMap<>();
        
        thread.put("threadCount", ManagementFactory.getThreadMXBean().getThreadCount());
        thread.put("peakThreadCount", ManagementFactory.getThreadMXBean().getPeakThreadCount());
        thread.put("daemonThreadCount", ManagementFactory.getThreadMXBean().getDaemonThreadCount());
        thread.put("totalStartedThreadCount", ManagementFactory.getThreadMXBean().getTotalStartedThreadCount());
        
        return PubResult.success(thread);
    }

    /**
     * 获取运行时信息
     *
     * @return 运行时数据
     */
    @GetMapping("/runtime")
    @Operation(summary = "运行时信息", description = "获取应用运行时信息")
    public PubResult<Map<String, Object>> getRuntimeDetails() {
        Map<String, Object> runtime = new HashMap<>();
        
        runtime.put("uptime", formatUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
        runtime.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());
        runtime.put("systemProperties", ManagementFactory.getRuntimeMXBean().getSystemProperties());
        
        return PubResult.success(runtime);
    }

    /**
     * 获取所有监控数据（聚合接口）
     *
     * @return 所有监控数据
     */
    @GetMapping("/all")
    @Operation(summary = "全部监控数据", description = "获取所有监控数据的聚合接口")
    public PubResult<Map<String, Object>> getAllMonitorData() {
        Map<String, Object> allData = new HashMap<>();
        
        allData.put("overview", getSystemOverview().getData());
        allData.put("memory", getMemoryDetails().getData());
        allData.put("jvm", getJvmDetails().getData());
        allData.put("os", getOsDetails().getData());
        allData.put("thread", getThreadDetails().getData());
        allData.put("runtime", getRuntimeDetails().getData());
        
        return PubResult.success(allData);
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
}
