package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TaskThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskThread.class);
    private EurekaTask task;

    public TaskThread(EurekaTask task) {
        this.task = task;
    }


    @Override
    public void run() {
        LOGGER.info("Start task:" + task.getId());
        task.getQuery().getPredicateTree();
    }
}