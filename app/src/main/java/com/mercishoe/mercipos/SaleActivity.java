package com.mercishoe.mercipos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class SaleActivity extends Activity {
    final String MERCI = "merciPOS";
    final String P_NAME = "POS_DATA",U_ID = "UserID",
            U_PWD="UserPWD",U_BRN="UserBranch",S_ITEM="SelectedItem";
    Button btnAdd, btnRmv, btnOk;
    RadioButton radCash, radCard;
    TextView sumText, itemText,amountText;
    EditText discountText;
    Database mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    ListView sale_list;
    ArrayList<String> sale_bar_list;
    String detail;
    int sum_amount = 0;
    int add_amount = 0;
    int sum_sale = 0;
    int sum_return = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        btnAdd = (Button) findViewById(R.id.add_button);
        btnRmv = (Button) findViewById(R.id.remove_button);
        btnOk = (Button) findViewById(R.id.saleOk_button);
        radCash = (RadioButton) findViewById(R.id.cash_radio);
        radCard = (RadioButton) findViewById(R.id.card_radio);
        sumText = (TextView) findViewById(R.id.sum_text);
        itemText = (TextView) findViewById(R.id.item_text);
        amountText = (TextView) findViewById(R.id.amount_text);
        discountText = (EditText) findViewById(R.id.discount_text);
        sale_list = (ListView) findViewById(R.id.sellitem_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHelper = new Database(this);
        mDb = mHelper.getReadableDatabase();
        sale_list.setAdapter(updateSaleList());

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(i);

            }
        });
        btnRmv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ลบละจ้ะ", Toast.LENGTH_SHORT).show();
                mDb.execSQL("DELETE FROM " + Database.TABLE_SELECT
                        + " WHERE _id = (SELECT MAX(_id) FROM " + Database.TABLE_SELECT + ");");
                sale_list.setAdapter(updateSaleList());
            }
        });
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCursor.getCount() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SaleActivity.this);
                    builder.setTitle("สรุปรายการขาย");
                    String payment = "";
                    if (radCash.isChecked()) payment = "เงินสด";
                    else payment = "บัตรเครดิต";
                    String alert_message = "จำนวนรายการ  : " + itemText.getText().toString() + "\n"
                            + "ขาย  : " + String.valueOf(sum_sale) + "\t" + "ชิ้น" + "\n"
                            + "คืน  : " + String.valueOf(sum_return) + "\t" + "ชิ้น" + "\n"
                            + "รวมเงิน  : " + amountText.getText().toString() + "\t" + "บาท" + "\n"
                            + "ชำระด้วย  : " + payment;
                    builder.setMessage(alert_message);
                    builder.setPositiveButton("ใช่แล้ว", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCursor.moveToFirst();
                            int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            int month = Calendar.getInstance().get(Calendar.MONTH);
                            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                            String date = Integer.toString(day) + "/"
                                    + Integer.toString(month + 1) + "/" + Integer.toString(year);
                            SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                            String seller = sp.getString(U_ID, "");
                            String branch = sp.getString(U_BRN, "");
                            String statusPaid,statusSale;
                            int priz = 0,disc = 0, itemSale = 0;
                            String cost;

                            while (!mCursor.isAfterLast()) {
                                itemSale = mCursor.getInt(mCursor.getColumnIndex(Database.COL_ITM));
                                if(mCursor.getString(mCursor.getColumnIndex(Database.COL_STA)).equals("+")){
                                    statusSale = "S";
                                }else {
                                    statusSale = "R";
                                    itemSale = -itemSale;
                                }
                                priz = mCursor.getInt(mCursor.getColumnIndex(Database.COL_PRZ))* itemSale;
                                if (discountText.getText().toString().length() != 0) {
                                    disc = Integer.parseInt(discountText.getText().toString());
                                } else disc = 0;
                                if (mCursor.isFirst()) cost = Integer.toString(priz - disc);
                                else cost = Integer.toString(priz);
                                if(radCash.isChecked()) statusPaid = "X";
                                else statusPaid = "K";

                                mDb.execSQL(
                                        "INSERT INTO " + Database.TABLE_SELL + " ("
                                                + Database.COL_BRN + ", "
                                                + Database.COL_USE + ", "
                                                + Database.COL_DAT + ", "
                                                + Database.COL_TIM + ", "
                                                + Database.COL_BAR + ", "
                                                + Database.COL_VOL + ", "
                                                + Database.COL_COS + ", "
                                                + Database.COL_STA + ", "
                                                + Database.COL_PAY + ") "
                                                + "VALUES ('" + branch + "', '"
                                                + seller + "', '"
                                                + date + "', '"
                                                + Integer.toString(time) + "', '"
                                                + mCursor.getString(mCursor.getColumnIndex(Database.COL_BAR)) + "', '"
                                                + itemSale + "', '"
                                                + cost + "', '"
                                                + statusSale + "', '"
                                                + statusPaid + "');");
                                mCursor.moveToNext();
                            }
                            mDb.execSQL(
                                    "DELETE FROM " + Database.TABLE_SELECT
                            );
                            finish();
                        }
                    });
                    builder.setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }else{
                    Toast.makeText(getApplicationContext(),"ไม่มีรายการนะจ๊ะ",Toast.LENGTH_SHORT).show();
                }
            }
        });
        discountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int sum = Integer.parseInt(sumText.getText().toString());
                int discount;
                try{
                    if(discountText.getText().toString().length()!=0){
                        discount = Integer.parseInt(discountText.getText().toString()) ;
                    }
                    else discount = 0;
                }catch (NumberFormatException e){
                    discountText.setText("");
                    Toast.makeText(getApplicationContext(),"อย่าใส่จุดนะจ้ะ",Toast.LENGTH_SHORT).show();
                    discount = 0;
                }
                amountText.setText(Integer.toString(sum-discount));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.close();
        mDb.close();
    }

    public ArrayAdapter<String> updateSaleList(){
        mCursor = mDb.rawQuery(
                 "SELECT *" + "FROM "
                        + Database.TABLE_SELECT + " JOIN " + Database.TABLE_PRODUCT
                        + " ON " + Database.TABLE_SELECT + "." + Database.COL_BAR
                        + " = " + Database.TABLE_PRODUCT + "." + Database.COL_BAR
                , null);

        ArrayList<String> sale_item_list = new ArrayList<String>();
        String count = String.valueOf(mCursor.getCount());
        sum_amount = 0;
        add_amount = 0;
        sum_sale = 0;
        sum_return = 0;
        itemText.setText(count);
        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            detail = mCursor.getString(mCursor.getColumnIndex(Database.COL_STA)) + "\t\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_PRO)) + "\t\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_MOD)) + "\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_COL)) + "\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_SIZ)) + "\n"+ "\t\t\t\t"
                    + "ราคา : " + mCursor.getString(mCursor.getColumnIndex(Database.COL_PRZ)) + "\t\t"
                    + "จำนวน : " + mCursor.getString(mCursor.getColumnIndex(Database.COL_ITM)) + " ชิ้น";
            add_amount = mCursor.getInt(mCursor.getColumnIndex(Database.COL_PRZ))
                        * mCursor.getInt(mCursor.getColumnIndex(Database.COL_ITM));
            if(mCursor.getString(mCursor.getColumnIndex(Database.COL_STA)).equals("+")){
                sum_amount = sum_amount + add_amount;
                sum_sale = sum_sale + mCursor.getInt(mCursor.getColumnIndex(Database.COL_ITM));
            }
            else {
                sum_amount = sum_amount - add_amount;
                sum_return = sum_return + mCursor.getInt(mCursor.getColumnIndex(Database.COL_ITM));
            }
            sale_item_list.add(detail);
            mCursor.moveToNext();
        }
        sumText.setText(String.valueOf(sum_amount));
        int discount;
        if(discountText.getText().toString().length()!=0){
            discount = Integer.parseInt(discountText.getText().toString()) ;
        }
        else discount = 0;
        amountText.setText(Integer.toString(sum_amount-discount));

        ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(getApplicationContext()
                , R.layout.my_listview, sale_item_list);
        return adapterDir;
    }
}
