package com.kangaruu.sundiary.DiaryScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kangaruu.sundiary.R;
import com.kangaruu.sundiary.Shared.CalendarUtils;

import java.util.ArrayList;

/**
 * The activity for a single page app which allows user to start/stop sun sessions, view their
 * sunlight time for the current day, and view their sunlight time for the past.
 */
public class DiaryActivity extends AppCompatActivity {

    private static final int TARGET_MINUTES = 30;

    private DiaryViewModel mDiaryViewModel;
    private Toast mToast;

    private Button mSessionButton;
    private TextView mTimeDisplay;
    private LinearLayout mSunMetersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // Set up the view model
        mDiaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // Set up the session button
        mSessionButton = findViewById(R.id.session_button);
        mSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiaryViewModel.onSessionButtonClick();
            }
        });
        mDiaryViewModel.isRecording().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRecording) {
                if (isRecording) {
                    mSessionButton.setText(R.string.stop_button_label);
                } else {
                    mSessionButton.setText(R.string.start_button_label);
                }
            }
        });

        // Set up the sun time display
        mTimeDisplay = findViewById(R.id.time_display);

        // Set up the sun icons for the week
        mSunMetersLayout = findViewById(R.id.sun_meters);
        mDiaryViewModel.getWeekTimes().observe(this, new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> weekTimes) {
                updateSunMeters(weekTimes);
            }
        });

        // TODO: Set up the date bar.
        //       Buttons should cause weekDisplayStartDate to change.
        //       Text should update to show the start/end days of the week by observing weekDisplayStartDate.
        //       May need to call getWeekTimes() and reobserve each time weekDisplayStartDate changes.

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Remove any observers for the total time
        mDiaryViewModel.getTodayTotalTime().removeObservers(this);

        // Re-observe in case the day has changed since the view model was created.
        mDiaryViewModel.getTodayTotalTime().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer timeToday) {
                mTimeDisplay.setText(CalendarUtils.convertMillisToTimeString(timeToday));
            }
        });
    }

    private void updateSunMeters(ArrayList<Integer> weekTimes) {
        for(int i = 0; i < mSunMetersLayout.getChildCount(); i++) {
            ImageButton sunMeter = (ImageButton) mSunMetersLayout.getChildAt((i));

            float meterPercentage = (float) weekTimes.get(i) / (TARGET_MINUTES * 60 * 1000);
            if (meterPercentage == 0) {
                sunMeter.setImageResource(R.drawable.ic_sun_0);
            } else if (meterPercentage < 0.3) {
                sunMeter.setImageResource(R.drawable.ic_sun_25);
            } else if (meterPercentage < 0.6) {
                sunMeter.setImageResource(R.drawable.ic_sun_50);
            } else if (meterPercentage < 0.9) {
                sunMeter.setImageResource(R.drawable.ic_sun_75);
            } else {
                sunMeter.setImageResource(R.drawable.ic_sun_100);
            }

            // TODO: Update string to be form "21/11 - 00:21:23" i.e. using DATE!
            final String message = "Time - " + CalendarUtils.convertMillisToTimeString(weekTimes.get(i));
            sunMeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(DiaryActivity.this, message, Toast.LENGTH_LONG);
                    mToast.show();
                }
            });
        }
    }

}
