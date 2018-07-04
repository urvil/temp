package com.reveautomation.revesmartsecuritykit_online.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reveautomation.revesmartsecuritykit_online.R;


public class DialogOk extends Dialog {

    public IOkDialog listenerOkDialog;
    private String msg;
    private String title;
    private Context context;
    private boolean autoDismiss;

    public DialogOk(Context context, String title, String msg, IOkDialog listenerOkDialog) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.listenerOkDialog = listenerOkDialog;
        this.msg = msg;
        this.title = title;
        this.context = context;
        autoDismiss = false;
    }

    public DialogOk(Context context, String title, String msg) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.msg = msg;
        this.title = title;
        this.context = context;
        autoDismiss = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.dialog_ok);

        LinearLayout llHeader = (LinearLayout) findViewById(R.id.llHeader);
        TextView tvHeader = (TextView) findViewById(R.id.tvHeader);
        TextView tvOk = (TextView) findViewById(R.id.tvOk);
        TextView tvMessage = (TextView) findViewById(R.id.tvMessage);

        if (title != null) {
            llHeader.setVisibility(View.VISIBLE);
            tvHeader.setText(title);
        } else
            llHeader.setVisibility(View.GONE);

        tvMessage.setText(msg);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoDismiss) {
                    dismiss();
                } else {
                    if (listenerOkDialog != null) {
                        listenerOkDialog.onOkClick();
                    }
                }
            }
        });

    }

    public interface IOkDialog {
        void onOkClick();
    }


}
