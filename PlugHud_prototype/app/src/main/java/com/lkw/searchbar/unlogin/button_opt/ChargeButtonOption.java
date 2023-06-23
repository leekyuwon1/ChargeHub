package com.lkw.searchbar.unlogin.button_opt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import com.google.android.material.chip.Chip;
import com.lkw.searchbar.R;
import com.lkw.searchbar.unlogin.UnLoginMainActivity;
import com.lkw.searchbar.unlogin.states.ChargeType;
public class ChargeButtonOption implements
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener  {
    public interface TypeListener{
        void onChargeTypeChanged(ChargeType chargeType);
    }
    private boolean isChipState;
    private TypeListener typeListener;
    private PopupWindow popupWindow;
    private ChargeType chargeType = ChargeType.ENTIRE;
    private HorizontalScrollView scrollView;
    private Chip chargeTotalType;
    private RadioButton entireType;
    private RadioButton chargeTypeDc;
    private RadioButton chargeTypeAc;
    private RadioButton chargeTypecomb;
    private RadioButton chargeTypeBtype;
    private RadioButton chargeTypeCtype;
    private RadioButton chargeTypeBCtypeSev;
    private RadioButton chargeTypeBCtypeFiv;
    private RadioButton chargeTypeDCtypeDCComb;
    private RadioButton chargeTypeDC_AC;
    private RadioButton chargeTypeDC_DCComb_AC;
    private UnLoginMainActivity unLoginMainActivity;
    public ChargeButtonOption(
            Chip chargeTotalType,
            HorizontalScrollView scrollView,
            TypeListener typeListener,
            UnLoginMainActivity unLoginMainActivity
    ){
        this.chargeTotalType = chargeTotalType;
        this.scrollView = scrollView;
        this.typeListener = typeListener;
        this.unLoginMainActivity = unLoginMainActivity;

        chargeTotalType.setCheckable(true);
        chargeTotalType.setCheckedIconVisible(false);
        chargeTotalType.setOnClickListener(this);
    }
    public ChargeType getChargeType(){
        return chargeType;
    }

    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        if(v.getId() != this.chargeTotalType.getId()){
            return;
        }
        viewOnClickType();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.unlogin_entire_type:
                    chargeType = entireType.isChecked() ? ChargeType.ENTIRE : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("전체 타입");
                    chargeTotalType.setChecked(ChargeType.ENTIRE == chargeType.ENTIRE);
                    break;

                case R.id.b_type:
                    chargeType = chargeTypeBtype.isChecked() ? ChargeType.B_TYPE : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("B타입(5핀)");
                    chargeTotalType.setChecked(ChargeType.B_TYPE == chargeType.B_TYPE);
                    break;

                case R.id.c_type:
                    chargeType = chargeTypeCtype.isChecked() ? ChargeType.C_TYPE : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("C타입(5핀)");
                    chargeTotalType.setChecked(ChargeType.C_TYPE == chargeType.C_TYPE);
                    break;

                case R.id.bc_type_5:
                    chargeType = chargeTypeBCtypeFiv.isChecked() ? ChargeType.BC_TYPE_FIV : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("BC타입(5핀)");
                    chargeTotalType.setChecked(ChargeType.BC_TYPE_FIV == chargeType.BC_TYPE_FIV);
                    break;

                case R.id.bc_type_7:
                    chargeType = chargeTypeBCtypeSev.isChecked() ? ChargeType.BC_TYPE_SEV : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("BC타입(7핀)");
                    chargeTotalType.setChecked(ChargeType.BC_TYPE_SEV == chargeType.BC_TYPE_SEV);
                    break;

                case R.id.unlogin_speed_dc:
                    chargeType = chargeTypeDc.isChecked() ? ChargeType.DC : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("DC차데모");
                    chargeTotalType.setChecked(ChargeType.DC == chargeType.DC);
                    break;

                case R.id.unlogin_speed_ac:
                    chargeType = chargeTypeAc.isChecked() ? ChargeType.AC : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("AC3상");
                    chargeTotalType.setChecked(ChargeType.AC == chargeType.AC);
                    break;

                case R.id.login_speed_dccomb:
                    chargeType = chargeTypecomb.isChecked() ? ChargeType.DC_COMB : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("DC콤보");
                    chargeTotalType.setChecked(ChargeType.DC_COMB == chargeType.DC_COMB);
                    break;

                case R.id.dc_dccomb:
                    chargeType = chargeTypeDCtypeDCComb.isChecked() ? ChargeType.DC_TYPE_DC_COMB : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("DC차데모 + DC콤보");
                    chargeTotalType.setChecked(ChargeType.DC_TYPE_DC_COMB == chargeType.DC_TYPE_DC_COMB);
                    break;

                case R.id.dc_ac:
                    chargeType = chargeTypeDC_AC.isChecked() ? ChargeType.DC_AC : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("DC차데모 + AC3상");
                    chargeTotalType.setChecked(ChargeType.DC_AC == chargeType.DC_AC);
                    break;

                case R.id.dc_dccomb_ac:
                    chargeType = chargeTypeDC_DCComb_AC.isChecked() ? ChargeType.DC_DC_COMB_AC : chargeType;
                    updateChargeType(chargeType);
                    chargeTotalType.setText("DC차데모 + DC콤보 + AC3상");
                    chargeTotalType.setChecked(ChargeType.DC_DC_COMB_AC == chargeType.DC_DC_COMB_AC);
                    break;
            }
        }
    }
    public void viewOnClickType() {
        int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
        int centerX = maxScrollX / 2;
        scrollView.scrollTo(-centerX, 0);

        LayoutInflater inflater = (LayoutInflater) unLoginMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.unlogin_using_typemenu, null);

        popupWindow = new PopupWindow(popupView, 330, 600);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);

        entireType = popupView.findViewById(R.id.unlogin_entire_type);
        chargeTypeBtype = popupView.findViewById(R.id.b_type);
        chargeTypeCtype = popupView.findViewById(R.id.c_type);
        chargeTypeBCtypeFiv = popupView.findViewById(R.id.bc_type_5);
        chargeTypeBCtypeSev = popupView.findViewById(R.id.bc_type_7);
        chargeTypeDc = popupView.findViewById(R.id.unlogin_speed_dc);
        chargeTypeAc = popupView.findViewById(R.id.unlogin_speed_ac);
        chargeTypecomb = popupView.findViewById(R.id.login_speed_dccomb);
        chargeTypeDCtypeDCComb = popupView.findViewById(R.id.dc_dccomb);
        chargeTypeDC_AC = popupView.findViewById(R.id.dc_ac);
        chargeTypeDC_DCComb_AC = popupView.findViewById(R.id.dc_dccomb_ac);

        chargeTotalType.setChecked(isChipState);
        updateTypeStates(chargeType);

        entireType.setOnCheckedChangeListener(this);
        chargeTypeBtype.setOnCheckedChangeListener(this);
        chargeTypeCtype.setOnCheckedChangeListener(this);
        chargeTypeBCtypeFiv.setOnCheckedChangeListener(this);
        chargeTypeBCtypeSev.setOnCheckedChangeListener(this);
        chargeTypeDc.setOnCheckedChangeListener(this);
        chargeTypeAc.setOnCheckedChangeListener(this);
        chargeTypecomb.setOnCheckedChangeListener(this);
        chargeTypeDCtypeDCComb.setOnCheckedChangeListener(this);
        chargeTypeDC_AC.setOnCheckedChangeListener(this);
        chargeTypeDC_DCComb_AC.setOnCheckedChangeListener(this);

        popupWindow.showAsDropDown(chargeTotalType);
    }

    public void updateChargeType(ChargeType chargeType) {
        if (this.chargeType == chargeType) {
            this.typeListener.onChargeTypeChanged(this.chargeType);
            System.out.println("테스트");
        }
    }

    public void updateTypeStates(ChargeType chargeType) {
        entireType.setChecked(chargeType == ChargeType.ENTIRE);
        chargeTypeBtype.setChecked(chargeType == ChargeType.B_TYPE);
        chargeTypeCtype.setChecked(chargeType == ChargeType.C_TYPE);
        chargeTypeBCtypeFiv.setChecked(chargeType == ChargeType.BC_TYPE_FIV);
        chargeTypeBCtypeSev.setChecked(chargeType == ChargeType.BC_TYPE_SEV);
        chargeTypeDc.setChecked(chargeType == ChargeType.DC);
        chargeTypeAc.setChecked(chargeType == ChargeType.AC);
        chargeTypecomb.setChecked(chargeType == ChargeType.DC_COMB);
        chargeTypeDCtypeDCComb.setChecked(chargeType == ChargeType.DC_TYPE_DC_COMB);
        chargeTypeDC_AC.setChecked(chargeType == ChargeType.DC_AC);
        chargeTypeDC_DCComb_AC.setChecked(chargeType == ChargeType.DC_DC_COMB_AC);

        isChipState = entireType.isChecked()
                || chargeTypeBtype.isChecked()
                || chargeTypeCtype.isChecked()
                || chargeTypeBCtypeFiv.isChecked()
                || chargeTypeBCtypeSev.isChecked()
                || chargeTypeDc.isChecked()
                || chargeTypeAc.isChecked()
                || chargeTypecomb.isChecked()
                || chargeTypeDCtypeDCComb.isChecked()
                || chargeTypeDC_AC.isChecked()
                || chargeTypeDC_DCComb_AC.isChecked();

        chargeTotalType.setChecked(isChipState);
    }
}
