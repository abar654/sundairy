package com.kangaruu.sundiary.DiaryScreen;

import android.app.Application;


import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.kangaruu.sundiary.Model.Session;
import com.kangaruu.sundiary.Repository.SessionRepository;
import com.kangaruu.sundiary.Shared.CalendarUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DiaryViewModel extends AndroidViewModel {

    private LiveData<ArrayList<Integer>> mWeekTimes; // milliseconds
    private LiveData<Integer> mCurrentDayTotalTime; // milliseconds
    private MutableLiveData<Long> mWeekDisplayStartDate;

    private SessionRepository mSessionRepository;
    private SessionRecorder mSessionRecorder;
    private Calendar mLastRefreshed;

    public DiaryViewModel(Application application) {
        super(application);
        mSessionRepository = new SessionRepository(application);
        mSessionRecorder = new SessionRecorder(mSessionRepository);

        // TODO: Correctly process the sessions in the current weekDisplay time period
        mWeekTimes = Transformations.map(mSessionRepository.getAllSessions(), new Function<List<Session>, ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> apply(List<Session> sessions) {
                for(Session session: sessions) {
                    System.out.println(session);
                }
                return new ArrayList<Integer>(Arrays.asList(6000000, 0, 100000, 1200000, 1500000, 0, 200000));
            }
        });
    }

    public LiveData<Boolean> isRecording() {
        return mSessionRecorder.isRecording();
    }

    public LiveData<ArrayList<Integer>> getWeekTimes() {
        return mWeekTimes;
    }

    public LiveData<Integer> getTodayTotalTime() {
        Calendar now = Calendar.getInstance();

        if (mLastRefreshed == null
                || now.get(Calendar.DAY_OF_YEAR) > mLastRefreshed.get(Calendar.DAY_OF_YEAR)) {

            mLastRefreshed = now;

            long start = CalendarUtils.getMillisForStartOfDay(now.getTimeInMillis());
            long end = CalendarUtils.getMillisForStartOfDay(now.getTimeInMillis() + 24 * 60 * 60 * 1000);

            mCurrentDayTotalTime = Transformations.map(
                    mSessionRepository.getSessionsStartingBetween(start, end),
                    new Function<List<Session>, Integer>() {

                        @Override
                        public Integer apply(List<Session> sessions) {
                            int totalMillis = 0;
                            for(Session session: sessions) {
                                totalMillis += session.getEndTime() - session.getStartTime();
                            }
                            return totalMillis;
                        }

                    });

        }
        return mCurrentDayTotalTime;
    }

    public void onSessionButtonClick() {
        if (isRecording().getValue()) {
            mSessionRecorder.stopSession();
        } else {
            mSessionRecorder.startSession();
        }
    }

    private ArrayList<Integer> processWeekTimes() {
        return new ArrayList<Integer>(Arrays.asList(6000000, 0, 100000, 1200000, 1500000, 0, 200000));
    }

}
