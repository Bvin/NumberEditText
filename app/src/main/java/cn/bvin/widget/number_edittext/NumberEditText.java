/*
 * Copyright 2015 bvin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bvin.widget.number_edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.RoundingMode;

/**
 * 数值控件，可通过增减微调数值，也可手动输入..
 */
public class NumberEditText extends LinearLayout implements TextWatcher, View.OnFocusChangeListener, View.OnTouchListener, TextView.OnEditorActionListener {

    private static final long DEFAULT_INPUT_TIMEOUT = 2000;
    private static final long DEFAULT_QUICK_SPEED = 150;

    private String mHint;
    private String mText;
    private int mTextColor;
    private NumberConvertor mNumberConvertor;
    private double mValue = -1;
    private boolean mEmptyValue = true;
    private double mMaxValue;
    private double mMinValue;
    private final EditText etInput;
    private final Button btZoomIn;
    private final Button btZoomOut;

    private boolean mSelfChange;

    private Zoomable mZoom;
    private ZoomListener mZoomListener;
    private OnValueChangeListener mValueChangeListener;
    private OnValueReachRangeListener mValueReachRangeListener;

    private LoopTrigger mTouchEventTriggerLooper;//quick increase/decrease
    private Runnable mUpdateTextRunnable;
    private long mInputTimeout;
    private long mSpeedOfQuickControl;//quick increase/decrease 's speed

    public NumberEditText(Context context) {
        this(context, null);
    }

