package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AsynchronousService {
    static final List<TaskThread> activeTasks = Collections.synchronizedList(new ArrayList<>());

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

    public void stopTask(EurekaTask eurekaTask) {
        //TODO: incorporar estragia de monitoreo https://stackoverflow.com/questions/35571395/how-to-access-running-threads-inside-threadpoolexecutor
        synchronized (activeTasks) {
            boolean anIf = activeTasks.removeIf(taskThread -> {
                if (taskThread.getTask().getId().equals(eurekaTask.getId())) {
                    taskThread.stop();
                    return true;
                }
                return false;
            });

        }
    }

    public ArrayList<String> getLog(EurekaTask task) {
        for (TaskThread t : activeTasks) {
            if (t.getTask().getId().equals(task.getId())) {
                return t.getLog();
            }
        }
        return null;
    }


}