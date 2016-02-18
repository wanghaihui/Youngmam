package com.xiaobukuaipao.youngmam.form.validator;

import java.util.regex.Pattern;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class PhoneValidator extends PatternValidator {

    public PhoneValidator(String errorMessage) {
        super(errorMessage, Pattern.compile("^[1][0-9]{10}$"));
    }
}
