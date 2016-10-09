package com.mercishoe.mercipos;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MainActivity extends Activity{
    private static String[] user_name_list,user_password_list;
    public final String P_NAME = "POS_DATA",U_ID = "UserID",
            U_PWD="UserPWD",U_BRN="UserBranch",S_ITEM="SelectedItem";
    Button btnLogin;
    EditText txtIdName,txtPWD,txtBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.login_button);
        txtIdName = (EditText) findViewById(R.id.login_name);
        txtPWD = (EditText) findViewById(R.id.password_name);
        txtBranch = (EditText) findViewById(R.id.branch_name);


        SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        txtIdName.setText(sp.getString(U_ID,""));
        txtPWD.setText(sp.getString(U_PWD,""));
        txtBranch.setText(sp.getString(U_BRN,""));

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtIdName.getText().length()!=0 & txtPWD.getText().length()!=0 & txtBranch.getText().length()!=0 ){
                    if (checkUserID(txtIdName.getText().toString(),txtPWD.getText().toString())){
                        Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"รหัสไม่ถูกนะจ้ะ",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"กรอกข้อมูลให้ครบนะจ้ะ",Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtIdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(U_ID,txtIdName.getText().toString());
                editor.commit();
            }
        });
        txtPWD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(U_PWD,txtPWD.getText().toString());
                editor.commit();
            }
        });
        txtBranch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(U_BRN,txtBranch.getText().toString());
                editor.commit();
            }
        });

    }
    public Boolean checkUserID(String username,String userpassword){
        String[][] user_name_list = {
                {"MON","1234"},{"POO","4567"},{"PUI","1111"},{"PORN","2345"},{"PREAW","3456"},
                {"ADMIN","1234"},{"UNG","4567"},{"MITRA","5678"},{"MERCI","8833"}
        };
        for(int i = 0; i < user_name_list.length;i++){
            if(user_name_list[i][0].equals(username)&user_name_list[i][1].equals(userpassword)){
                return true;
            }
        }
        return false;

    }




}
