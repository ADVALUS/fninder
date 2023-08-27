import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "your_database_name";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your tables here
        db.execSQL("CREATE TABLE your_table_name (id INTEGER PRIMARY KEY AUTOINCREMENT, dataField TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
    }
}



import android.content.ContentValues;
        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;

public class DatabaseRepository {
    private final DatabaseHelper dbHelper;

    public DatabaseRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insertMultipleRows(final List<YourDataModel> dataList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                for (YourDataModel data : dataList) {
                    ContentValues values = new ContentValues();
                    values.put("column1", data.getColumn1Value());
                    values.put("column2", data.getColumn2Value());
                    db.insert("your_table_name", null, values);
                }
                db.close();
            }
        }).start();
    }
}



    List<YourDataModel> dataList = new ArrayList<>();
dataList.add(new YourDataModel("value1_row1", "value2_row1"));
        dataList.add(new YourDataModel("value1_row2", "value2_row2"));

        DatabaseRepository repository = new DatabaseRepository(this);
        repository.insertMultipleRows(dataList);
