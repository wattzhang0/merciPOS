package com.mercishoe.mercipos;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends Activity {
    public final String P_NAME = "POSdata";
    Database mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    ListView listItem;
    String model;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        model = getIntent().getExtras().getString("modelNum"); //รับค่ารหัสสินค้าที่ต้องการค้นหา
        mHelper = new Database(this);
        mDb = mHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT * FROM "
                + Database.TABLE_PRODUCT
                + " WHERE " + Database.COL_MOD + " = '" + model +"'", null);
        if(mCursor.getCount()==0){
            Toast.makeText(getApplicationContext(),"ไม่มีรายการนี้นะจ้ะ",Toast.LENGTH_SHORT).show();
            SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("SelectedItem","");
            editor.commit();
            finish();
        }
        ArrayList<String> arr_list = new ArrayList<String>();
        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            String detail = mCursor.getString(mCursor.getColumnIndex(Database.COL_PRO)) + "\t\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_MOD)) + "\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_COL)) + "\t"
                    + mCursor.getString(mCursor.getColumnIndex(Database.COL_SIZ)) + "\n"
                    + ":  " + mCursor.getString(mCursor.getColumnIndex(Database.COL_PRZ))+ " บาท";
            arr_list.add(detail);
            mCursor.moveToNext();

        }

        ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(getApplicationContext()
                , R.layout.my_listview, arr_list);

        listItem = (ListView)findViewById(R.id.search_item_list);
        listItem.setAdapter(adapterDir);
        listItem.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mCursor.moveToPosition(arg2);

                String selected = mCursor.getString(mCursor.getColumnIndex(Database.COL_BAR));
                Toast.makeText(getApplicationContext(),selected,Toast.LENGTH_SHORT).show();
                SharedPreferences sp = getSharedPreferences(P_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("SelectedItem", selected);
                editor.commit();
                finish();
            }
        });
    }

    public void onStop() {
        super.onStop();
        mHelper.close();
        mDb.close();
    }
}
