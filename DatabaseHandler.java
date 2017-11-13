package com.app.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.myapplication.model.WeatherDataDTO;

public class DatabaseHandler extends SQLiteOpenHelper{
	 // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "weather";
    // Contacts table name
    private static final String TABLE_CITY = "city";
    private SQLiteDatabase db = null;
    private static DatabaseHandler dbHelper = null;
    private Cursor cursor = null;

    // users Table Columns names
    public static final String CITYID = "_id";
    public static final String CITYNAME = "cityName";
    public static final String TEMP = "temp";
    public static final String PRESSURE = "pressure";
    public static final String HUMIDITY = "humidity";

    Context context;
	public DatabaseHandler(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        dbHelper = this;
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_CITY + "("
                + CITYID + " INTEGER PRIMARY KEY,"
				+ CITYNAME + " TEXT,"
                + TEMP + " TEXT,"
                + PRESSURE + " TEXT,"
                + HUMIDITY + " TEXT"
                + ")";

        db.execSQL(CREATE_USER_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

    public void close(){
        if (db != null)
            db.close();
    }

    public static DatabaseHandler getInstance(Context context){
        if(dbHelper==null){
            dbHelper = new DatabaseHandler(context.getApplicationContext());
        }
        return dbHelper;
    }

    public Cursor getData(String city)
    {
        String[] columns= {};
        SQLiteDatabase db = null;
        try
        {
            db = this.getWritableDatabase();
            columns = new String[]{CITYID, CITYNAME, TEMP, PRESSURE, HUMIDITY};
            cursor= db.query(TABLE_CITY, columns, CITYNAME + " = '" + city + "'",null,null,null,null);
            // db.close(); // Closing database connection
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cursor;
    }

    // Adding new data
    public void addData(WeatherDataDTO weatherDataDTO) {

        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CITYNAME, weatherDataDTO.getCity());
            values.put(TEMP, weatherDataDTO.getTemp());
            values.put(PRESSURE, weatherDataDTO.getPressure());
            values.put(HUMIDITY, weatherDataDTO.getHumidity());

            // Inserting Row
            long i = db.insert(TABLE_CITY, null, values);
            Log.e("RTA", "Row inserted at : "+i);

            // db.close(); // Closing database connection
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
