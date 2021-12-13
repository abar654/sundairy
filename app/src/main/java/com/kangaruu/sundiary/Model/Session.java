package com.kangaruu.sundiary.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a timed session spent in the sun.
 */
@Entity(tableName = "session_table")
public class Session {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "start_time")
    private long mStartTime;

    @NonNull
    @ColumnInfo(name = "end_time")
    private long mEndTime;

    public Session(long startTime, long endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ start: " + this.mStartTime
                + ", end: " + this.mEndTime + " }";
    }

}
