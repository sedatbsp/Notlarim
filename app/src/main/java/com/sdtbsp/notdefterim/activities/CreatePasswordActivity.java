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

public class CreatePasswordActivity extends AppCompatActivity {

    private EditText editText1,editText2;
    private LinearLayout linearLayoutButton;
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
        setContentView(R.layout.activity_create_password);


        constraintLayout = findViewById(R.id.createPasswordLayout);
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


        editText1 = findViewById(R.id.inputPassword1);
        editText2 = findViewById(R.id.inputPassword2);
        linearLayoutButton = findViewById(R.id.layoutSearch3);

        linearLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text1=editText1.getText().toString();
                String text2=editText2.getText().toString();

                if(text1.equals("") || text2.equals("")){
                    //Toast.makeText(CreatePasswordActivity.this, R.string.sifrenizi_girmediniz, Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(CreatePasswordActivity.this,"Şifrenizi girmediniz.", Toast.LENGTH_LONG, R.style.mytoast).show();
                }else {
                    if(text1.equals(text2)){
                        SharedPreferences settings = getSharedPreferences("PREFS",0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("password",text1);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        //Toast.makeText(CreatePasswordActivity.this, R.string.sifreler_uyusmuyor, Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(CreatePasswordActivity.this,"Hata! Şifreler uyuşmuyor.", Toast.LENGTH_LONG, R.style.mytoast).show();
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }


}