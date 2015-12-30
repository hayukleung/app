package com.hayukleung.app.widget.qrcode.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hayukleung.app.widget.qrcode.R;

public class CustomDialog extends Dialog {

    /**
     * 监听
     * 
     * @author Hayuk
     * 
     */
    public interface CustomDialogInterface {

        /**
         * 执行
         * 
         * @return 确认点击
         */
        public boolean onClick();
    }

    private Context mContext;

    /** 肯定 */
    private CustomDialogInterface mInterfacePositive;

    /** 否定 */
    private CustomDialogInterface mInterfaceNegative;
    
    /** 单选 */
    private CustomDialogInterface mInterfaceUnique;

    private TextView mTxtTitle;
    private LinearLayout mLayoutContent;
    private TextView mTxtContent;
    /** 双选 */
    private LinearLayout mLayoutJudge;
    private Button mBtnPositive;
    private Button mBtnNegative;
    /** 单选 */
    private LinearLayout mLayoutUnique;
    private Button mBtnUnique;

    public CustomDialog(Context context, boolean top) {
        super(context, R.style.custom_dialog);
        init(context, top);
    }
    
    public CustomDialog(Context context) {
        super(context, R.style.custom_dialog);
        init(context, false);
    }
    
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }
    
    private void init(Context context, boolean top) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_custom_dialog);
        setCanceledOnTouchOutside(true);
        this.mContext = context;
        this.mTxtTitle = (TextView) findViewById(R.id.LayoutCustomDialog$txt_title);
        this.mLayoutContent = (LinearLayout) findViewById(R.id.LayoutCustomDialog$ll_vertical);
        this.mTxtContent = (TextView) findViewById(R.id.LayoutCustomDialog$txt_content);
        this.mLayoutJudge = (LinearLayout) findViewById(R.id.LayoutCustomDialog$ll_bottom);
        this.mBtnPositive = (Button) findViewById(R.id.LayoutCustomDialog$btn_positive);
        this.mBtnNegative = (Button) findViewById(R.id.LayoutCustomDialog$btn_negative);
        this.mLayoutUnique = (LinearLayout) findViewById(R.id.LayoutCustomDialog$ll_bottom_unique_choice);
        this.mBtnUnique = (Button) findViewById(R.id.LayoutCustomDialog$btn_unique_choice);
        
        setUniqueLayoutUsable(false);
        
        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        if (top) {
            window.setGravity(Gravity.TOP);
            params.y = (int) ((float) mContext.getResources().getDisplayMetrics().heightPixels / 10);
        }
        params.width = (int) ((float) mContext.getResources().getDisplayMetrics().widthPixels / 10 * 8);
        window.setAttributes(params);
    }

    /**
     * 设置标题
     * 
     * @param title
     */
    public void setCustomTitle(String title) {
        this.mTxtTitle.setText(title);
        this.mTxtTitle.setVisibility(View.VISIBLE);
    }
    
    /**
     * 设置内容
     * 
     * @param content
     */
    public void setCustomContent(String content) {
        this.mTxtContent.setText(Html.fromHtml(content));
        this.mTxtContent.setVisibility(View.VISIBLE);
    }

    /**
     * 设置内容（垂直布局）
     * 
     * @param view
     */
    public void setViewVertical(View view) {
        this.mLayoutContent.setVisibility(View.VISIBLE);
        this.mLayoutContent.removeAllViews();
        View lineTop = new View(mContext);
        lineTop.setBackgroundColor(0xAADDDDDD);
        this.mLayoutContent.addView(lineTop, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        this.mLayoutContent.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, /*ImageUtil.dp2px(this.mContext, 50)*/ LayoutParams.WRAP_CONTENT));
        View lineBottom = new View(mContext);
        lineBottom.setBackgroundColor(0xAADDDDDD);
        this.mLayoutContent.addView(lineBottom, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
    }

    /**
     * 标题是否可用
     * 
     * @param usable
     */
    public void setTitleUsable(boolean usable) {
        this.mTxtTitle.setVisibility(usable ? View.VISIBLE : View.GONE);
    }
    
    /**
     * 布尔按钮是否可用
     * 
     * @param usable
     */
    public void setJudgeLayoutUsable(boolean usable) {
        this.mLayoutJudge.setVisibility(usable ? View.VISIBLE : View.GONE);
        if (usable) {
            setUniqueLayoutUsable(false);
        }
    }
    
    /**
     * 设置确定按钮
     * 
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text, CustomDialogInterface listener) {
        this.mInterfacePositive = listener;
        this.mLayoutContent.setVisibility(View.VISIBLE);
        this.mBtnPositive.setVisibility(View.VISIBLE);
        this.mBtnPositive.setText(text);
        this.mBtnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mInterfacePositive) {
                    if (mInterfacePositive.onClick()) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        });
    }

    /**
     * 设置取消按钮
     * 
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text, CustomDialogInterface listener) {
        this.mInterfaceNegative = listener;
        this.mLayoutContent.setVisibility(View.VISIBLE);
        this.mBtnNegative.setVisibility(View.VISIBLE);
        this.mBtnNegative.setText(text);
        this.mBtnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mInterfaceNegative) {
                    mInterfaceNegative.onClick();
                }
                dismiss();
            }
        });
    }
    
    /**
     * 单选按钮是否可用
     * 
     * @param usable
     */
    public void setUniqueLayoutUsable(boolean usable) {
        this.mLayoutUnique.setVisibility(usable ? View.VISIBLE : View.GONE);
        if (usable) {
            setJudgeLayoutUsable(false);
        }
    }
    
    /**
     * 设置单选按钮
     * 
     * @param text
     * @param listener
     */
    public void setUniqueButton(String text, CustomDialogInterface listener) {
        this.mInterfaceUnique = listener;
        this.mLayoutContent.setVisibility(View.VISIBLE);
        this.mBtnUnique.setVisibility(View.VISIBLE);
        this.mBtnUnique.setText(text);
        this.mBtnUnique.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mInterfaceUnique) {
                    mInterfaceUnique.onClick();
                }
                dismiss();
            }
        });
    }

}
