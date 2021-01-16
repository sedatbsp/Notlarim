package com.sdtbsp.notdefterim.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;
import com.sdtbsp.notdefterim.R;


public class EnterPasswordActivity extends AppCompatActivity {

    private EditText editText1;
    private LinearLayout linearLayoutButton;
    private String password;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }



        setContentView(R.layout.activity_enter_password);

        constraintLayout = findViewById(R.id.enterPasswordLayout);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = getCurrentFocus();
                if(view!=null){
                    InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
            }
        });

        SharedPreferences settings = getSharedPreferences("PREFS",0);
        password = settings.getString("password","");

        editText1 = findViewById(R.id.inputPassword1);
        linearLayoutButton = findViewById(R.id.layoutSearch3);

        linearLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText1.getText().toString();
                if(text.equals(password)){
                    Intent intent = new Intent(EnterPasswordActivity.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                    finish();
                }else if(text.trim().isEmpty()){
                    //Toast.makeText(EnterPasswordActivity.this, R.string.sifrenizi_girmediniz, Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(EnterPasswordActivity.this,"Şifrenizi girmediniz.", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else {
                    //Toast.makeText(EnterPasswordActivity.this, R.string.hatali_sifre_girdiniz, Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(EnterPasswordActivity.this,"Hata! Şifreniz yanlış.", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        finish();
    }
}