package com.lessons.sync.services;

import com.lessons.sync.interfaces.RefreshMapping;
import com.lessons.sync.workers.RefreshMappingWorker;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class RefreshService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshService.class);

    private final AtomicBoolean isRefreshInProgress = new AtomicBoolean(false);


    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource
    private ExceptionService exceptionService;

    @Resource
    private EmailService emailService;

    @Resource
    private RefreshApp16UsersMapping refreshApp16UsersMapping;



    /**
     * Refresh all ES mappings
     */
    public void refreshAlLMappings()  {
        logger.debug("refreshAllMappings() started.");
        long startTime = System.currentTimeMillis();

        try {
            if (this.isRefreshInProgress.get()) {
                logger.warn("Warning in refreshAlLMappings():  Refresh is still in progress.  So, ignoring this thread.");
                return;
            }

            // Set the flag to indicate that a refresh is in progress
            this.isRefreshInProgress.set(true);

            // R E F R E S H     A L L     M A P P I N G S    (asynchronously)
            List<RefreshMapping> listOfMappings = Arrays.asList(refreshApp16UsersMapping);

            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(5);
            int iMinPoolSize = 5;
            int iMaxPoolSize = 5;
            int iKeepAliveTime = 60;

            ThreadPoolExecutor executorService = new ThreadPoolExecutor(iMinPoolSize, iMaxPoolSize, iKeepAliveTime, TimeUnit.MINUTES, queue);

            // Submit the workers
            for (RefreshMapping rm : listOfMappings){
                Runnable workerThread = new RefreshMappingWorker(rm, this.elasticSearchService, this.exceptionService);
                executorService.execute(workerThread);
            }

            // The workers run in the background


            // Shutdown the executor (which prevents additional workers from being added)
            executorService.shutdown();

            // Wait for all of the worker threads to complete
            while(true) {
                try {
                    if (executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                        // The executor has fully shutdown.  So, break out of this while loop
                        break;
                    }
                }
                catch (InterruptedException e) {
                    logger.debug("While waiting for shutdown, this  interruptedException was raised", e);
                }
           }


        }
        catch (Exception e) {
            logger.error("Error in refreshAllMappings()", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace());

            // Save the exception to the database
            Integer exceptionId = exceptionService.saveException(e);

            // Send an email out asynchronously regarding exceptions (if needed)
            emailService.sendEmailRegardingExceptions(exceptionId);

            throw re;
        }
        finally {
            // Set the flag to indicate that a refresh is not in progress
            this.isRefreshInProgress.set(false);

            long endTime = System.currentTimeMillis();
            logger.info("refreshAllMappings() finished after {} msecs.", ((endTime - startTime)) );
        }
    }



}