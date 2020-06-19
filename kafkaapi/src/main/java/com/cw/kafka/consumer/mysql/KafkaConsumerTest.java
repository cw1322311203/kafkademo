package com.cw.kafka.consumer.mysql;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 陈小哥cw
 * @date 2020/6/19 20:12
 */
public class KafkaConsumerTest {
    private static Properties properties = null;
    private static String group = "mysql_offset";
    private static String topic = "mysql_store_offset";
    private static KafkaConsumer<String, String> consumer;

    static {
        properties = new Properties();
        // kafka集群，broker-list
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "cm1:9092,cm2:9092,cm3:9092");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 消费者组，只要group.id相同，就属于同一个消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        // 关闭自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // 1.创建一个消费者
        consumer = new KafkaConsumer<>(properties);
    }

    public static void main(String[] args) {

        consumer.subscribe(Arrays.asList(topic), new ConsumerRebalanceListener() {

            // rebalance之前将记录进行保存
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                for (TopicPartition partition : partitions) {
                    // 获取分区
                    int sub_topic_partition_id = partition.partition();
                    // 对应分区的偏移量
                    long sub_topic_partition_offset = consumer.position(partition);
                    String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(
                            new Date(
                                    new Long(
                                            System.currentTimeMillis()
                                    )
                            )
                    );

                    DBUtils.update("replace into offset values(?,?,?,?,?)",
                            new Offset(
                                    group,
                                    topic,
                                    sub_topic_partition_id,
                                    sub_topic_partition_offset,
                                    date
                            )
                    );
                }
            }

            // rebalance之后读取之前的消费记录，继续消费
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                for (TopicPartition partition : partitions) {
                    int sub_topic_partition_id = partition.partition();
                    long offset = DBUtils.queryOffset(
                            "select sub_topic_partition_offset from offset where consumer_group=? and sub_topic=? and sub_topic_partition_id=?",
                            group,
                            topic,
                            sub_topic_partition_id
                    );
                    System.out.println("partition = " + partition + "offset = " + offset);
                    consumer.seek(partition, offset);

                }
            }
        });

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            List<Offset> offsets = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(
                        new Date(
                                new Long(
                                        System.currentTimeMillis()
                                )
                        )
                );
                offsets.add(new Offset(group, topic, record.partition(), record.offset(), date));

                System.out.println("|---------------------------------------------------------------\n" +
                        "|group\ttopic\tpartition\toffset\ttimestamp\n" +
                        "|" + group + "\t" + topic + "\t" + record.partition() + "\t" + record.offset() + "\t" + record.timestamp() + "\n" +
                        "|---------------------------------------------------------------"
                );
            }
            for (Offset offset : offsets) {
                DBUtils.update("replace into offset values(?,?,?,?,?)", offset);
            }
            offsets.clear();
        }
    }
}
