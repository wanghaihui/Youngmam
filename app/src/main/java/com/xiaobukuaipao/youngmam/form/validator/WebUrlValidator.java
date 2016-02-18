package com.xiaobukuaipao.youngmam.form.validator;

import android.os.Build;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class WebUrlValidator extends PatternValidator {
    public WebUrlValidator(String errorMessage) {
        super(errorMessage, Build.VERSION.SDK_INT>=8? Patterns.WEB_URL: Pattern.compile(".*"));
    }
}
