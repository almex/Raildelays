package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;

/**
 * Transform a {@link be.raildelays.batch.bean.BatchExcelRow} into {@link org.springframework.mail.SimpleMailMessage}
 * in order to send the e-mail with delay more than one hour.
 *
 * @author Almex
 * @since 1.2
 */
public class BatchExcelRowToSimpleMailMessageProcessor implements ItemProcessor<BatchExcelRow, SimpleMailMessage> {

    private String from;

    private String subject;

    private String to;

    @Override
    public SimpleMailMessage process(BatchExcelRow item) throws Exception {
        SimpleMailMessage result = new SimpleMailMessage();

        result.setFrom(from);
        result.setReplyTo(from);
        result.setSubject(subject);
        result.setSentDate(new Date());
        result.setTo(to);
        result.setText(item.toString());

        return result;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
