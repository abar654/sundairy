package com.kangaruu.sundiary.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kangaruu.sundiary.Model.Session;

import java.util.List;

public class SessionRepository {

    private SessionDao mSessionDao;

    public SessionRepository(Application application) {
        SessionRoomDatabase db = SessionRoomDatabase.getDatabase(application);
        mSessionDao = db.sessionDao();
    }

    public void insertSession(final Session session) {
        SessionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mSessionDao.insertSession(session);
            }
        });
    }

    public void updateSession(final Session session) {
        SessionRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mSessionDao.updateSession(session);
            }
        });
    }

    public LiveData<List<Session>> getAllSessions() {
        return mSessionDao.getAllSessions();
    }

    public LiveData<List<Session>> getSessionsStartingBetween(long firstTime, long secondTime) {
        return mSessionDao.getSessionsStartingBetween(firstTime, secondTime);
    }


}
