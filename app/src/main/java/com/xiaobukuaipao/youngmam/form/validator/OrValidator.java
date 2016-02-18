package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class OrValidator extends MultiValidator {
    public OrValidator(String message, Validator... validators) {
        super(message, validators);
    }

    public boolean isValid(EditText editText) {
        for (Validator validator : validators) {
            if (validator.isValid(editText)) {
                return true;
            }
        }

        return false;
    }
}
