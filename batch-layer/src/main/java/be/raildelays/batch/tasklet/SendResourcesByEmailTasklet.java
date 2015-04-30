package be.raildelays.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Given a set of resources we send them by an e-mail.
 *
 * @author Almex
 * @since 1.2
 */
public class SendResourcesByEmailTasklet implements Tasklet, InitializingBean {

    private Resource[] resources;
    private JavaMailSender mailSender;
    private String subject;
    private String from;
    private String to;
    private String plainText = "";
    private String plainHtml = "";
    private MimeMessageHelper helper;


    @Override
    public void afterPropertiesSet() throws Exception {
        helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
        helper.setSubject(this.subject);
        helper.setFrom(this.from);
        helper.setTo(this.to);
        helper.setText(plainText, plainHtml);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        RepeatStatus status = RepeatStatus.CONTINUABLE;

        if (chunkContext.getStepContext().getStepExecution().getReadCount() < resources.length) {
            // On each iteration we add as an attachment a resource
            Resource resource = resources[chunkContext.getStepContext().getStepExecution().getReadCount()];

            helper.addAttachment(resource.getFilename(), resource.getFile());
            // We confirm that we read one resource
            contribution.incrementReadCount();
        } else {
            // We send the e-mail on the last iteration
            this.mailSender.send(helper.getMimeMessage());
            // We confirm the number of attachments
            contribution.incrementWriteCount(resources.length);
            status = RepeatStatus.FINISHED;
        }

        return status;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
