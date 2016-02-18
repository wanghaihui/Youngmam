package com.xiaobukuaipao.youngmam.form.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public abstract class MultiValidator extends Validator {

    protected final List<Validator> validators;

    public MultiValidator(String message, Validator... validators) {
        super(message);

        if (validators == null) {
            throw new NullPointerException("validators is null");
        }

        this.validators = new ArrayList<Validator>(Arrays.asList(validators));
    }

    public MultiValidator(String message) {
        super(message);
        this.validators = new ArrayList<Validator>();
    }

    public void enqueue(Validator newValidator) {
        validators.add(newValidator);
    }
}
