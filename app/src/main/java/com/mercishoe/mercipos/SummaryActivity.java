package com.mercishoe.mercipos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SummaryActivity extends Activity {
    Button btnDate, btnSentSum;
    private int year, month, day;
    private final int LINE_ID = 1;
    private final String[] sharePackages = {"com.google.android.gm","jp.naver.line.android","com.facebook.katana","com.twitter.android"};
    Database mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    ListView sale_list;
    String result,datepick;
    TextView tvAmount,tvSumItem,tvCash,tvCard;
    int amountSum, itemSum, cashSum, cardSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        btnDate = (Button) findViewById(R.id.dateshow_button);
        btnSentSum = (Button) findViewById(R.id.sent_summary_button);
        sale_list = (ListView) findViewById(R.id.sellitem_list);
        tvAmount = (TextView) findViewById(R.id.sum_text);
        tvSumItem = (TextView) findViewById(R.id.item_text);
        tvCash = (TextView) findViewById(R.id.cash_text);
        tvCard = (TextView) findViewById(R.id.card_text);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mHelper = new Database(this);
        mDb = mHelper.getReadableDatabase();

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        datepick = Integer.toString(day) + "/"
                + Integer.toString(month + 1) + "/" + Integer.toString(year);
        btnDate.setText(datepick);
        result = "";
        updateList();

        btnDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearOfSet, int monthOfYear, int dayOfMonth) {
                        datepick = Integer.toString(dayOfMonth) + "/"
                                + Integer.toString(monthOfYear + 1) + "/" + Integer.toString(yearOfSet);
                        btnDate.setText(datepick);
                        updateList();
                    }
                }, year, month, day);
                dp.show();
            }
        });

        btnSentSum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
          // share text to Line App
                if(mCursor.getCount() != 0){
                    if(isShareAppInstall(LINE_ID)){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("line://msg/text/" + result));
                        startActivity(intent);
                    }else{
                        shareAppDl(LINE_ID);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"ไม่มีรายการนะจ้ะ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private Boolean isShareAppInstall(int shareId){
        try {
            PackageManager pm = getPackageManager();
            pm.getApplicationInfo(sharePackages[shareId], PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    // アプリが無かったのでGooglePalyに飛ばす
    private void shareAppDl(int shareId){
        Uri uri = Uri.parse("market://details?id="+sharePackages[shareId]);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mHelper.close();
        mDb.close();
    }
    private void updateList(){
        mCursor = mDb.rawQuery(
                "SELECT *" + "FROM "
                        + Database.TABLE_SELL + " JOIN " + Database.TABLE_PRODUCT
                        + " ON " + Database.TABLE_SELL + "." + Database.COL_BAR
                        + " = " + Database.TABLE_PRODUCT + "." + Database.COL_BAR
                        + " WHERE " + Database.TABLE_SELL + "." + Database.COL_DAT
                        + " = '" + datepick + "'"
                , null);
        mCursor.moveToFirst();
        ArrayList<String> sale_item_list = new ArrayList<String>();
        String detail;
        amountSum = 0;
        itemSum = 0;
        cashSum = 0;
        cardSum = 0;

        if(mCursor.getCount() != 0 ) {
            result = mCursor.getString(mCursor.getColumnIndex(Database.COL_USE)) + "%0A"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_BRN)) + "%0A"
                    + datepick;
            String status,paid;
            while (!mCursor.isAfterLast()) {
                if(mCursor.getString(mCursor.getColumnIndex(Database.COL_STA)).equals("S")){
                    status = "ขาย";
                }else {
                    status = "คืน";
                }
                if(mCursor.getString(mCursor.getColumnIndex(Database.COL_PAY)).equals("X")){
                    paid = "เงินสด";
                    cashSum = cashSum + mCursor.getInt(mCursor.getColumnIndex(Database.COL_COS));
                }else {
                    paid = "บัตร";
                    cardSum = cardSum + mCursor.getInt(mCursor.getColumnIndex(Database.COL_COS));
                }
                detail = status + "\t\t"
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_PRO)) + "\t"
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_MOD)) + "\t"
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_COL)) + "\t"
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_SIZ)) + "\n"
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_VOL)) + " ชิ้น" + "\t\t\t\t"
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_COS)) + " บาท"+ "\t\t"
                        + "ชำระ : " + paid;
                sale_item_list.add(detail);

                result = result + "%0A" + mCursor.getString(mCursor.getColumnIndex(Database.COL_BAR)) + ","
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_VOL)) + ","
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_COS)) + ","
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_STA)) + ","
                        + mCursor.getString(mCursor.getColumnIndex(Database.COL_PAY));

                amountSum = amountSum + mCursor.getInt(mCursor.getColumnIndex(Database.COL_COS));
                itemSum = itemSum + mCursor.getInt(mCursor.getColumnIndex(Database.COL_VOL));
                mCursor.moveToNext();
            }
        }
        ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(getApplicationContext()
                , R.layout.my_listview, sale_item_list);
        sale_list.setAdapter(adapterDir);
        tvAmount.setText(String.valueOf(amountSum));
        tvSumItem.setText(String.valueOf(itemSum));
        tvCash.setText(String.valueOf(cashSum));
        tvCard.setText(String.valueOf(cardSum));
        result = result + "%0A" + String.valueOf(itemSum) + "%0A" + String.valueOf(cashSum)
                + "%0A" + String.valueOf(cardSum) + "%0A" + String.valueOf(amountSum);
    }

}
