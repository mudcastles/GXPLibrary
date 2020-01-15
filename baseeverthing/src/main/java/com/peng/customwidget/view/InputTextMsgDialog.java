package com.peng.customwidget.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.peng.baseeverthing.R;

import java.util.Objects;

/**
 * 输入框弹窗
 * 用于聊天界面的输入弹窗
 */
public class InputTextMsgDialog extends Dialog {

    /**
     * 点击发送按钮监听
     */
    public interface OnTextSendListener {
        /**
         * @param msg editText的内容
         */
        void onTextSend(String msg);
    }

    /**
     * 布局完成监听
     */
    public interface OnLayoutCompleteListener {
        /**
         * @param mSoftKeyboardHeight 软键盘高度
         * @param mLayoutHeight       dialog布局高度
         */
        void onLayoutComplete(int mSoftKeyboardHeight, int mLayoutHeight);
    }

    private ImageView confirmBtn;
    private EditText messageTextView;
    private Context mContext;
    private InputMethodManager imm;
    private RelativeLayout rlDlg;
    //上一次记录的尺寸，当软键盘隐藏时该值应该 > 0，
    // 计算到的（屏幕高度 - 可视区域高度）值应为0，此时应dismiss掉该dialog
    private int mLastDiff = 0;
    private OnTextSendListener mOnTextSendListener;
    private OnLayoutCompleteListener mOnLayoutCompleteListener;

    //软键盘的高度
    private int mSoftKeyboardHeight = 0;
    //dialog布局的高度
    private int mLayoutHeight = 0;

    public int getmSoftKeyboardHeight() {
        return mSoftKeyboardHeight;
    }

    public int getmLayoutHeight() {
        return mLayoutHeight;
    }

    public InputTextMsgDialog(@NonNull final Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.dialog_input_text);

        messageTextView = (EditText) findViewById(R.id.et_input_message);
        messageTextView.setInputType(InputType.TYPE_CLASS_TEXT);
        //修改下划线颜色
//        messageTextView.getBackground().setColorFilter(context.getResources().getColor(android.R.color.transparent), PorterDuff.Mode.CLEAR);
//        messageTextView.getBackground().setColorFilter(context.getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SCREEN);

        confirmBtn = (ImageView) findViewById(R.id.confirm_btn);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageTextView.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    mOnTextSendListener.onTextSend(msg);
                    imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    messageTextView.setText("");
                    dismiss();
                } else {
                    Toast.makeText(mContext, "评论内容不能为空", Toast.LENGTH_LONG).show();
                }
                messageTextView.setText(null);
            }
        });

        messageTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e("onEditorAction", actionId + "");
                switch (actionId) {
                    case KeyEvent.KEYCODE_ENDCALL:
                    case KeyEvent.KEYCODE_ENTER:
                        if (messageTextView.getText().length() > 0) {
                            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "评论内容不能为空", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case KeyEvent.KEYCODE_BACK:
                        dismiss();
                        return false;
                    default:
                        return false;
                }
            }
        });

        messageTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("My test", "onKey " + keyEvent.getCharacters());
                return false;
            }
        });

        rlDlg = findViewById(R.id.rl_outside_view);
        rlDlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() != R.id.rl_inputdlg_view)
                    dismiss();
            }
        });

        final LinearLayout rldlgview = (LinearLayout) findViewById(R.id.rl_inputdlg_view);

        rldlgview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Rect r = new Rect();
                //获取当前界面可视部分
                Objects.requireNonNull(getWindow()).getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度,注意，不可以用getWindow().getDecorView().getRootView().getHeight()获取屏幕高度，因为这个值会变化
//                int screenHeight = getWindow().getDecorView().getRootView().getHeight();
                int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;

                Log.e("OnLayoutChangeListener", "screenHeight" + screenHeight + "  r.bottom=" + r.bottom + "  heightDifference=" + heightDifference);
                if (heightDifference <= 0 && mLastDiff > 0) {
                    //imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    dismiss();
                }
                mLastDiff = heightDifference;

                //计算软键盘的高度
                if (mLastDiff > mSoftKeyboardHeight) mSoftKeyboardHeight = mLastDiff;
                //计算布局的高度
                if (mLayoutHeight <= 0) mLayoutHeight = view.getHeight();

                if (mSoftKeyboardHeight > 0 && mLayoutHeight > 0)
                    if (mOnLayoutCompleteListener != null)
                        mOnLayoutCompleteListener.onLayoutComplete(mSoftKeyboardHeight, mLayoutHeight);
            }
        });
        rldlgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                dismiss();
            }
        });
    }

    public void setmOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.mOnTextSendListener = onTextSendListener;
    }

    public void setmOnLayoutCompleteListener(OnLayoutCompleteListener mOnLayoutCompleteListener) {
        this.mOnLayoutCompleteListener = mOnLayoutCompleteListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0;
    }

    @Override
    public void show() {
        super.show();
        if(messageTextView!=null){
            messageTextView.requestFocus();
        }
    }
}
