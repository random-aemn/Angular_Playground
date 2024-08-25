package com.lessons.sync.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.Callable;

public class SendEmailWorker implements Callable<String> {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailWorker.class);

    private final MimeMessage mimeMessage;

    /**
     * @param aMimeMessage holds the message to email out
     */
    public SendEmailWorker(MimeMessage aMimeMessage) {
        if (aMimeMessage == null) {
            throw new RuntimeException("Error in SendEmailWorker constructor:  The passed-in aMimeMessage is null.");
        }

        this.mimeMessage = aMimeMessage;
    }


    /**
     * Send the email out asynchronously
     * @return nothing
     */
    @Override
    public String call() {
       try {
           logger.debug("SendEmailWorker.call() started sending an email");
           Transport.send(this.mimeMessage);
           logger.debug("SendEmailWorker.call() finished sending an email");
       }
        catch(Exception e) {
           // Log the exception and swallow it (as we will not attempt to recover)
           logger.error("Error in SendEmailWorker.call()", e);
        }

       return "";

    }  // end of call()


}
