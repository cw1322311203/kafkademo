package com.cw.kafka.producer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 测试Future
 *
 * @author 陈小哥cw
 * @date 2020/6/19 14:34
 */
public class TestFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println("i = " + i);
                }
            }
        });

        future.get();//阻塞

        System.out.println("=======================");

        executor.shutdown();
    }
}
