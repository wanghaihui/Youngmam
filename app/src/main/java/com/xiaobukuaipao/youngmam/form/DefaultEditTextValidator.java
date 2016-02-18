package com.xiaobukuaipao.youngmam.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.xiaobukuaipao.youngmam.R;
import com.xiaobukuaipao.youngmam.form.validator.AlphaNumericValidator;
import com.xiaobukuaipao.youngmam.form.validator.AlphaValidator;
import com.xiaobukuaipao.youngmam.form.validator.AndValidator;
import com.xiaobukuaipao.youngmam.form.validator.DateValidator;
import com.xiaobukuaipao.youngmam.form.validator.DummyValidator;
import com.xiaobukuaipao.youngmam.form.validator.EmailValidator;
import com.xiaobukuaipao.youngmam.form.validator.EmptyValidator;
import com.xiaobukuaipao.youngmam.form.validator.MultiValidator;
import com.xiaobukuaipao.youngmam.form.validator.NotValidator;
import com.xiaobukuaipao.youngmam.form.validator.NumericRangeValidator;
import com.xiaobukuaipao.youngmam.form.validator.NumericValidator;
import com.xiaobukuaipao.youngmam.form.validator.OrValidator;
import com.xiaobukuaipao.youngmam.form.validator.PhoneValidator;
import com.xiaobukuaipao.youngmam.form.validator.RegexpValidator;
import com.xiaobukuaipao.youngmam.form.validator.Validator;
import com.xiaobukuaipao.youngmam.form.validator.WebUrlValidator;

/**
 * Created by xiaobu1 on 15-5-12.
 */
public class DefaultEditTextValidator implements EditTextValidator {
    /**
     * 检测类型
     */
    protected int testType;

    protected EditText editText;

    /**
     * The custom validators setted using
     */
    protected MultiValidator mValidator;
    protected boolean emptyAllowed;
    protected String testErrorString;
    protected String classType;
    protected String customRegexp;
    protected String emptyErrorString;
    protected String customFormat;
    protected int minNumber;
    protected int maxNumber;
    private String defaultEmptyErrorString;
    protected String emptyErrorStringActual;

    private TextWatcher textWatcher;

    public DefaultEditTextValidator(EditText editText, Context context) {
        testType = EditTextValidator.TEST_NOCHECK;
        // 设置EditText
        setEditText(editText);
        // 重置验证器
        resetValidator(context);
    }

    public DefaultEditTextValidator(EditText editText,  Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FormEditText);
        emptyAllowed = typedArray.getBoolean(R.styleable.FormEditText_emptyAllowed, false);
        testType = typedArray.getInt(R.styleable.FormEditText_testType, EditTextValidator.TEST_NOCHECK);
        testErrorString = typedArray.getString(R.styleable.FormEditText_testErrorString);
        classType = typedArray.getString(R.styleable.FormEditText_classType);
        customRegexp = typedArray.getString(R.styleable.FormEditText_customRegexp);
        emptyErrorString = typedArray.getString(R.styleable.FormEditText_emptyErrorString);
        customFormat = typedArray.getString(R.styleable.FormEditText_customFormat);

        if (testType == TEST_NUMERIC_RANGE) {
            minNumber = typedArray.getInt(R.styleable.FormEditText_minNumber, Integer.MIN_VALUE);
            maxNumber = typedArray.getInt(R.styleable.FormEditText_maxNumber, Integer.MAX_VALUE);
        }
        typedArray.recycle();

