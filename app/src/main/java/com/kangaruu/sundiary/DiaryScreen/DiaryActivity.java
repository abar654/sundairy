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
import java.util.Calendar;

/**
 * The activity for a single page app which allows user to start/stop sun sessions, view their
 * sunlight time for the current day, and view their sunlight time for the past.
 */
public class DiaryActivity extends AppCompatActivity {

    private static final int TARGET_MINUTES = 30;

    private DiaryViewModel mDiaryViewModel;
    private Toast mToast;

    private TextView mTimeDisplay;
    private TextView mDateDisplay;
    private LinearLayout mSunMetersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // Set up the view model
        mDiaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        // Set up the session button
        final Button sessionButton = findViewById(R.id.session_button);
        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiaryViewModel.onSessionButtonClick();
            }
        });
        mDiaryViewModel.isRecording().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRecording) {
                if (isRecording) {
                    sessionButton.setText(R.string.stop_button_label);
                } else {
                    sessionButton.setText(R.string.start_button_label);
                }
            }
        });

        // Set up the sun time display
        mTimeDisplay = findViewById(R.id.time_display);

        // Set up the sun icons for the week
        mSunMetersLayout = findViewById(R.id.sun_meters);

        // Set up the date bar.
        ImageButton prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiaryViewModel.displayPreviousWeek();
            }
        });

        ImageButton nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDiaryViewModel.displayNextWeek();
            }
        });

        mDateDisplay = findViewById(R.id.date_display);
        mDiaryViewModel.getWeekDisplayStartDate().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(final Long startOfWeek) {
                updateDateDisplay(startOfWeek);
                mDiaryViewModel.refreshWeekTimes(DiaryActivity.this)
                        .observe(DiaryActivity.this, new Observer<ArrayList<Integer>>() {
                    @Override
                    public void onChanged(ArrayList<Integer> weekTimes) {

                        System.out.println("New week! " + weekTimes);

                        updateSunMeters(weekTimes, startOfWeek);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-observe in case the day has changed since the view model was created.
        mDiaryViewModel.refreshTodayTotalTime(this).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer timeToday) {
                mTimeDisplay.setText(CalendarUtils.convertMillisToTimeString(timeToday));
            }
        });
    }

    private void updateDateDisplay(Long startOfWeek) {
        long now = Calendar.getInstance().getTimeInMillis();
        System.out.println(startOfWeek + " : " + CalendarUtils.getMillisForStartOfWeek(now));
        if (startOfWeek == CalendarUtils.getMillisForStartOfWeek(now)) {
            mDateDisplay.setText(R.string.present_week);
        } else {
            Calendar startDay = Calendar.getInstance();
            startDay.setTimeInMillis(startOfWeek);
            Calendar endDay = Calendar.getInstance();
            endDay.setTimeInMillis(startOfWeek);

            endDay.add(Calendar.DATE, 6);

            mDateDisplay.setText(String.format("%d/%d - %d/%d",
                    startDay.get(Calendar.DAY_OF_MONTH), startDay.get(Calendar.MONTH) + 1,
                    endDay.get(Calendar.DAY_OF_MONTH), endDay.get(Calendar.MONTH) + 1));
        }
    }

    private void updateSunMeters(ArrayList<Integer> weekTimes, Long startOfWeek) {
        Calendar currentDay = Calendar.getInstance();
        currentDay.setTimeInMillis(startOfWeek);

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

            final String message = String.format("%d/%d - ",
                    currentDay.get(Calendar.DAY_OF_MONTH), currentDay.get(Calendar.MONTH) + 1)
                    + CalendarUtils.convertMillisToTimeString(weekTimes.get(i));

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

            currentDay.add(Calendar.DATE, 1);
        }
    }

}
