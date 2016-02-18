package com.xiaobukuaipao.youngmam.form.validator;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class AlphaNumericValidator extends RegexpValidator {
    public AlphaNumericValidator(String message) {
        super(message, "[a-zA-Z0-9 \\./-]*");
    }
}