        setEditText(editText);
        resetValidator(context);
    }

    @Override
    public void addValidator(Validator theValidator) throws IllegalArgumentException {
        if (theValidator == null) {
            throw new IllegalArgumentException("theValidator argument should not be null");
        }
        mValidator.enqueue(theValidator);
    }

    public String getClassType() {
        return classType;
    }

    public String getCustomRegexp() {
        return customRegexp;
    }

    public EditText getEditText() {
        return editText;
    }

    public String getTestErrorString() {
        return testErrorString;
    }

    public int getTestType() {
        return testType;
    }

    @Override
    public boolean isEmptyAllowed() {
        return emptyAllowed;
    }

    @Override
    public TextWatcher getTextWatcher() {
        if (textWatcher == null) {
            textWatcher = new TextWatcher() {
                public void afterTextChanged(Editable s) {

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged( CharSequence s, int start, int before, int count ) {
                    if (s != null && s.length() > 0 && editText.getError() != null) {
                        editText.setError(null);
                    }
                }
            };
        }
        return textWatcher;
    }

    @Override
    public void resetValidator(Context context)
    {
        // its possible the context may have changed so re-get the defaultEmptyErrorString
        defaultEmptyErrorString = context.getString(R.string.error_field_must_not_be_empty);
        setEmptyErrorString(emptyErrorString);

        mValidator = new AndValidator();

        Validator toAdd;

        switch ( testType ) {
            default:
            case TEST_NOCHECK:
                toAdd = new DummyValidator();
                break;

            case TEST_ALPHA:
                toAdd = new AlphaValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_only_standard_letters_are_allowed)
                                : testErrorString);
                break;

            case TEST_ALPHANUMERIC:
                toAdd = new AlphaNumericValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_this_field_cannot_contain_special_character)
                                        : testErrorString);
                break;

            case TEST_NUMERIC:
                toAdd = new NumericValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_only_numeric_digits_allowed)
                                : testErrorString);
                break;

            case TEST_NUMERIC_RANGE:
                toAdd = new NumericRangeValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_only_numeric_digits_range_allowed, minNumber, maxNumber)
                                : testErrorString, minNumber, maxNumber);
                break;

            case TEST_REGEXP:
                toAdd = new RegexpValidator(testErrorString, customRegexp);
                break;

            case TEST_EMAIL:
                toAdd = new EmailValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_email_address_not_valid)
                                : testErrorString);
                break;

            case TEST_PHONE:
                toAdd = new PhoneValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_phone_not_valid) : testErrorString);
                break;
            case TEST_WEBURL:
                toAdd = new WebUrlValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_url_not_valid) : testErrorString);
                break;

            case TEST_DATE:
                toAdd = new DateValidator(TextUtils.isEmpty(testErrorString) ? context.getString(R.string.error_date_not_valid) : testErrorString, customFormat);
                break;
        }

        MultiValidator tmpValidator;

        if (!emptyAllowed) { // If the xml tells us that this is a required field, we will add the EmptyValidator.
            tmpValidator = new AndValidator();
            tmpValidator.enqueue(new EmptyValidator(emptyErrorStringActual));
            tmpValidator.enqueue(toAdd);
        } else {
            tmpValidator = new OrValidator(toAdd.getErrorMessage(), new NotValidator(null, new EmptyValidator(null)), toAdd);
        }

        addValidator(tmpValidator);
    }

    @Override
    public boolean testValidity()
    {
        return testValidity(true);
    }

    @Override
    public boolean testValidity(boolean showUIError) {

        boolean isValid = mValidator.isValid(editText);

        if (!isValid && showUIError) {
            showUIError();
        }

        return isValid;
    }

    @Override
    public void showUIError() {
        if (mValidator.hasErrorMessage()) {
            editText.setError( mValidator.getErrorMessage() );
        }
    }

    public void setEmptyErrorString(String emptyErrorString) {
        if (!TextUtils.isEmpty(emptyErrorString)) {
            emptyErrorStringActual = emptyErrorString;
        } else {
            emptyErrorStringActual = defaultEmptyErrorString;
        }
    }

    public void setEditText(EditText editText) {
        if (this.editText != null) {
            this.editText.removeTextChangedListener(getTextWatcher());
        }

        this.editText = editText;
        editText.addTextChangedListener(getTextWatcher());
    }

}
