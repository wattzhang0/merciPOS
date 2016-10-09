package com.mercishoe.mercipos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Database extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyDatabase";
    private static final int DB_VERSION = 7; // 10-6-2016
// ตารางฐานข้อมูลสินค้า
    public static final String TABLE_PRODUCT = "Product";
    public static final String COL_BAR = "barcode";
    public static final String COL_PRO = "product";
    public static final String COL_MOD = "model";
    public static final String COL_COL = "color";
    public static final String COL_SIZ = "size";
    public static final String COL_PRZ = "price";
//รายชื่อผู้ใช้งาน
    public static final String TABLE_USER = "User";
    public static final String COL_NAM = "name";
    public static final String COL_USE = "user";
    public static final String COL_PWD = "passward";
//รายการขายสินค้าทั้งหมด
    public static final String TABLE_SELL = "Sell";
    public static final String COL_BRN = "branch";
    //public static final String COL_NAM = "name";
    public static final String COL_DAT = "date";
    public static final String COL_TIM = "time";
    //public static final String COL_BAR = "barcode";
    public static final String COL_VOL = "volume";
    public static final String COL_COS = "cost";
    public static final String COL_PAY = "paid";
//สินค้าที่ถูกเลือก
    public static final String TABLE_SELECT = "SelectItem";
    //public static final String COL_BAR = "barcode";
    public static final String COL_ITM = "items";
    public static final String COL_STA = "status";
    Context ctx;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        ctx = context;
    }

    public void onCreate(SQLiteDatabase db) {
//--------- สร้างตาราง ----------
        db.execSQL(
                "CREATE TABLE " + TABLE_PRODUCT + " ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_BAR + " TEXT, "
                        + COL_PRO + " TEXT, "
                        + COL_MOD + " TEXT, "
                        + COL_COL + " TEXT, "
                        + COL_SIZ + " TEXT, "
                        + COL_PRZ + " TEXT);"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_USER + " ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_NAM + " TEXT, "
                        + COL_USE + " TEXT, "
                        + COL_PWD + " TEXT);"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_SELL + " ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_BRN + " TEXT, "
                        + COL_USE + " TEXT, "
                        + COL_DAT + " TEXT, "
                        + COL_TIM + " TEXT, "
                        + COL_BAR + " TEXT, "
                        + COL_VOL + " TEXT, "
                        + COL_COS + " TEXT, "
                        + COL_STA + " TEXT, "
                        + COL_PAY + " TEXT);"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_SELECT + " ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_BAR + " TEXT, "
                        + COL_ITM + " TEXT, "
                        + COL_STA + " TEXT);"
        );
//--------------------------------
/*        db.execSQL(
                "INSERT INTO " + TABLE_SELECT + " ("
                        + COL_BAR + ", "
                        + COL_ITM + ", "
                        + COL_STA + ") "
                        + "VALUES ('2010000011351', '1','+');"
        ); */

        // read from database csv file

        try{
            BufferedReader br = new BufferedReader( new InputStreamReader(
                    ctx.getAssets().open("productdata.csv")));
            String readLine = null;
            readLine = br.readLine();
            try{
                while ((readLine = br.readLine()) != null){
                    String[] str = readLine.split(",");
                    db.execSQL(
                            "INSERT INTO " + TABLE_PRODUCT + " ("
                                    + COL_BAR + ", "
                                    + COL_PRO + ", "
                                    + COL_MOD + ", "
                                    + COL_COL + ", "
                                    + COL_SIZ + ", "
                                    + COL_PRZ + ") "
                                    + "VALUES ('"
                                    + str[0] + "', '"
                                    + str[1] +"', '"
                                    + str[2] + "','"
                                    + str[3] + "', '"
                                    + str[4] + "', '"
                                    + str[5] +"');"
                    );
                }
            }catch (IOException e){}
        }catch (IOException e){}
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCT);
        db.execSQL(
                "CREATE TABLE " + TABLE_PRODUCT + " ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_BAR + " TEXT, "
                        + COL_PRO + " TEXT, "
                        + COL_MOD + " TEXT, "
                        + COL_COL + " TEXT, "
                        + COL_SIZ + " TEXT, "
                        + COL_PRZ + " TEXT);"
        );
        try{
            BufferedReader br = new BufferedReader( new InputStreamReader(
                    ctx.getAssets().open("productdata.csv")));
            String readLine = null;
            readLine = br.readLine();
            try{
                while ((readLine = br.readLine()) != null){
                    String[] str = readLine.split(",");
                    db.execSQL(
                            "INSERT INTO " + TABLE_PRODUCT + " ("
                                    + COL_BAR + ", "
                                    + COL_PRO + ", "
                                    + COL_MOD + ", "
                                    + COL_COL + ", "
                                    + COL_SIZ + ", "
                                    + COL_PRZ + ") "
                                    + "VALUES ('"
                                    + str[0] + "', '"
                                    + str[1] +"', '"
                                    + str[2] + "','"
                                    + str[3] + "', '"
                                    + str[4] + "', '"
                                    + str[5] +"');"
                    );
                }
            }catch (IOException e){}
        }catch (IOException e){}
    }
}