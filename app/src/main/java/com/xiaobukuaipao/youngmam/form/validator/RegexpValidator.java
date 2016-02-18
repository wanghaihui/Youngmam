package com.xiaobukuaipao.youngmam.form.validator;

import java.util.regex.Pattern;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class RegexpValidator extends PatternValidator {
    public RegexpValidator(String message, String regexp) {
        super(message, Pattern.compile(regexp));
    }
}
