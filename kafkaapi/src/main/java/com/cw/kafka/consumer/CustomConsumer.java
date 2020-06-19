package com.cw.kafka.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * 手动提交
 * 同步提交offset
 *
 * @author 陈小哥cw
 * @date 2020/6/19 15:40
 */
public class CustomConsumer {
    public static void main(String[] args) {

        Properties properties = new Properties();
        // kafka集群，broker-list
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "cm1:9092,cm2:9092,cm3:9092");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 消费者组，只要group.id相同，就属于同一个消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        // 关闭自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // 1.创建一个消费者
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        // 消费者订阅topic
        consumer.subscribe(Arrays.asList("first"));

        // 2.调用poll
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("topic = " + record.topic() + " offset = " + record.offset() + " value = " + record.value());
            }
            // 同步提交，当前线程会阻塞直到offset提交成功
            // consumer.commitSync();
            // 异步提交，没有失败重试机制，故有可能提交失败
            consumer.commitAsync(new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                    if (exception != null) {
                        System.err.println("Commit failed for" + offsets);
                    }
                }
            });

        }
    }
}
