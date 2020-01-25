package com.example.silenceapp_2020;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Currently allows user to alter Do Not Disturb
 * settings programmatically. Original code from online example:
 * https://android--examples.blogspot.com/2017/08/android-turn-on-off-do-not-disturb-mode.html
 *
 */

public class MainActivity extends AppCompatActivity {
    //Necessary for changing activity, context
    private Context mContext;
    private Activity mActivity;

    //Temporary way to call changeInterruptionFilter; change from buttons to
    //enter/exit boundaries
    private Button mBtnFilterNone;
    private Button mBtnFilterAll;

    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        // Get the widget reference from xml layout
        //mRootLayout = findViewById(R.id.root_layout);
        mBtnFilterAll = findViewById(R.id.btn_all);
        mBtnFilterNone = findViewById(R.id.btn_none);

        /*
            NotificationManager
                Class to notify the user of events that happen. This is how you tell
                the user that something has happened in the background.
        */

        // Get the notification manager instance
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Total silence the device, turn off all notifications
        mBtnFilterNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_NONE);
            }
        });

        // Turn off do not disturb mode, allow all notifications
        mBtnFilterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        });
    }

    protected void changeInterruptionFiler(int interruptionFilter){
        /**
         * This is the method responsible for initiating the shift from DnD to silent, etc.
         * To go to COMPLETELY SILENT (when ENTERING an area) call:
         * changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_NONE);
         * To go revert this (when LEAVING an area) call:
         * changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
         *
         * Details of possible args are documented in each conditional
         */
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){ // If api level minimum 23
            /*
                boolean isNotificationPolicyAccessGranted ()
                    Checks the ability to read/modify notification policy for the calling package.
                    Returns true if the calling package can read/modify notification policy.
                    Request policy access by sending the user to the activity that matches the
                    system intent action ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS.

                    Use ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED to listen for
                    user grant or denial of this access.

                Returns
                    boolean

            */
            // If notification policy access granted for this package
            if(mNotificationManager.isNotificationPolicyAccessGranted()){
                /*
                    void setInterruptionFilter (int interruptionFilter)
                        Sets the current notification interruption filter.

                        The interruption filter defines which notifications are allowed to interrupt
                        the user (e.g. via sound & vibration) and is applied globally.

                        Only available if policy access is granted to this package.

                    Parameters
                        interruptionFilter : int
                        Value is INTERRUPTION_FILTER_NONE, INTERRUPTION_FILTER_PRIORITY,
                        INTERRUPTION_FILTER_ALARMS, INTERRUPTION_FILTER_ALL
                        or INTERRUPTION_FILTER_UNKNOWN.
                */

                // Set the interruption filter
                mNotificationManager.setInterruptionFilter(interruptionFilter);
            }else {
                /*
                    String ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                        Activity Action : Show Do Not Disturb access settings.
                        Users can grant and deny access to Do Not Disturb configuration from here.

                    Input : Nothing.
                    Output : Nothing.
                    Constant Value : "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"
                */
                // If notification policy access not granted for this package
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }
}
