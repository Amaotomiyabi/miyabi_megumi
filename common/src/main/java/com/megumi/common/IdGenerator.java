package com.megumi.common;

import java.util.ResourceBundle;

/**
 * 2021/2/21
 * 雪花算法实现
 *
 * @author miyabi
 * @since 1.0
 */
public class IdGenerator {

    private static final IdGenerator instance;

    static {
        var machineId = Long.parseLong(ResourceBundle.getBundle("server").getString("machineId"));
        var datacenterId = Long.parseLong(ResourceBundle.getBundle("server").getString("datacenterId"));
        instance = new IdGenerator(machineId, datacenterId);
    }


    /**
     * 开始时间戳（2021.01.01 00：00：00)
     */
    private final long startTimestamp = 1609430400L;
    /**
     * 工作机器ID所占位数
     */
    private final int machineIdBits = 5;
    /**
     * 数据中心所占ID位数
     */
    private final int datacenterIdBits = 5;
    /**
     * 序列号所占位数
     */
    private final int seqBits = 12;
    /**
     * 机器ID左移位数
     */
    private final int machineShift = seqBits;
    /**
     * 数据中心左移位数
     */
    private final int datacenterShift = seqBits + machineIdBits;
    /**
     * 时间戳左移位数
     */
    private final int timestampShift = machineIdBits + seqBits + datacenterIdBits;
    /**
     * 最大数据中心ID
     */
    private final long maxdatacenterId = ~(-1L << datacenterIdBits);
    /**
     * 最大工作机器ID
     */
    private final long maxMachineId = ~(-1L << machineIdBits);
    /**
     * 最大序列号
     */
    private final long maxSeq = ~(-1L << seqBits);
    /**
     * 工作机器ID
     */
    private long machineId;
    /**
     * 数据中心ID
     */
    private long datacenterId;
    /**
     * 序列号
     */
    private long seq;
    /**
     * 最后一次获取的时间戳
     */
    private long lastTimestamp;

    public IdGenerator(long machineId, long datacenterId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException("machine's id is illegal");
        }
        if (datacenterId > maxdatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("datacenter's id is illegal");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public static IdGenerator getInstance() {
        return instance;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }

    /**
     * @return 下一个时间戳
     */
    public long nextTimestamp() {
        for (; ; ) {
            var temp = System.currentTimeMillis();
            if (temp > lastTimestamp) {
                return temp;
            }
        }
    }

    /**
     * @return 下一个ID
     */
    public synchronized long getNextId() {
        var timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("clock error");
        }
        if (timestamp == lastTimestamp) {
            seq = (seq + 1) & maxSeq;
            if (seq == 0) {
                timestamp = nextTimestamp();
            }
        } else {
            seq = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - startTimestamp) << timestampShift) | (datacenterId << datacenterShift) | (machineId << machineShift)
               | seq;
    }
}
