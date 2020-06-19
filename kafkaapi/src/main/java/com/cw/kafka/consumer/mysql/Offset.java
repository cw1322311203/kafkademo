package com.cw.kafka.consumer.mysql;

/**
 * @author 陈小哥cw
 * @date 2020/6/19 20:00
 */
public class Offset {
    private String consumer_group;
    private String sub_topic;
    private Integer sub_topic_partition_id;
    private Long sub_topic_partition_offset;
    private String timestamp;

    public Offset() {
    }

    public Offset(String consumer_group, String sub_topic, Integer sub_topic_partition_id, Long sub_topic_partition_offset, String timestamp) {
        this.consumer_group = consumer_group;
        this.sub_topic = sub_topic;
        this.sub_topic_partition_id = sub_topic_partition_id;
        this.sub_topic_partition_offset = sub_topic_partition_offset;
        this.timestamp = timestamp;
    }

    public String getConsumer_group() {
        return consumer_group;
    }

    public void setConsumer_group(String consumer_group) {
        this.consumer_group = consumer_group;
    }

    public String getSub_topic() {
        return sub_topic;
    }

    public void setSub_topic(String sub_topic) {
        this.sub_topic = sub_topic;
    }

    public Integer getSub_topic_partition_id() {
        return sub_topic_partition_id;
    }

    public void setSub_topic_partition_id(Integer sub_topic_partition_id) {
        this.sub_topic_partition_id = sub_topic_partition_id;
    }

    public Long getSub_topic_partition_offset() {
        return sub_topic_partition_offset;
    }

    public void setSub_topic_partition_offset(Long sub_topic_partition_offset) {
        this.sub_topic_partition_offset = sub_topic_partition_offset;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Offset{" +
                "consumer_group='" + consumer_group + '\'' +
                ", sub_topic='" + sub_topic + '\'' +
                ", sub_topic_partition_id=" + sub_topic_partition_id +
                ", sub_topic_partition_offset=" + sub_topic_partition_offset +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
