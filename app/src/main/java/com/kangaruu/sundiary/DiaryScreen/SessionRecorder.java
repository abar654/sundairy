package com.kangaruu.sundiary.DiaryScreen;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kangaruu.sundiary.Model.Session;
import com.kangaruu.sundiary.Repository.SessionRepository;

import java.util.Calendar;

/**
 * Records sessions and saves them to the database.
 */
public class SessionRecorder {

    private static final int SAMPLING_PERIOD = 1000; // milliseconds

    private MutableLiveData<Boolean> mIsRecording;

    private SessionRepository mSessionRepository;
    private Handler mHandler;
    private Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            // Update the current session!
            updateCurrentSession();
            mHandler.postDelayed(this, SAMPLING_PERIOD);
        }
    };

    private Session mCurrentSession;

    public SessionRecorder(SessionRepository sessionRepository) {
        mSessionRepository = sessionRepository;
        mIsRecording = new MutableLiveData<>(false);
        mHandler = new Handler();
    }

    public LiveData<Boolean> isRecording() {
        return mIsRecording;
    }

    public void startSession() {
        if (!mIsRecording.getValue()) {
            System.out.println("Session started!");

            mIsRecording.setValue(true);

            // Create the new session and save it!
            long now = Calendar.getInstance().getTimeInMillis();
            mCurrentSession = new Session(now, now);
            mSessionRepository.insertSession(mCurrentSession);

            mHandler.post(mTimerTask);
        }
    }

    public void stopSession() {
        if (mIsRecording.getValue()) {
            System.out.println("Session stopped!");

            // Save the session one last time.
            updateCurrentSession();

            // Stop the recording
            mHandler.removeCallbacks(mTimerTask);
            mCurrentSession = null;
            mIsRecording.setValue(false);
        }
    }

    private void updateCurrentSession() {
        mCurrentSession.setEndTime(Calendar.getInstance().getTimeInMillis());
        mSessionRepository.updateSession(mCurrentSession);
    }

}
