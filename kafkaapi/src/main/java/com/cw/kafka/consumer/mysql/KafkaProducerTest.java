package com.cw.kafka.consumer.mysql;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

/**
 * @author 陈小哥cw
 * @date 2020/6/19 19:22
 */
public class KafkaProducerTest {
    static Properties properties = null;
    static KafkaProducer<String, String> producer = null;

    static {
        properties = new Properties();
        // kafka集群，broker-list
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "cm1:9092,cm2:9092,cm3:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 重试次数
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // 批次大小
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // 等待时间
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        // RecordAccumulator缓冲区大小
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        producer = new KafkaProducer<String, String>(properties);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        for (int i = 0; i < 100; i++) {
            System.out.println("第" + (i + 1) + "条消息开始发送");
            sendData();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        producer.close();
    }

    public static String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        int random = new Random().nextInt(1000);
        digest.update((input + random).getBytes());
        byte[] bytes = digest.digest();
        BigInteger bi = new BigInteger(1, bytes);
        String string = bi.toString(16);
        return string.substring(0, 3) + input + random;
    }

    public static void sendData() throws NoSuchAlgorithmException {
        String topic = "mysql_store_offset";

        producer.send(new ProducerRecord<String, String>(
                        topic,
                        generateHash(topic),
                        new Random().nextInt(1000) + "\t金锁家庭财产综合险(家顺险)\t1\t金锁家庭财产综合险(家顺险)\t213\t自住型家财险\t10\t家财保险\t44\t人保财险\t23:50.0"),
                new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            System.out.println("|----------------------------\n|topic\tpartition\toffset\n|" + metadata.topic() + "\t" + metadata.partition() + "\t" + metadata.offset() + "\n|----------------------------");
                        } else {
                            exception.printStackTrace();
                        }
                    }
                });
    }
}
