package com.cw.kafka.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;

/**
 * ConsumerAPI自己维护offset
 *
 * @author 陈小哥cw
 * @date 2020/6/19 17:07
 */
public class CustomOffsetConsumer {

    private static Map<TopicPartition, Long> currentOffset = new HashMap<>();

    public static void main(String[] args) {
        //创建配置信息
        Properties properties = new Properties();
        // kafka集群broker-list
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "cm1:9092,cm2:9092,cm3:9092");

        // Key和Value的反序列化类
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 消费者组，只要group.id相同，就属于同一个消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");

        // 关闭自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // 1.创建一个消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        // 消费者订阅topic
        consumer.subscribe(Arrays.asList("first"), new ConsumerRebalanceListener() {
            // 该方法会在Rebalance之前调用
            // 提交当前负责的分区的offset
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                commitOffset(currentOffset);
            }

            // 该方法会在Rebalance之后调用
            // 定位新分配的分区的offset
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                currentOffset.clear();
                for (TopicPartition partition : partitions) {
                    consumer.seek(partition, getOffset(partition));//定位到最近提交的offset位置继续消费
                }
            }
        });

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);//消费者拉取数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                currentOffset.put(new TopicPartition(record.topic(), record.partition()), record.offset());
            }
            commitOffset(currentOffset);//异步提交
        }

    }

    //获取某分区的最新offset
    private static long getOffset(TopicPartition partition) {
        return 0;
    }

    //提交该消费者所有分区的offset
    private static void commitOffset(Map<TopicPartition, Long> currentOffset) {

    }
}
