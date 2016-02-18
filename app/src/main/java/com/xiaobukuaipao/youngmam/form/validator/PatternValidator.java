package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by xiaobu1 on 15-5-12.
 * 模式验证器
 */
public class PatternValidator extends Validator {
    private Pattern pattern;

    public PatternValidator(String errorMessage, Pattern pattern) {
        super(errorMessage);
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        this.pattern = pattern;
    }

    public boolean isValid(EditText editText) {
        return pattern.matcher(editText.getText()).matches();
    }
}
