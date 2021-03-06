package com.cw.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * 异步发送不带回调函数的API
 *
 * @author 陈小哥cw
 * @date 2020/6/19 9:41
 */
public class CustomProducer {
    public static void main(String[] args) {

        Properties properties = new Properties();
        // kafka集群，broker-list
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "cm1:9092,cm2:9092,cm3:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 重试次数
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // 批次大小
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // 等待时间
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        // RecordAccumulator缓冲区大小
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);


        // 1.创建一个生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // 2.调用send方法
        for (int i = 0; i < 1000; i++) {
            producer.send(new ProducerRecord<String, String>("first", i + "", "message-" + i));
        }

        // 3.关闭生产者
        producer.close();
    }
}
