package com.cw.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 同步发送API
 * @author 陈小哥cw
 * @date 2020/6/19 15:02
 */
public class SyncProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

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
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        // RecordAccumulator缓冲区大小
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);


        // 1.创建一个生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // 2.调用send方法
        for (int i = 0; i < 1000; i++) {
            // 消息发送时间间隔由linger.ms决定，这里我们改为1s可以看到效果
            RecordMetadata meta = producer.send(new ProducerRecord<String, String>("first", i + "", "message-" + i)).get();
            System.out.println("offset = " + meta.offset());
        }

        // 3.关闭生产者
        producer.close();
    }
}
