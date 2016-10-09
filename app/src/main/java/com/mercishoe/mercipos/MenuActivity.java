package com.mercishoe.mercipos;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button btnSale = (Button) findViewById(R.id.sale_button);
        btnSale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SaleActivity.class);
                startActivity(i);
            }
        });
        Button btnCon = (Button) findViewById(R.id.conclude_button);
        btnCon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SummaryActivity.class);
                startActivity(i);
            }
        });
    }


}
