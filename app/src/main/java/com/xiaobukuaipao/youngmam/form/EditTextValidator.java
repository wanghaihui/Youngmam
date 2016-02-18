package com.xiaobukuaipao.youngmam.form;

import android.content.Context;
import android.text.TextWatcher;

import com.xiaobukuaipao.youngmam.form.validator.Validator;

/**
 * Created by xiaobu1 on 15-5-12.
 * Interface for encapsulating(封装的) validation(验证) of an EditText control
 * EditText验证器接口
 */
public interface EditTextValidator {
    /**
     * Add a validator to this FormEditText. The validator will be added in the
     * queue of the current validators.
     *
     * @param theValidator
     * @throws IllegalArgumentException
     *             if the validator is null
     */
    public void addValidator(Validator theValidator) throws IllegalArgumentException;

    /**
     * This should be used with {@link #addTextChangedListener(TextWatcher)}. It
     * fixes the non-hiding error popup behaviour.
     */
    public TextWatcher getTextWatcher();

    /**
     * 允许为空
     */
    public boolean isEmptyAllowed();

    /**
     * Resets the {@link Validator}
     */
    public void resetValidator(Context context);

    /**
     * Calling *testValidity()* will cause the EditText to go through
     * customValidators and call {@link #Validator.isValid(EditText)}
     * Same as {@link #testValidity(boolean)} with first parameter true
     * @return true if the validity passes false otherwise.
     * 测试合法性
     */
    public boolean testValidity();

    /**
     * Calling *testValidity()* will cause the EditText to go through
     * customValidators and call {@link #Validator.isValid(EditText)}
     * @param showUIError determines if this call should show the UI error.
     * @return true if the validity passes false otherwise.
     */
    public boolean testValidity(boolean showUIError);

    public void showUIError();

    // 表达式
    final int TEST_REGEXP = 0;
    // 数字
    final int TEST_NUMERIC = 1;
    // 字母
    final int TEST_ALPHA = 2;
    // 数字和字母
    final int TEST_ALPHANUMERIC = 3;
    // 邮箱
    final int TEST_EMAIL = 4;
    // 电话
    final int TEST_PHONE = 5;
    // 网页Url
    final int TEST_WEBURL = 6;
    // 日期
    final int TEST_DATE = 7;
    // 数字范围
    final int TEST_NUMERIC_RANGE = 8;
    // No Check
    final int TEST_NOCHECK = 9;

}
