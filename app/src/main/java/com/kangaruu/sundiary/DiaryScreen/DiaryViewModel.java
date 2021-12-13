package com.kangaruu.sundiary.DiaryScreen;

import android.app.Application;


import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
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
        mWeekDisplayStartDate = new MutableLiveData<>(
                CalendarUtils.getMillisForStartOfWeek(Calendar.getInstance().getTimeInMillis()));
    }

    public LiveData<Boolean> isRecording() {
        return mSessionRecorder.isRecording();
    }

    public LiveData<ArrayList<Integer>> refreshWeekTimes(LifecycleOwner owner) {
        if (mWeekTimes != null) {
            mWeekTimes.removeObservers(owner);
        }

        final long start = mWeekDisplayStartDate.getValue();
        final long end = start + 7 * 24 * 60 * 60 * 1000;

        mWeekTimes = Transformations.map(
                mSessionRepository.getSessionsStartingBetween(start, end),
                new Function<List<Session>, ArrayList<Integer>>() {

            @Override
            public ArrayList<Integer> apply(List<Session> sessions) {

                Integer[] weekTimes = {0, 0, 0, 0, 0, 0, 0};

                for(Session session: sessions) {
                    int dayIndex = (int) (session.getStartTime() - start) / (24 * 60 * 60 * 1000);
                    weekTimes[dayIndex] += (int) (session.getEndTime() - session.getStartTime());
                }

                return new ArrayList<Integer>(Arrays.asList(weekTimes));
            }

        });

        return mWeekTimes;
    }

    public LiveData<Integer> refreshTodayTotalTime(LifecycleOwner owner) {
        if (mCurrentDayTotalTime != null) {
            mCurrentDayTotalTime.removeObservers(owner);
        }

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

    MutableLiveData<Long> getWeekDisplayStartDate() {
        return mWeekDisplayStartDate;
    }

    public void onSessionButtonClick() {
        if (isRecording().getValue()) {
            mSessionRecorder.stopSession();
        } else {
            mSessionRecorder.startSession();
        }
    }

    public void displayPreviousWeek() {
        adjustDisplayWeek(false);
    }

    public void displayNextWeek() {
        adjustDisplayWeek(true);
    }

    private void adjustDisplayWeek(boolean forward) {
        int adjustDays = 7;
        if (!forward) {
            adjustDays *= -1;
        }
        Calendar newDay = Calendar.getInstance();
        newDay.setTimeInMillis(mWeekDisplayStartDate.getValue());
        newDay.add(Calendar.DATE, adjustDays);
        mWeekDisplayStartDate.setValue(newDay.getTimeInMillis());
    }

}
