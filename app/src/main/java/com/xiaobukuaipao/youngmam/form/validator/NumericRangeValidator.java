package com.xiaobukuaipao.youngmam.form.validator;

import android.widget.EditText;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class NumericRangeValidator extends Validator {
    private int min, max;

    public NumericRangeValidator(String errorMessage, int min, int max) {
        super(errorMessage);
        this.min = min;
        this.max = max;
    }

    public boolean isValid(EditText editText) {
        try {
            int value = Integer.parseInt(editText.getText().toString());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
