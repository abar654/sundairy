package com.kangaruu.sundiary.Repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kangaruu.sundiary.Model.Session;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSession(Session session);

    @Update
    void updateSession(Session session);

    @Query("SELECT * from session_table WHERE start_time >= :firstTime AND start_time < :secondTime")
    LiveData<List<Session>> getSessionsStartingBetween(long firstTime, long secondTime);

    @Query("SELECT * from session_table ORDER BY start_time DESC")
    LiveData<List<Session>> getAllSessions();

}
