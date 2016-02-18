package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class DummyValidator extends Validator {
    public DummyValidator() {
        super(null);
    }

    public boolean isValid(EditText editText) {
        return true;
    }
}
