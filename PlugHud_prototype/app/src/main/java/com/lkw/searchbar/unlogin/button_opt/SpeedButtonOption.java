package com.lkw.searchbar.unlogin.button_opt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.google.android.material.chip.Chip;
import com.lkw.searchbar.R;
import com.lkw.searchbar.unlogin.UnLoginMainActivity;
import com.lkw.searchbar.unlogin.states.SpeedType;

public class SpeedButtonOption implements
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
    public interface SpeedListener {
        void onSpeedTypeChanged(SpeedType speedType);
    }
    private boolean isChipState;
    private SpeedType speedType = SpeedType.ENTIRE;
    private Chip chargeSpeedType;
    private HorizontalScrollView scrollView;
    private RadioButton speedBoxPos;
    private RadioButton speedBoxImps;
    private RadioButton speedBoxEntire;
    private PopupWindow popupWindow;
    private SpeedListener speedListener;
    private UnLoginMainActivity unLoginMainActivity;
    public SpeedButtonOption(
            Chip chargeSpeedType,
            HorizontalScrollView scrollView,
            SpeedListener speedListener,
            UnLoginMainActivity unLoginMainActivity
    ){
        this.scrollView = scrollView;
        this.chargeSpeedType = chargeSpeedType;
        this.speedListener = speedListener;
        this.unLoginMainActivity = unLoginMainActivity;

        chargeSpeedType.setCheckable(true);
        chargeSpeedType.setCheckedIconVisible(false);
        chargeSpeedType.setOnClickListener(this);
    }

    public SpeedType getSpeedType() {
        return speedType;
    }
    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        if(v.getId() != this.chargeSpeedType.getId()){
            return;
        }
        viewOnOptionSpeed(v);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            int id = buttonView.getId();
            switch (id) {
                case R.id.entire_charge:
                    speedType = speedBoxEntire.isChecked() ? SpeedType.ENTIRE : speedType;
                    updateSpeedType(speedType);
                    chargeSpeedType.setText("전체속도");
                    chargeSpeedType.setChecked(SpeedType.ENTIRE == speedType.ENTIRE);
                    break;

                case R.id.char_full:
                    speedType = speedBoxPos.isChecked() ? SpeedType.FULL : speedType;
                    updateSpeedType(speedType);
                    chargeSpeedType.setText("완속");
                    chargeSpeedType.setChecked(SpeedType.FULL == speedType.FULL);
                    break;

                case R.id.char_speed:
                    speedType = speedBoxImps.isChecked() ? SpeedType.FAST : speedType;
                    updateSpeedType(speedType);
                    chargeSpeedType.setText("급속");
                    chargeSpeedType.setChecked(SpeedType.FAST == speedType.FAST);
                    break;
            }
        }
    }

    public void viewOnOptionSpeed(View v){
        int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
        scrollView.scrollTo(maxScrollX, 0);

        LayoutInflater inflater = (LayoutInflater) unLoginMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.unlogin_using_speed, null);
        popupWindow = new PopupWindow(popupView, 250, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);

        speedBoxEntire = popupView.findViewById(R.id.entire_charge);
        speedBoxPos = popupView.findViewById(R.id.char_full);
        speedBoxImps = popupView.findViewById(R.id.char_speed);

        chargeSpeedType.setChecked(isChipState);
        updateSpeedStates(speedType);

        speedBoxEntire.setOnCheckedChangeListener(this);
        speedBoxPos.setOnCheckedChangeListener(this);
        speedBoxImps.setOnCheckedChangeListener(this);

        int offsetX = 15;
        int offsetY = 0;

        popupWindow.showAsDropDown(chargeSpeedType, offsetX, offsetY);
    }

    public void updateSpeedType(SpeedType speedType) {
        if(this.speedType == speedType){
            this.speedListener.onSpeedTypeChanged(this.speedType);
        }
    }
    public void updateSpeedStates(SpeedType speedType){
        speedBoxEntire.setChecked(speedType == SpeedType.ENTIRE);
        speedBoxPos.setChecked(speedType == SpeedType.FULL);
        speedBoxImps.setChecked(speedType == SpeedType.FAST);

        isChipState = speedBoxEntire.isChecked() || speedBoxPos.isChecked() || speedBoxImps.isChecked();

        chargeSpeedType.setChecked(isChipState);
    }
}
