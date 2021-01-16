package com.sdtbsp.notdefterim.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;
import com.sdtbsp.notdefterim.R;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText editText1,editText2;
    private LinearLayout linearLayoutButton;
    private SharedPreferences settings = null;
    private SharedPreferences.Editor editor;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        constraintLayout = findViewById(R.id.updatePasswordLayout);
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
        linearLayoutButton = findViewById(R.id.layoutSaveButton);


        settings = getSharedPreferences("PREFS",0);
        editor = settings.edit();


        linearLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword=editText1.getText().toString();
                String newPassword=editText2.getText().toString();

                if(oldPassword.equals("") || newPassword.equals("")){
                    //Toast.makeText(UpdatePasswordActivity.this, R.string.sifrenizi_girmediniz, Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(UpdatePasswordActivity.this,"Şifrenizi girmediniz.", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
                else if(oldPassword.equals(settings.getString("password",""))){
                    editor.putString("password",newPassword);
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else {
                    //Toast.makeText(UpdatePasswordActivity.this, R.string.sifreler_uyusmuyor, Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(UpdatePasswordActivity.this,"Hata! Eski şifreniz doğru değil", Toast.LENGTH_LONG, R.style.mytoast).show();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}