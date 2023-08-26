import android.app.Application;
import androidx.room.Room;
import androidx.room.Database;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.RoomDatabase;
import java.util.List;

public class MyApplication extends Application {

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "data-db").build();

        insertDataUsingLoop();
        updateData();
        deleteData();
        retrieveAndPrintData();
    }

    private void insertDataUsingLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    DataEntry dataEntry = new DataEntry();
                    dataEntry.column1 = "Value " + i;
                    dataEntry.column2 = "Another Value " + i;
                    database.dataDao().insertData(dataEntry);
                }
            }
        }).start();
    }

    private void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Assuming you have data to update (retrieve it first)
                DataEntry dataToUpdate = database.dataDao().getDataById(1);
                if (dataToUpdate != null) {
                    dataToUpdate.column1 = "Updated Value";
                    dataToUpdate.column2 = "Updated Another Value";
                    database.dataDao().updateData(dataToUpdate);
                }
            }
        }).start();
    }

    private void deleteData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Assuming you have data to delete (retrieve it first)
                DataEntry dataToDelete = database.dataDao().getDataById(2);
                if (dataToDelete != null) {
                    database.dataDao().deleteData(dataToDelete);
                }
            }
        }).start();
    }

    private void retrieveAndPrintData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DataEntry> dataEntries = database.dataDao().getAllData();
                for (DataEntry dataEntry : dataEntries) {
                    System.out.println("ID: " + dataEntry.id + ", Column 1: " + dataEntry.column1 + ", Column 2: " + dataEntry.column2);
                }
            }
        }).start();
    }

    @Entity(tableName = "data")
    public static class DataEntry {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String column1;
        public String column2;
    }

    @Database(entities = {DataEntry.class}, version = 1)
    public abstract static class AppDatabase extends RoomDatabase {
        public abstract DataDao dataDao();
    }

    @Dao
    public interface DataDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertData(DataEntry data);

        @Update
        void updateData(DataEntry data);

        @Delete
        void deleteData(DataEntry data);

        @Query("SELECT * FROM data")
        List<DataEntry> getAllData();

        @Query("SELECT * FROM data WHERE id = :id")
        DataEntry getDataById(int id);
    }
}
