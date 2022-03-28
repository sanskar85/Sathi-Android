package com.abbvmk.sathi.Views.OTP_InputBox;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.abbvmk.sathi.R;

public class OTP_InputBox extends ConstraintLayout {

    EditText[] et = new EditText[4];

    public OTP_InputBox(@NonNull Context context) {
        super(context);
        init(context);
    }

    public OTP_InputBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_otp_input_box,this,true);
        et[0] = view.findViewById((R.id.otp_1));
        et[1] = view.findViewById((R.id.otp_2));
        et[2] = view.findViewById((R.id.otp_3));
        et[3] = view.findViewById((R.id.otp_4));
        for (int i = 0; i < et.length; i++) {
            et[i].addTextChangedListener(createTextWatcher(i));
        }
    }

    private TextWatcher createTextWatcher(int index){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==1){
                    if(index != et.length-1) {
                        et[index+1].requestFocus();
                    }
                }
            }
        };
    }

    public String getOTP() {
        StringBuilder sb = new StringBuilder();
        for(EditText e:et){
            sb.append(e.getText());
        }
        return sb.toString();
    }

    public int size() {
        return et.length;
    }
}
