package com.xiaobukuaipao.youngmam.form.validator;

import android.os.Build;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class EmailValidator extends PatternValidator {

    public EmailValidator(String errorMessage) {
        super(errorMessage, Build.VERSION.SDK_INT>=8? Patterns.EMAIL_ADDRESS: Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        ));
    }
}
