package DatabaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.provider.BaseColumns;

import org.json.JSONObject;

import DAO.UserDao;

import static DAO.UserDao.DESCRIPTION;
import static DAO.UserDao.ID;
import static DAO.UserDao.MAIL;
import static DAO.UserDao.PASSWORD;
import static DAO.UserDao.RFID;
import static DAO.UserDao.PHONE_NUMBER;
import static DAO.UserDao.USERNAME;
import static DatabaseUtils.CommonUtils.DATABASE_NAME;
import static DatabaseUtils.CommonUtils.TABLE_NAME;

public class DatabaseWriter extends SQLiteOpenHelper implements BaseColumns{


    public DatabaseWriter(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        //onUpgrade(db, 0, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USERNAME + " VARCHAR(255) NOT NULL," +
                MAIL + " VARCHAR(255) NOT NULL," +
                PASSWORD + " VARCHAR(255) NOT NULL," +
                RFID + " VARCHAR(255) UNIQUE NOT NULL," +
                DESCRIPTION + " VARCHAR(255)," +
                PHONE_NUMBER + " INTEGER );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    public boolean insertUser(String name, String mail, String password, String rfid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, name);
        contentValues.put(MAIL, mail);
        contentValues.put(PASSWORD, password);
        contentValues.put(RFID, rfid);
        long result = db.insert(TABLE_NAME, null,contentValues);
        return result != -1;
    }

    public boolean updateUser(String name, String mail, String password, String rfid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, name);
        contentValues.put(MAIL, mail);
        contentValues.put(PASSWORD, password);
        long result = db.update(TABLE_NAME, contentValues, RFID + " = " + rfid, null);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
    }

    public Cursor userExist(String rfid){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + RFID + " FROM " + TABLE_NAME + " WHERE " + RFID + " = '" + rfid + "'",null);
    }

    public Integer deleteData(String rfid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, UserDao.RFID + " = '" + rfid + "'",null);
    }

    public Cursor getUser(String rfid){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " + DESCRIPTION + " FROM " + TABLE_NAME + " WHERE " + RFID + " = '" + rfid + "'",null);
    }

    public boolean insertHistory(String rfid, JSONObject history){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DESCRIPTION, history.toString());
        long result = db.update(TABLE_NAME, contentValues, rfid, null);
        return result != -1;
    }
}
