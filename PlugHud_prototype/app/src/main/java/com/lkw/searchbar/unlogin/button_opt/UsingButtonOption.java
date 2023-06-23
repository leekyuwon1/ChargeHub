package com.lkw.searchbar.unlogin.button_opt;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;

import com.google.android.material.chip.Chip;
import com.lkw.searchbar.unlogin.UnLoginMainActivity;
import com.lkw.searchbar.unlogin.charge_controller.DatabaseCharge;
import com.lkw.searchbar.unlogin.states.UsingType;

import net.daum.mf.map.api.MapView;

import java.util.Map;

public class UsingButtonOption implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    public interface Listener {
        void onUsingTypeChanged(UsingType using);
    }

    private UsingType usingType = UsingType.ENTIRE;
    private Chip chargeUsingType;
    private HorizontalScrollView scrollView;
    private Listener listener;

    public UsingButtonOption(
        Chip chargeUsingType,
        HorizontalScrollView scrollView,
        Listener listener
    ) {
        this.chargeUsingType = chargeUsingType;
        this.scrollView = scrollView;
        this.listener = listener;

        chargeUsingType.setCheckable(true);
        chargeUsingType.setCheckedIconVisible(false);
        chargeUsingType.setOnCheckedChangeListener(this);
        chargeUsingType.setOnClickListener(this);
    }

    public UsingType getUsingType() {
        return usingType;
    }

    @Override
    public void onClick(View v) {
        if(v == null) {
            return;
        }
        if(v.getId() != this.chargeUsingType.getId()) {
            return;
        }
        viewOnOptionUsing(v);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateUsing(isChecked ? UsingType.USING : UsingType.ENTIRE);
    }

    public void viewOnOptionUsing(View v){
        int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
        scrollView.scrollTo(-maxScrollX, 0);
        usingType = chargeUsingType.isChecked() ? UsingType.USING : UsingType.ENTIRE;
        updateUsing(usingType);
    }

    public void updateUsing(UsingType usingType) {
        chargeUsingType.setChecked(usingType == UsingType.USING);
        chargeUsingType.setText(usingType == UsingType.USING? "사용가능" : "전체");

        if(this.usingType != usingType) {
            this.usingType = usingType;
            this.listener.onUsingTypeChanged(this.usingType);
        }
    }
}
