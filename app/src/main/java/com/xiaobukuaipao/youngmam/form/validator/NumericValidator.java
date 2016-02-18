package com.xiaobukuaipao.youngmam.form.validator;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class NumericValidator extends Validator {

    public NumericValidator(String errorMessage) {
        super(errorMessage);
    }

    public boolean isValid(EditText editText) {
        return TextUtils.isDigitsOnly(editText.getText());
    }
}
