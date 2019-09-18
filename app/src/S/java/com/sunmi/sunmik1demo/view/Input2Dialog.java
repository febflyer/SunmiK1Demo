package com.sunmi.sunmik1demo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.widget.dialog.InputDialog;

public class Input2Dialog extends Dialog implements View.OnClickListener {

    private TextView mTitleTextView;
    private ImageView mDeleteImageView;
    private ImageView mEyeImageView;
    private EditText mEditText;
    private TextView mCancelTextView;
    private TextView mSureTextView;
    private RelativeLayout mInputLayout;

    private Context mContext;
    private InputDialog.DialogOnClickCallback mCallback;
    private boolean isHide = false;


    public Input2Dialog(@NonNull Context context) {
        super(context, com.sunmi.widget.R.style.defaultDialogStyle);
        mContext = context;
        init();
    }

    public Input2Dialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }

    private void init() {
        setContentView(com.sunmi.widget.R.layout.dialog_input);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setCanceledOnTouchOutside(false);
        mTitleTextView = findViewById(com.sunmi.widget.R.id.tv_title);
        mDeleteImageView = findViewById(com.sunmi.widget.R.id.iv_delete);
        mEyeImageView = findViewById(com.sunmi.widget.R.id.iv_eye);
        mEditText = findViewById(com.sunmi.widget.R.id.et_input);
        mCancelTextView = findViewById(com.sunmi.widget.R.id.tv_cancel);
        mSureTextView = findViewById(com.sunmi.widget.R.id.tv_sure);
        mInputLayout = findViewById(com.sunmi.widget.R.id.rl_input);

        mDeleteImageView.setOnClickListener(this);
        mEyeImageView.setOnClickListener(this);
        mCancelTextView.setOnClickListener(this);
        mSureTextView.setOnClickListener(this);

        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 0) {
//                    mInputLayout.setBackground(mContext.getResources().getDrawable(com.sunmi.widget.R.drawable.bg_input_unfocused));
//                    mEditText.setLetterSpacing(0.2f);
//                } else {
//                    mEditText.setLetterSpacing(0);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mEyeImageView.setImageDrawable(mContext.getResources().getDrawable(isHide ? com.sunmi.widget.R.drawable.eye_gray : com.sunmi.widget.R.drawable.eye_yellow));
        mEyeImageView.setVisibility(View.INVISIBLE);
        mDeleteImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == com.sunmi.widget.R.id.iv_delete) {
            mEditText.setText("");
        } else if (v.getId() == com.sunmi.widget.R.id.iv_eye) {
            isHide = !isHide;
            mEditText.setInputType(isHide ? EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                    : EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEyeImageView.setImageDrawable(mContext.getResources().getDrawable(isHide ? com.sunmi.widget.R.drawable.eye_gray : com.sunmi.widget.R.drawable.eye_yellow));
            mEditText.setSelection(mEditText.length());
        } else if (v.getId() == com.sunmi.widget.R.id.tv_cancel) {
            if (mCallback != null) {
                mCallback.onCancel();
            }
            dismiss();
        } else if (v.getId() == com.sunmi.widget.R.id.tv_sure) {
            if (mCallback != null) {
                mCallback.onSure(mEditText.getText().toString());
            }
        }
    }

    /**
     * 如果输入有误时调用改方法
     */
    public void inputError() {
        mInputLayout.setBackground(mContext.getResources().getDrawable(com.sunmi.widget.R.drawable.bg_input_focused));
    }

    /**
     * 设置标题
     *
     * @param text
     */
    public void setTitle(String text) {
        mTitleTextView.setText(text);
    }

    /**
     * 设置输入框默认内容
     *
     * @param text
     */
    public void setHiht(String text) {
        mEditText.setHint(text);
    }

    /**
     * 设置左边按钮内容
     *
     * @param text
     */
    public void setLeftText(String text) {
        mCancelTextView.setText(text);
    }

    /**
     * 设置右边按钮内容
     *
     * @param text
     */
    public void setRightText(String text) {
        mSureTextView.setText(text);
    }

    /**
     * 设置按钮监听
     *
     * @param mCallback
     */
    public void setCallback(InputDialog.DialogOnClickCallback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     * 设置弹窗弹出/隐藏动画效果
     *
     * @param resId
     */
    private void setWindowAnimations(int resId) {
        if (getWindow() != null) {
            getWindow().setWindowAnimations(com.sunmi.widget.R.style.DialogAnimation);
        }
    }

    /**
     * 弹框建造类
     */
    public static class Builder {

        private Input2Dialog dialog;

        public Builder(Context context) {
            this.dialog = new Input2Dialog(context);
        }

        public Builder(Context context, int themeResId) {
            this.dialog = new Input2Dialog(context, themeResId);
        }

        public Input2Dialog.Builder setLeftText(String text) {
            dialog.setLeftText(text);
            return this;
        }

        public Input2Dialog.Builder setRightText(String text) {
            dialog.setRightText(text);
            return this;
        }

        public Input2Dialog.Builder setTitle(String text) {
            dialog.setTitle(text);
            return this;
        }

        public Input2Dialog.Builder setHint(String text) {
            dialog.setHiht(text);
            return this;
        }

        public Input2Dialog.Builder setCallBack(InputDialog.DialogOnClickCallback callBack) {
            dialog.setCallback(callBack);
            return this;
        }

        public Input2Dialog.Builder setWindowAnimations(int resId) {
            dialog.setWindowAnimations(resId);
            return this;
        }

        public Input2Dialog build() {
            return dialog;
        }
    }

    public interface DialogOnClickCallback {
        void onSure(String text);

        void onCancel();
    }
}