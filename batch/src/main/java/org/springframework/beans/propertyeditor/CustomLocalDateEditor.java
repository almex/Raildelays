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

package org.springframework.beans.propertyeditor;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Custom {@link PropertyEditorSupport} to convert from {@link String} to
 * {@link LocalDate}.
 *
 * @autor Almex
 * @see org.springframework.beans.propertyeditors.CustomDateEditor
 * @see LocalDate
 * @since 2.0
 */
public class CustomLocalDateEditor extends PropertyEditorSupport {

    private final boolean allowEmpty;
    private DateTimeFormatter formatter;

    /**
     * Create a new CustomLocalDateEditor instance, using the given format for
     * parsing and rendering.
     * <p>
     * The "allowEmpty" parameter states if an empty String should be allowed
     * for parsing (i.e.: get interpreted as {@code null} value).
     *
     * @param dateFormat DateFormat to use for parsing and rendering
     * @param allowEmpty if empty strings should be allowed
     * @throws IllegalArgumentException when {@code allowEmpty} is {@code false} and that we have a {@code null} value
     */
    public CustomLocalDateEditor(String dateFormat, boolean allowEmpty) {
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
        this.allowEmpty = allowEmpty;
    }

    /**
     * Format the ${@code LocalDate} as a {@code String} using the specified format.
     */
    @Override
    public String getAsText() {
        LocalDate value = (LocalDate) getValue();

        return (value != null ? value.format(this.formatter) : "");
    }

    /**
     * Parse the value from the given {@code text} using the specified format.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else {
            setValue(LocalDate.parse(text, this.formatter));
        }
    }
}
