package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class StartupHousekeeper {
    private static final Logger logger = LoggerFactory.getLogger(StartupHousekeeper.class);
    @Autowired
    EurekaTaskRepository eurekaTaskRepository;
    @Autowired
    AsynchronousService service;
    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        logger.info("Startup checking pending task");
        AtomicLong count = new AtomicLong(0);
        eurekaTaskRepository.findAll().stream().filter(eurekaTask ->
            eurekaTask.getStatus() == EurekaTask.Status.Created || eurekaTask.getStatus() == EurekaTask.Status.Running).forEach(eurekaTask -> {
                //service.executeAsynchronously(eurekaTask);
                eurekaTask.setStatus(EurekaTask.Status.Failed);
                eurekaTaskRepository.save(eurekaTask);
                count.incrementAndGet();
        });
        logger.info("Failed tasks {}", count.get());
    }
}
