package com.castellanos94.fuzzylogic.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class AsynchronousService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskExecutor taskExecutor;

    public void executeAsynchronously() {

        MyThread myThread = applicationContext.getBean(MyThread.class);
        taskExecutor.execute(myThread);
    }

    public int getActiveThreads() {
        return ((ThreadPoolTaskExecutor) taskExecutor).getActiveCount();
    }

    public int getQueueSize() {
        return ((ThreadPoolTaskExecutor) taskExecutor).getThreadPoolExecutor().getQueue().size();
    }

    /**
     * Created by gkatzioura on 10/18/17.
     */
    @Component
    @Scope("prototype")
    public static class MyThread implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(MyThread.class);

        @Override
        public void run() {
            LOGGER.info("Start thread");
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOGGER.info("End thread");
        }
    }
}