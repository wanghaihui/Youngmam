package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 * Validator abstract class. To be used with FormEditText
 * 验证器
 */
public abstract class Validator {

    protected String errorMessage;

    public Validator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Should check if the EditText is valid
     */
    public abstract boolean isValid(EditText editText);

    public boolean hasErrorMessage() {
        return errorMessage != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
