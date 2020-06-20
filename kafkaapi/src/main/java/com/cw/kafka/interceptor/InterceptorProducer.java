package com.cw.kafka.interceptor;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * producer主程序
 * @author 陈小哥cw
 * @date 2020/6/20 14:18
 */
public class InterceptorProducer {

    public static void main(String[] args) {
        // 1 设置配置信息
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

        properties.put(ProducerConfig.RETRIES_CONFIG, 0);

        // 2.构建拦截器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add("com.cw.kafka.interceptor.TimeInterceptor");
        interceptors.add("com.cw.kafka.interceptor.CounterInterceptor");
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);


        String topic = "first";
        Producer<String, String> producer = new KafkaProducer<>(properties);


        // 3.发送消息
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, "message" + i);
            producer.send(record);
        }

        // 4.一定要关闭producer，这样才会调用interceptor的close方法
        producer.close();
    }
}
