package com.mercishoe.mercipos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class AddItemActivity extends Activity {
    SQLiteDatabase mDb;
    Database mHelper;
    Cursor mCursor;
    String searchedModel;
    EditText txtModel, txtBarcode;
    TextView tvProduct,tvModel, tvColor, tvSize, tvPrice,tvItem;
    RadioButton radSale,radReturn;
    Button btnScan, btnSearch, btnOk, btnPlus, btnMinus;
    public final String P_NAME = "POSdata";
    String selected,product,model,color,price,size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        txtModel = (EditText) findViewById(R.id.modal_number_text);
        txtBarcode = (EditText) findViewById(R.id.barcode_text);
        btnScan = (Button) findViewById(R.id.barcode_scanner_button);
        btnSearch = (Button) findViewById(R.id.search_item_button);
        btnOk = (Button) findViewById(R.id.add_item_ok_button);
        btnPlus = (Button) findViewById(R.id.plus_button);
        btnMinus = (Button) findViewById(R.id.minus_button);
        radSale = (RadioButton) findViewById(R.id.sale_radio);
        radReturn = (RadioButton) findViewById(R.id.return_radio);
        tvProduct = (TextView) findViewById(R.id.product_type_label);
        tvModel = (TextView) findViewById(R.id.modal_number_label);
        tvColor = (TextView) findViewById(R.id.modal_color_label);
        tvSize = (TextView) findViewById(R.id.modal_size_label);
        tvPrice = (TextView) findViewById(R.id.modal_price_label);
        tvItem = (TextView)findViewById(R.id.modal_item_label);

        SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SelectedItem","");
        editor.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper = new Database(this);
        mDb = mHelper.getReadableDatabase();

        SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
        selected = sp.getString("SelectedItem","");
        txtBarcode.setText(selected);

        btnScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"barcode scanner",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),BarcodeScanner.class);
                startActivity(i);
            }
        });
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedModel = txtModel.getText().toString();
                if (searchedModel.length() != 0) {
                    Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                    i.putExtra("modelNum", searchedModel);
                    startActivity(i);
                }
                else Toast.makeText(getApplicationContext(),"ใส่รหัสก่อนนะจ้ะ",Toast.LENGTH_SHORT).show();
            }
        });
        btnSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCursor = mDb.rawQuery("SELECT " + Database.COL_PRO + " FROM "
                        + Database.TABLE_PRODUCT , null);
                mCursor.moveToFirst();
                SortedSet<String> s_set = new TreeSet<String>();
                while(!mCursor.isAfterLast()){
                    s_set.add(mCursor.getString(mCursor.getColumnIndex(Database.COL_PRO)));
                    mCursor.moveToNext();
                }
                final String[] sortedList = s_set.toArray(new String[s_set.size()]);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddItemActivity.this);
                alertDialog.setTitle("เลือกประเภทสินค้า");
                alertDialog.setItems(sortedList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int order) {
                        String selectedProduct = sortedList[order];
                        Intent i = new Intent(getApplicationContext(), ProductSearchActivity.class);
                        i.putExtra("productType", selectedProduct);
                        startActivity(i);
                    }
                });
                alertDialog.show();
                return true;
            }
        });
        btnPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int volume = Integer.parseInt(tvItem.getText().toString());
                if(volume < 100){
                    volume++;
                    tvItem.setText(String.valueOf(volume));
                }
                else{
                    Toast.makeText(getApplicationContext(),"Too Much So Much Very Much",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int volume = Integer.parseInt(tvItem.getText().toString());
                if(volume > 1){
                    volume--;
                    tvItem.setText(String.valueOf(volume));
                }
                else{
                    Toast.makeText(getApplicationContext(),"น้อยกว่านี้ไม่ได้จ้ะ",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvModel.getText().toString().length()!=0){
                    mDb.execSQL(
                            "INSERT INTO " + Database.TABLE_SELECT + " ("
                                    + Database.COL_BAR + ", "
                                    + Database.COL_ITM + ", "
                                    + Database.COL_STA + ") "
                                    + "VALUES ('" + txtBarcode.getText().toString()
                                    + "', '" + tvItem.getText().toString()
                                    + "', '" + checkSaleRadio()
                                    + "');"

                    );
                    Toast.makeText(getApplicationContext(),"เพิ่มรายการแล้วจ้ะ",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"ไม่มีรายการนะจ้ะ",Toast.LENGTH_SHORT).show();
                }
            }
        });
        txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mCursor = mDb.rawQuery("SELECT * FROM "
                        + Database.TABLE_PRODUCT + " WHERE "
                        + Database.COL_BAR + " = '" + txtBarcode.getText().toString() +"'", null);
                mCursor.moveToFirst();
                if(mCursor.getCount()!=0) {
                     product = mCursor.getString(mCursor.getColumnIndex(Database.COL_PRO));
                     model = mCursor.getString(mCursor.getColumnIndex(Database.COL_MOD));
                     color = mCursor.getString(mCursor.getColumnIndex(Database.COL_COL));
                     size = mCursor.getString(mCursor.getColumnIndex(Database.COL_SIZ));
                     price = mCursor.getString(mCursor.getColumnIndex(Database.COL_PRZ));
                }
                else {
                     product = "";
                     model = "";
                     color = "";
                     size = "";
                     price = "";

                }
                tvProduct.setText(product);
                tvModel.setText(model);
                tvColor.setText(color);
                tvSize.setText(size);
                tvPrice.setText(price);
            }

        });
    }
    public void onStop() {
        super.onStop();
        mHelper.close();
        mDb.close();
    }
    public String checkSaleRadio(){
        if (radSale.isChecked()) {
            return "+";
        }
        else return "-";
    }
}


