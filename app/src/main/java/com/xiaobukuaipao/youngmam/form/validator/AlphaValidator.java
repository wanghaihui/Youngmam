package com.xiaobukuaipao.youngmam.form.validator;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class AlphaValidator extends RegexpValidator {
    public AlphaValidator(String message) {
        super(message, "[a-zA-Z\\./-]*");
    }
}
