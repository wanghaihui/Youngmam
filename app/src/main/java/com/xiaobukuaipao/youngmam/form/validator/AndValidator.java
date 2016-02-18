package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class AndValidator extends MultiValidator {
    public AndValidator() {
        super(null);
    }

    public AndValidator(Validator... validators) {
        super(null, validators);
    }

    public boolean isValid(EditText editText) {
        for (Validator validator : validators) {
            if (!validator.isValid(editText)) {
                this.errorMessage = validator.getErrorMessage();
                return false;
            }
        }
        return true;
    }
}
