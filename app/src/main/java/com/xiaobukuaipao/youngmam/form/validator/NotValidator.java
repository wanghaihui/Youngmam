package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class NotValidator extends Validator {
    private Validator validator;
    public NotValidator(String errorMessage, Validator validator) {
        super(errorMessage);
        this.validator = validator;
    }

    public boolean isValid(EditText editText) {
        return ! validator.isValid(editText);
    }
}
