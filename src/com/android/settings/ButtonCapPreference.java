/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.SeekBarDialogPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class ButtonCapPreference extends SeekBarDialogPreference implements
        SeekBar.OnSeekBarChangeListener {
    
    private SeekBar mSeekBar;
    private CheckBox mCheckBox;
    private static final int SAVED_VAL_MAX  = 0xFF;
    private static final int SEEK_BAR_EXT   = 40;
    private static final int SEEK_BAR_RANGE = SAVED_VAL_MAX*SEEK_BAR_EXT;
    private int mCurrentValue;

    public ButtonCapPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_dialog_button_autobrightness);
        setDialogIcon(R.drawable.ic_settings_display);
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
        mCurrentValue = progress;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        
        mCheckBox = (CheckBox)view.findViewById(R.id.lowlight_off);
        mCheckBox.setChecked( getPrefValue() < 0 );
        mCurrentValue = Math.abs(getPrefValue()*SEEK_BAR_EXT);

        mSeekBar = getSeekBar(view);
        mSeekBar.setMax(SEEK_BAR_RANGE);
        mSeekBar.setProgress(mCurrentValue);
        mSeekBar.setEnabled(true);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if(positiveResult) {
            /* value will be *-1 if checkbox is active
               we avoid storing 0 */
            int newval = mCurrentValue/SEEK_BAR_EXT;
            newval = ( newval == 0 ? 1 : newval );
            newval = ( mCheckBox.isChecked() ? newval*-1 : newval );
            setPrefValue(newval);
        }
    }


    private void setPrefValue(int val) {
        Settings.System.putInt(getContext().getContentResolver(),
                Settings.System.CAP_BUTTON_BACKLIGHT, val);
    }

    private int getPrefValue() {
        return Settings.System.getInt(getContext().getContentResolver(),
                       Settings.System.CAP_BUTTON_BACKLIGHT, SAVED_VAL_MAX);
    }

}

