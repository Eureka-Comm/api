package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class AsynchronousService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskExecutor taskExecutor;

    public void executeAsynchronously(EurekaTask eurekaTask) {

        TaskThread myThread = applicationContext.getBean(TaskThread.class, eurekaTask);
        taskExecutor.execute(myThread);
    }

    public int getActiveThreads() {
        return ((ThreadPoolTaskExecutor) taskExecutor).getActiveCount();
    }

    public int getQueueSize() {
        return ((ThreadPoolTaskExecutor) taskExecutor).getThreadPoolExecutor().getQueue().size();
    }


}