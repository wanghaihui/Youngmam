package com.xiaobukuaipao.youngmam.form.validator;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class EmptyValidator extends Validator {

    public EmptyValidator(String message) {
        super(message);
    }

    public boolean isValid(EditText editText) {
        return TextUtils.getTrimmedLength(editText.getText()) > 0;
    }
}
