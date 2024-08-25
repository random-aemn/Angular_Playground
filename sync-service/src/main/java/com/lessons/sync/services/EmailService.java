package com.lessons.sync.services;

import com.lessons.sync.workers.SendEmailWorker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final String PROD_MODE  = "prod";
    private final String OFF_MODE   = "off";
    private final String DEMO_MODE  = "demo";

    @Value("${email.mode}")      // Possible values are "off", "demo", or "prod"
    private String emailMode;

    @Value("${email.demo.to.address:}")       // In demo mode, all emails are sent to this address
    private String demoModeEmailToAddress;

    @Value("${email.from.label:}")
    private String emailFromLabel;

    @Value("${email.from.address:}")
    private String emailFromAddress;

    @Value("${email.smtp.host.url:}")              // This url **CONTAINS** the email smtp server
    private String smtpUrlContainingHostname;

    @Value("${email.smtp.port:25}")
    private Integer smtpPort;

    @Value("${email.smtp.debug:false}")
    private boolean emailShowDebugging;

    @Value("${email.exceptions.enabled:false}")
    private boolean emailExceptionsEnabled;

    @Value("${email.exceptions.to.address:}")
    private List<String> listOfExceptionEmailRecipients;


    private Session emailSession;                   // This emailSession object is used to send out emails


    @Resource
    private AsyncService asyncService;


    /**
     * Initialize the EmailService
     * @throws Exception if there are problems getting the smtp host from the url
     */
    @PostConstruct
    public void init() throws Exception {
        logger.debug("init() started: ");

        this.emailMode = this.emailMode.toLowerCase().trim();

        // If any email properties are invalid, then throw a RuntimeException so the webapp does not startup
        validateEmailProperties();

        if (this.emailMode.equalsIgnoreCase(OFF_MODE)) {
            logger.warn("Email is disabled.  Email mode is {}.  No emails will be sent out.", this.emailMode);
            return;
        }

        // Extract the puppet master hostname from the ldap url
        String smtpHostServer =  getHostnameFromUrl(this.smtpUrlContainingHostname);

        // Setup the email Session object
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", this.smtpPort);
        this.emailSession = Session.getInstance(props, null);

        this.emailSession.setDebug(this.emailShowDebugging);


        if (this.emailMode.equalsIgnoreCase(DEMO_MODE)) {
            // Emails are running in demo mode
            logger.debug("init() finished.  Email mode is {}.  Email from address is {}.  Sending all emails to {}.  Using smtp server at {} on port {}", this.emailMode, this.emailFromAddress, this.demoModeEmailToAddress, smtpHostServer, this.smtpPort);
        }
        else {
            // Emails are running in prod mode
            logger.debug("init() finished.  Email mode is {}.  Email from address is {}.   Using smtp server at {} on port {}", this.emailMode, this.emailFromAddress, smtpHostServer, this.smtpPort);
        }
    }



    private void validateEmailProperties() {
        if (! this.emailMode.equalsIgnoreCase(DEMO_MODE) && (! this.emailMode.equalsIgnoreCase(PROD_MODE)) && (! this.emailMode.equalsIgnoreCase(OFF_MODE))) {
            throw new RuntimeException("Error in init():  The email.mode mode value in the application.yaml is not valid.  It must be either 'off', 'demo', or 'prod' ");
        }

        if (this.emailMode.equalsIgnoreCase(DEMO_MODE) || this.emailMode.equalsIgnoreCase(PROD_MODE)) {
            // We are running in demo or prod mode.  So, there must be an email-from-label and email-from-address

            if (StringUtils.isBlank(this.emailFromAddress)) {
                // The email.from.address property is missing
                throw new RuntimeException("Error in init():  The email.mode mode is " + this.emailMode + " but the email.from.address value is missing.  If you run in this mode, you must set the email.from.address property -- e.g., noreply@rbr-tech.com .");
            }

            if (StringUtils.isBlank(this.emailFromLabel)) {
                // The email.from.labe property is missing
                throw new RuntimeException("Error in init():  The email.mode mode is " + this.emailMode + " but the email.from.label value is missing.  If you run in this mode, you must set the email.from.label property -- e.g., noreply");
            }

            if (StringUtils.isBlank(this.smtpUrlContainingHostname)) {
                // The email.smtp.host.url property is missing
                throw new RuntimeException("Error in init():  The email.mode mode is " + this.emailMode + " but the email.smtp.host.url value is missing.  If you run in this mode, you must set the email.smtp.host.url property -- e.g., http://localhost/");
            }

        }

        if (this.emailMode.equalsIgnoreCase(DEMO_MODE)) {
            if (StringUtils.isBlank(this.demoModeEmailToAddress)) {
                // We are running in demo mode and there is demo-email-to address
                throw new RuntimeException("Error in init():  The email.mode mode is " + this.emailMode + " but the email.demo.to.address value is missing.  If you run in this mode, you must set the email.demo.to.address property.");
            }
        }

        if (this.emailExceptionsEnabled) {
            if ((this.listOfExceptionEmailRecipients == null) || (this.listOfExceptionEmailRecipients.size() == 0)) {
                throw new RuntimeException("Error in init():  The emailing of exceptions is enabled but the list of email-exception-recipients is null or empty.");
            }
        }
    }




    /**
     * Extract the hostname from a valid url string
     *
     * @param url holds a valid url
     * @return the hostname part of the url
     * @throws URISyntaxException if the url is invalid
     */
    private String getHostnameFromUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String hostname = uri.getHost();
        return hostname;
    }



    /**
     * Attempt to send the email out to *multiple* recipients and swallow the error
     *
     * @param aEmailToList holds a list of recipient email addresses
     * @param aEmailSubject holds the subject
     * @param aEmailBody    holds the body
     */
    public void sendEmailMultipleRecipients(List<String> aEmailToList, String aEmailSubject, String aEmailBody) {
        try {
            // Attempt to send the email out (and swallow any exceptions)
            sendEmailMultipleRecipientsInternal(aEmailToList, aEmailSubject, aEmailBody);
        }
        catch(Exception e) {
            logger.warn("Warning in sendEmailMultipleRecipients().  This Exception is IGNORED", e);

            // Swallow the exception
        }
    }


    /**
     * Send an email out to multiple recipients
     * @param aEmailToList holds a list of recipient email strings
     * @param aEmailSubject holds the email subject
     * @param aEmailBody holds the email body
     * @throws Exception if something bad happens
     */
    private void sendEmailMultipleRecipientsInternal(List<String> aEmailToList, String aEmailSubject, String aEmailBody) throws Exception {
        String csvOfToEmails = StringUtils.join(aEmailToList, ", ");
        logger.debug("sendEmailMultipleRecipientsInternal() started.  fromEmail={}  toEmails={}  subject={}", this.emailFromAddress, csvOfToEmails, aEmailSubject);

        if ( (aEmailToList == null) || (aEmailToList.size() == 0)) {
            // The list of emails is null or empty.   So, do not send an email
            logger.warn("Warning in sendEmailMultipleRecipientsInternal():  The list of emails is null or empty.  So, the email will NOT be sent out");
            return;
        }

        if (this.emailMode.equalsIgnoreCase(DEMO_MODE)) {
            // We are running in demo mode.  So, *override* the email-to address with the demo-mode-email-to-address
            logger.debug("Email mode is {} but would have sent an email to {} with the subject of \"{}\"", this.emailMode, csvOfToEmails, aEmailSubject);

            sendEmailInternal(this.demoModeEmailToAddress, aEmailSubject, aEmailBody);
        }

        if (this.emailMode.equalsIgnoreCase(OFF_MODE)) {
            // We are running in off mode.  So, log the email message
            logger.debug("Email mode is {} but would have sent an email to {} with the subject of \"{}\"", this.emailMode, csvOfToEmails, aEmailSubject);
            return;
        }

        if (this.emailMode.equalsIgnoreCase(PROD_MODE)) {
            // We are running in off mode.  So, log the email message
            logger.debug("Email mode is {} so sending email to to {} with the subject of \"{}\"", this.emailMode, csvOfToEmails, aEmailSubject);
        }

        // Construct the Email Message to send to multiple recipients
        MimeMessage msg = new MimeMessage(this.emailSession);
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        msg.setFrom(new InternetAddress(this.emailFromAddress, this.emailFromLabel));
        msg.setReplyTo(InternetAddress.parse(this.emailFromAddress, false));
        msg.setSubject(aEmailSubject, "UTF-8");
        msg.setText(aEmailBody, "UTF-8");
        msg.setSentDate(new Date());

        // Add the multiple recipients to this email
        for (String emailTo: aEmailToList) {
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo, false));
        }


        // Build the sendEmailWorker object
        SendEmailWorker sendEmailWorker = new SendEmailWorker(msg);

        // Start the sendEmailWorker in the background  (which will attempt to send the email out ASYNCHROOUSLY)
        // NOTE:  We do *NOT* want to wait for the email server (as it can be 90 seconds for a timeout)
        this.asyncService.submit(sendEmailWorker);

        logger.debug("sendEmailMultipleRecipientsInternal() finished");
    }


    /**
     * Attempt to send the email out and swallow any exceptions
     *
     * @param aEmailTo holds the destination email address
     * @param aEmailSubject holds the subject
     * @param aEmailBody    holds the body
     */
    public void sendEmail(String aEmailTo, String aEmailSubject, String aEmailBody) {
        try {
            // Attempt to send the email out (and swallow any exceptions)
            sendEmailInternal(aEmailTo, aEmailSubject, aEmailBody);
        }
        catch(Exception e) {
            logger.warn("Warning in sendEmail().  This Exception is IGNORED", e);

            // Swallow the exception
        }
    }


    /**
     * If Running in DEMO mode, then send emails out to the demo-to-email-address
     * If Running in PROD mode and email is blank, then log a warning (and no email is sent)
     * If the email-to address is blank, then an email is *NOT* send out
     *
     * @param aEmailTo holds the destination email address
     * @param aEmailSubject holds the subject
     * @param aEmailBody    holds the body
     * @throws Exception if something bad happens
     */
    private void sendEmailInternal(String aEmailTo, String aEmailSubject, String aEmailBody) throws Exception {
        logger.debug("sendEmail() started.  fromEmail={}  toEmail={}  subject={}", this.emailFromAddress, aEmailTo, aEmailSubject);

        String finalEmailToAddress = aEmailTo;


        if (this.emailMode.equalsIgnoreCase(DEMO_MODE)) {
            // We are running in demo mode.  So, *override* the email-to address with the demo-mode-email-to-address
            finalEmailToAddress = this.demoModeEmailToAddress;
        }

        if (StringUtils.isBlank(finalEmailToAddress)) {
            // The email to address is blank.  So, do not send an email
            logger.warn("Warning in sendEmailInternal():  The email to address is blank.  So, the email will NOT be sent out");
            return;
        }

        if (this.emailMode.equalsIgnoreCase(OFF_MODE)) {
            // We are running in off mode.  So, log the email message
            logger.debug("Email mode is {} but would have sent an email to {} with the subject of \"{}\"", this.emailMode, aEmailTo, aEmailSubject);
            return;
        }

        if (this.emailMode.equalsIgnoreCase(PROD_MODE)) {
            // We are running in off mode.  So, log the email message
            logger.debug("Email mode is {} so sending email to to {} with the subject of \"{}\"", this.emailMode, aEmailTo, aEmailSubject);
        }

        // Construct the Email Message
        MimeMessage msg = new MimeMessage(this.emailSession);
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        msg.setFrom(new InternetAddress(this.emailFromAddress, this.emailFromLabel));
        msg.setReplyTo(InternetAddress.parse(this.emailFromAddress, false));
        msg.setSubject(aEmailSubject, "UTF-8");
        msg.setText(aEmailBody, "UTF-8");
        msg.setSentDate(new Date());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(finalEmailToAddress, false));

        // Build the sendEmailWorker object
        SendEmailWorker sendEmailWorker = new SendEmailWorker(msg);

        // Start the sendEmailWorker in the background  (which will attempt to send the email out ASYNCHROOUSLY)
        // NOTE:  We do *NOT* want to wait for the email server (as it can be 90 seconds for a timeout)
        this.asyncService.submit(sendEmailWorker);

        logger.debug("sendEmail() finished");
    }


    /**
     * Send an email out to the users in the listOfExceptionEmailRecipients
     * @param aExceptionId
     */
    public void sendEmailRegardingExceptions(Integer aExceptionId) {
        if (!this.emailExceptionsEnabled) {
            // Emails are disabled.  So, do nothing
            logger.warn("Emailing of exceptions is disabled so I will NOT send an email regarding exception {}", aExceptionId);
        }

        // Construct the email subject
        String emailSubject = "NCCS Alert: Sync Service Raised Exception " + aExceptionId;

        // Construct the email body
        String emailBody =
                "** This is an automatically generated email. Please do not respond to this email.** " +
                "The Sync-Service through an exception\n" +
                "View the Exceptions here\n" +
                "  https://tesseract.cloud.dcsa.mil/nccs-admin/  \n" +
                "\n" +
                "** This is an automatically generated email. Please do not respond to this email. **";

        // Send the email out asynchronosly
        // NOTE:  If the email address is blank, then do not send an email out
        sendEmailMultipleRecipients(this.listOfExceptionEmailRecipients, emailSubject, emailBody);
    }
}