    public NumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.edit_text_controller, this);
        parseAttributeSet(context, attrs);
        btZoomIn = (Button) findViewById(R.id.zoom_in);
        btZoomOut = (Button) findViewById(R.id.zoom_out);
        etInput = (EditText) findViewById(R.id.edit_text);
        mZoom = new Zoomer() {//默认针对整数位增减1
            @Override
            public int scale() {
                return 0;//返回0则表示针对整数位增减1
            }
        };
        mMaxValue = Integer.MAX_VALUE;
        mMinValue = Integer.MIN_VALUE;
        mInputTimeout = DEFAULT_INPUT_TIMEOUT;
        mSpeedOfQuickControl = DEFAULT_QUICK_SPEED;
        mUpdateTextRunnable = new Runnable() {
            @Override
            public void run() {
                onValueConfirm(mValue);
            }
        };
        setupViews();
    }

    private void parseAttributeSet(Context context, AttributeSet set) {
        int[] attrs = new int[]{android.R.attr.inputType, android.R.attr.text
                , android.R.attr.textColor, android.R.attr.hint};
        TypedArray typedArray = context.obtainStyledAttributes(set, attrs);
        for (int i = 0; i < attrs.length; i++) {
            switch (attrs[i]) {
                case android.R.attr.inputType:
                    //SimpleLogger.log_e("parseAttributeSet",attrs[i]);
                    //mInputType = typedArray.getInt(i, EditorInfo.TYPE_NULL);
                    break;
                case android.R.attr.hint:
                    //SimpleLogger.log_e("parseAttributeSet",attrs[i]);
                    mHint = typedArray.getString(i);
                    break;
                case android.R.attr.text:
                    mText = typedArray.getString(i);
                    break;
                case android.R.attr.textColor:
                    //SimpleLogger.log_e("parseAttributeSet",attrs[i]);
                    mTextColor = typedArray.getColor(i, 0);
                    break;
            }

        }
        typedArray.recycle();
    }

    private void setupViews() {
        if (!TextUtils.isEmpty(mText))
            etInput.setText(mText);
        if (mTextColor != 0)
            etInput.setTextColor(mTextColor);
        if (!TextUtils.isEmpty(mHint))
            etInput.setHint(mHint);
        etInput.addTextChangedListener(this);
        etInput.setOnFocusChangeListener(this);
        etInput.setOnEditorActionListener(this);
        btZoomIn.setOnTouchListener(this);
        btZoomOut.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchEventTriggerLooper = new LoopTrigger(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    onTouchEventTrigger(v);
                }
            });
            mTouchEventTriggerLooper.setDelayMilliseconds(mSpeedOfQuickControl);
            mTouchEventTriggerLooper.setRunFlag(true);
            mTouchEventTriggerLooper.start();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mTouchEventTriggerLooper.setRunFlag(false);
            mTouchEventTriggerLooper.interrupt();
            mTouchEventTriggerLooper = null;
        }
        return false;
    }

    protected void onTouchEventTrigger(View v) {
        switch (v.getId()) {
            case R.id.zoom_in:
                setValue(checkValueReachRange(mZoom.increase(mValue)));
                requestUpdateValueText(mValue, 0);//立即更新
                if (mZoomListener != null) {
                    mZoomListener.onValueIncreased(mValue);
                }
                break;
            case R.id.zoom_out:
                setValue(checkValueReachRange(mZoom.decrease(mValue)));
                requestUpdateValueText(mValue, 0);//立即更新
                if (mZoomListener != null) {
                    mZoomListener.onValueDecreased(mValue);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mSelfChange) return;
        if (TextUtils.isEmpty(s.toString())) {
            mEmptyValue = true;
            return;
        }
        mEmptyValue = false;
        double value = 0;
        try {
            value = Double.valueOf(s.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        setValue(checkValueReachRange(value));
        requestUpdateValueText(value, mInputTimeout);
    }

    /**
     * 请求更新输入框值.
     * @param value 值
     * @param delayMs 延迟更新界面毫秒
     */
    private void requestUpdateValueText(double value, long delayMs) {
        removeCallbacks(mUpdateTextRunnable);//若频繁请求，会取消之前的请求，仅以最后一次为准
        postDelayed(mUpdateTextRunnable, delayMs);
    }

    /**
     * 确认值输入完毕触发.
     * @param value 输入值
     */
    private void onValueConfirm(double value) {
        if (isEmptyValue()) return;//如果是延迟了
        updateValueText(value);
    }

    /**
     * 检验输入值有没有触达限制范围，并不会主动校验当前值，只负责检验.
     * @param inputValue 输入值
     * @return 返回校验后的参考值
     */
    private double checkValueReachRange(double inputValue) {
        double tmp = inputValue;
        if (mMaxValue != Integer.MAX_VALUE) {//有设置最大值
            if (inputValue > mMaxValue) {
                tmp = mMaxValue;
                if (mValueReachRangeListener != null) {//触发超出范围回掉
                    mValueReachRangeListener.onValueReachMax(inputValue, mMaxValue);
                }
            }
        }
        if (mMinValue != Integer.MIN_VALUE) {//有设置最小值
            if (tmp < mMinValue) {
                tmp = mMinValue;
                if (mValueReachRangeListener != null) {//触发超出范围回掉
                    mValueReachRangeListener.onValueReachMin(inputValue, mMinValue);
                }
            }
        }
        return tmp;
    }

    public void setZoom(Zoomable zoom) {
        mZoom = zoom;
    }

    public void setZoomListener(ZoomListener zoomListener) {
        mZoomListener = zoomListener;
    }

    public void setOnValueChangeListener(OnValueChangeListener l) {
        mValueChangeListener = l;
    }

    public void setOnValueReachRangeListener(OnValueReachRangeListener l) {
        mValueReachRangeListener = l;
    }

    public void setNumberConvertor(NumberConvertor numberConvertor) {
        mNumberConvertor = numberConvertor;
    }

    public void setMaxValue(double maxValue) {
        if (mMaxValue == maxValue) return;
        mMaxValue = maxValue;
        if (isEmptyValue()) return;//如果还没有设置值就不需要去矫正
        if (mValue != checkValueReachRange(mValue)) setCurrentValue(mValue);
    }

    public void setMinValue(double minValue) {
        if (mMinValue == minValue) return;
        mMinValue = minValue;
        if (isEmptyValue()) return;//如果还没有设置值就不需要去矫正
        if (mValue != checkValueReachRange(mValue)) setCurrentValue(mValue);
    }

    /**
     * 设置当前值(立即更新UI)
     *
     * @param value
     */
    public void setCurrentValue(double value) {
        //外部调用
        double rightValue = checkValueReachRange(value);//检验值和范围
        setValue(rightValue);//赋值并触发值变换回掉
        requestUpdateValueText(rightValue, 0);//立即更新值文本
    }

    /**
     * 真正更新值文本操作.
     * @param value 值
     */
    private void updateValueText(double value) {
        String text;
        if (mNumberConvertor != null) {
            text = mNumberConvertor.convert(value);
        } else {
            text = String.valueOf(value);
        }
        mSelfChange = true;
        etInput.setText(text);
        etInput.setSelection(text.length());
        mSelfChange = false;
    }

    /**
     * 给当前值赋值(与之前的值不一样将会触发值变换回掉)
     *
     * @param value 值(如要处理需先处理好再设置)
     */
    private void setValue(double value) {
        if (mValue == value) return;
        mValue = value;
        mEmptyValue = false;
        if (mValueChangeListener != null) {
            mValueChangeListener.onValueChanged(mValue);
        }
    }

    public boolean isEmptyValue() {
        return mEmptyValue;
    }

    public double getCurrentValue() {
        return mValue;
    }

    public void setInputFilter(InputFilter filter) {
        //若要追加再自行扩展
        etInput.setFilters(new InputFilter[]{filter});
    }

    public void requestFocusOnInput(){
        etInput.requestFocus();
        etInput.requestFocusFromTouch();
    }

    public void setGravity(int gravity) {
        etInput.setGravity(gravity);
    }

    public void setHint(String hint) {
        mHint = hint;
        etInput.setHint(mHint);
    }

    /**
     * 设置输入超时，用户输入超时后将自动帮助用户确认当前输入值，随之会校验当前值是否在接受范围内.
     * @param inputTimeout input value change duration in millisecond
     */
    public void setInputTimeout(long inputTimeout) {
        mInputTimeout = inputTimeout;
    }

    /**
     * 设置快速增减的速度.
     * @param speedOfQuickControl 毫秒值
     */
    public void setSpeedOfQuickControl(long speedOfQuickControl) {
        mSpeedOfQuickControl = speedOfQuickControl;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            etInput.setHint("");
        } else {
            etInput.setHint(mHint);
            requestUpdateValueText(mValue, 0);//若正在编辑时失去焦点，将确认当前输入值
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            requestUpdateValueText(mValue, 0);//若用户在输入法上按下确认/完成键，将确认当前输入值
            return true;
        }
        return false;
    }

    class LoopTrigger extends Thread {

        private long delayMs = 250;//默认250
        private boolean runFlag = true;
        private Handler handler;

        public LoopTrigger(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (runFlag) {
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setRunFlag(boolean runFlag) {
            this.runFlag = runFlag;
        }

        public void setDelayMilliseconds(long delayMs) {
            this.delayMs = delayMs;
        }
    }

    public interface ZoomListener {
        void onValueIncreased(double value);
        void onValueDecreased(double value);
    }

    public interface OnValueChangeListener {
        void onValueChanged(double value);
    }

    public interface OnValueReachRangeListener{
        void onValueReachMax(double input,double max);
        void onValueReachMin(double input,double min);
    }

    public interface Zoomable {
        double increase(double origin);//increase
        double decrease(double origin);//decrease
    }

    /**
     * 默认以BigDecimal实现增减功能，只需给定增减幅度{@link #scale()}即可.
     */
    public static abstract class Zoomer implements Zoomable {

        @Override
        public double increase(double origin) {
            return BigDecimalUtil.add(origin, doPower()).doubleValue();
        }

        @Override
        public double decrease(double origin) {
            return BigDecimalUtil.subtraction(origin, doPower()).doubleValue();
        }

        //做幂运算,一个点 = 1/10^N(N表示几位小数).
        private double doPower() {
            double pow = Math.pow(10, scale());
            return BigDecimalUtil.divide((double) 1, pow, scale(), RoundingMode.HALF_EVEN).doubleValue();
        }

        /**
         * 获取精度(或理解为保留几位小数).
         * <p><i>PS：0表示加减1，1表示加减0.1，2表示加减0.01</i></p>
         *
         * @return
         */
        public abstract int scale();
    }

}
