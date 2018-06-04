package DatabaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.provider.BaseColumns;

import static DAO.UserDao.DESCRIPTION;
import static DAO.UserDao.ID;
import static DAO.UserDao.MAIL;
import static DAO.UserDao.PASSWORD;
import static DAO.UserDao.PHONE_NUMBER;
import static DAO.UserDao.USERNAME;
import static DatabaseUtils.CommonUtils.DATABASE_NAME;
import static DatabaseUtils.CommonUtils.TABLE_NAME;

public class DatabaseWriter extends SQLiteOpenHelper implements BaseColumns{


    public DatabaseWriter(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 0, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USERNAME + " VARCHAR(255) NOT NULL," +
                MAIL + " VARCHAR(255) NOT NULL," +
                PASSWORD + " VARCHAR(255) NOT NULL," +
                DESCRIPTION + " VARCHAR(255)," +
                PHONE_NUMBER + " INTEGER );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
        onCreate(db);
    }

    public boolean insertUser(String name, String mail, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, name);
        contentValues.put(MAIL, mail);
        contentValues.put(PASSWORD, password);
        long result = db.insert(TABLE_NAME, null,contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);
    }
}
