package com.mercishoe.mercipos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

public class ProductSearchActivity extends Activity {
    Database mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    ListView listItem;
    String product;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        product = getIntent().getExtras().getString("productType"); //รับค่าสินค้าที่ต้องการค้นหา
        mHelper = new Database(this);
        mDb = mHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT * FROM "
                + Database.TABLE_PRODUCT
                + " WHERE " + Database.COL_PRO + " = '" + product +"'"
                , null);
        SortedSet s_set = new TreeSet();
        mCursor.moveToFirst();
        while ( !mCursor.isAfterLast() ){
            s_set.add(mCursor.getString(mCursor.getColumnIndex(Database.COL_MOD)));
            mCursor.moveToNext();
        }
        final List<String> arr_list = new ArrayList<>(s_set);
        ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(getApplicationContext()
                , R.layout.my_listview, arr_list);

        listItem = (ListView)findViewById(R.id.search_product_list);
        listItem.setAdapter(adapterDir);
        listItem.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("modelNum", arr_list.get(arg2));
                startActivity(i);
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
