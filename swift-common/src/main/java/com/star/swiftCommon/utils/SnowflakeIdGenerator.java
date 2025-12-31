package com.star.swiftCommon.utils;

/**
 * 雪花算法 ID 生成器
 * <p>
 * 雪花算法生成的 ID 结构：
 * - 1 位符号位（始终为 0）
 * - 41 位时间戳（毫秒级，可用 69 年）
 * - 10 位机器 ID（5 位数据中心 ID + 5 位工作机器 ID，支持 1024 个节点）
 * - 12 位序列号（每毫秒可生成 4096 个 ID）
 * <p>
 * 优点：
 * - 分布式环境下不依赖数据库，可独立生成 ID
 * - 性能高，单机每秒可生成数百万个 ID
 * - ID 按时间递增，有利于数据库索引
 * - 不暴露系统信息，安全性较高
 *
 * @author SHOOTING_STAR_C
 */
public class SnowflakeIdGenerator {
    
    /**
     * 起始时间戳（2024-01-01 00:00:00）
     */
    private static final long START_TIMESTAMP = 1704067200000L;
    
    /**
     * 机器 ID 所占的位数
     */
    private static final long WORKER_ID_BITS = 5L;
    
    /**
     * 数据中心 ID 所占的位数
     */
    private static final long DATACENTER_ID_BITS = 5L;
    
    /**
     * 序列号所占的位数
     */
    private static final long SEQUENCE_BITS = 12L;
    
    /**
     * 机器 ID 的最大值
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    
    /**
     * 数据中心 ID 的最大值
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    
    /**
     * 序列号的最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    /**
     * 机器 ID 向左移的位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    
    /**
     * 数据中心 ID 向左移的位数
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    /**
     * 时间戳向左移的位数
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    
    /**
     * 工作机器 ID（0 ~ 31）
     */
    private final long workerId;
    
    /**
     * 数据中心 ID（0 ~ 31）
     */
    private final long datacenterId;
    
    /**
     * 毫秒内序列号（0 ~ 4095）
     */
    private long sequence = 0L;
    
    /**
     * 上次生成 ID 的时间戳
     */
    private long lastTimestamp = -1L;
    
    /**
     * 单例实例
     */
    private static volatile SnowflakeIdGenerator instance;
    
    /**
     * 私有构造函数
     *
     * @param workerId     工作机器 ID（0 ~ 31）
     * @param datacenterId 数据中心 ID（0 ~ 31）
     */
    private SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    /**
     * 获取单例实例（默认 workerId=0, datacenterId=0）
     *
     * @return SnowflakeIdGenerator 实例
     */
    public static SnowflakeIdGenerator getInstance() {
        return getInstance(0L, 0L);
    }
    
    /**
     * 获取单例实例
     *
     * @param workerId     工作机器 ID（0 ~ 31）
     * @param datacenterId 数据中心 ID（0 ~ 31）
     * @return SnowflakeIdGenerator 实例
     */
    public static SnowflakeIdGenerator getInstance(long workerId, long datacenterId) {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(workerId, datacenterId);
                }
            }
        }
        return instance;
    }
    
    /**
     * 生成下一个 ID
     *
     * @return ID
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();
        
        // 如果当前时间小于上次生成 ID 的时间，说明系统时钟回退，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }
        
        // 如果是同一毫秒内生成的
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = getNextTimestamp();
            }
        } else {
            // 不同毫秒内，序列号重置为 0
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // 生成 ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳（毫秒）
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取下一毫秒的时间戳
     *
     * @return 下一毫秒的时间戳
     */
    private long getNextTimestamp() {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * 解析 ID 获取时间戳
     *
     * @param id ID
     * @return 时间戳
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
    }
    
    /**
     * 解析 ID 获取数据中心 ID
     *
     * @param id ID
     * @return 数据中心 ID
     */
    public static long parseDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }
    
    /**
     * 解析 ID 获取工作机器 ID
     *
     * @param id ID
     * @return 工作机器 ID
     */
    public static long parseWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }
    
    /**
     * 解析 ID 获取序列号
     *
     * @param id ID
     * @return 序列号
     */
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }
    
    /**
     * 生成 ID（便捷方法）
     *
     * @return ID
     */
    public static long generateId() {
        return getInstance().nextId();
    }
}
