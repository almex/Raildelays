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

package be.raildelays.batch.listener;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.railtime.TwoDirections;

/**
 * @author Almex
 */
public class LogStep1ItemProcessorListener extends AbstractLogItemProcessorListener<Object, Object> {
    @Override
    public void infoInput(String message, Object input) {
        if (input instanceof TwoDirections) {
            logger.info(message, (TwoDirections) input);
        }
    }

    @Override
    public void infoOutput(String message, Object output) {
        if (output instanceof LineStop) {
            LineStop current = (LineStop) output;

            while (current != null) {
                logger.info(message, current);
                current = current.getNext();
            }
        }
    }
}
