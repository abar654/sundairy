package com.kangaruu.sundiary.Repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kangaruu.sundiary.Model.Session;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Session.class}, version = 1, exportSchema = false)
public abstract class SessionRoomDatabase extends RoomDatabase {

    public abstract SessionDao sessionDao();

    private static volatile SessionRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static SessionRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SessionRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SessionRoomDatabase.class, "session_database")
                            .addCallback(sSessionDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sSessionDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SessionDao dao = INSTANCE.sessionDao();

                    // Create some dummy data on database create.

                    // 6/12 - 2 mins
                    Session newSession = new Session(1638806215280L, 1638806335280L);
                    dao.insertSession(newSession);

                    // 8/12 - 10 mins
                    newSession = new Session(1638977215280L, 1638977815280L);
                    dao.insertSession(newSession);

                    //10/12 - 20 mins
                    newSession = new Session(1639150615280L, 1639151815280L);
                    dao.insertSession(newSession);

                    //11/12 - 60 mins
                    newSession = new Session(1639238335280L, 1639241935280L);
                    dao.insertSession(newSession);

                }
            });
        }
    };

}
