package com.lessons.sync.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AsyncService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    final int THREAD_COUNT = 200;

    private ExecutorService executorService = null;


    @PostConstruct
    public void init() {
        // Initialize the executor service
        this.executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    @PreDestroy
    public void destroy() {
        logger.debug("destroy() started.");

        // Spring is shutting down, so shutdown the executor service
        if (this.executorService != null) {
            executorService.shutdown();
        }

        logger.debug("destroy() finished.");
    }

    /**
     * Submit the passed-in worker to the ExecutorService (so it runs in the background)
     * @param aCallableOperation holds the worker
     */
    public void submit(Callable<?> aCallableOperation) {
        logger.debug("submit() started.");

        if (aCallableOperation == null) {
            throw new RuntimeException("Critical error in submit():  The passed-in callable is null.");
        }

        // Run this worker in the background
        this.executorService.submit(aCallableOperation);

        logger.debug("submit() finished.");
    }
}
