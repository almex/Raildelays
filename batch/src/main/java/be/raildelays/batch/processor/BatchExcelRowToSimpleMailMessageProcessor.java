/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

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
