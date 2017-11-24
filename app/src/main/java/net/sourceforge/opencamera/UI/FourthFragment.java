package net.sourceforge.opencamera.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.PreferenceKeys;
import net.sourceforge.opencamera.Preview.Preview;
import net.sourceforge.opencamera.R;

/**
 * Created by cti-pdd on 9/7/17.
 */

public class FourthFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "FourthFragment";
    private Preview preview;
    private static MainActivity mainActivity;


    private View v;
    private ImageButton whiten;
    private ImageButton smooth;
    private ImageButton slim;
    private ImageButton enlarge;
    private SeekBar seekBar_whiten;
    private SeekBar seekBar_smooth;
    private SeekBar seekBar_slim;
    private LinearLayout layout_wb_top;
    private SeekBar seekBar_exposure;
    private ImageButton left_pro;
    private ImageButton right_pro;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainActivity = (MainActivity)getActivity();
        preview = mainActivity.getPreview();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fourth_frag, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PreferenceKeys.getProModePreferenceKey(), true);
        editor.apply();

        whiten = (ImageButton)v.findViewById(R.id.whiten);
        smooth = (ImageButton)v.findViewById(R.id.smooth);
        slim = (ImageButton)v.findViewById(R.id.slim);
        enlarge = (ImageButton)v.findViewById(R.id.enlarge);
        seekBar_whiten = (SeekBar)v.findViewById(R.id.seekBar_whiten);
        seekBar_smooth = (SeekBar)v.findViewById(R.id.seekBar_smooth);
        seekBar_slim = (SeekBar)v.findViewById(R.id.seekBar_slim);
        layout_wb_top = (LinearLayout)v.findViewById(R.id.layout_wb_top);
        seekBar_exposure = (SeekBar)v.findViewById(R.id.seekBar_exposure);
        left_pro = (ImageButton)v.findViewById(R.id.left_pro);
        right_pro = (ImageButton)v.findViewById(R.id.right_pro);
//        layout_iso_top = (LinearLayout)v.findViewById(R.id.layout_iso_top);
//        layout_shutter_top = (LinearLayout)v.findViewById(R.id.layout_shutter_top);
//        exposure_label = (TextView)v.findViewById(R.id.exposure_label);
//        saturation_label = (TextView)v.findViewById(R.id.saturation_label);
//        contrast_label = (TextView)v.findViewById(R.id.contrast_label);
//        wb_label = (TextView)v.findViewById(R.id.wb_label);
//        horizontalscrollview = (HorizontalScrollView) v.findViewById(R.id.horizontalscrollview);

       // whiten.setImageResource(R.drawable.whitebalance_pressed);

        whiten.setOnClickListener(this);
        smooth.setOnClickListener(this);
        slim.setOnClickListener(this);
        enlarge.setOnClickListener(this);
        left_pro.setOnClickListener(this);
        right_pro.setOnClickListener(this);

        seekBar_whiten.setOnSeekBarChangeListener(this);
        seekBar_smooth.setOnSeekBarChangeListener(this);
        seekBar_slim.setOnSeekBarChangeListener(this);
//        horizontalscrollview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.d(TAG, " FOCUS_RIGHT " + " scrollX " + scrollX);
//
//                if (scrollX == 0)
//                {
//                    left_pro.setVisibility(View.INVISIBLE);
//                }
//                else if (scrollX == 450)
//                {
//                    right_pro.setVisibility(View.INVISIBLE);
//                }
//                else
//                {
//                    left_pro.setVisibility(View.VISIBLE);
//                    right_pro.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        left_pro.setVisibility(View.INVISIBLE);
        right_pro.setVisibility(View.INVISIBLE);

        return v;
    }

    public static FourthFragment newInstance(String text) {

        FourthFragment f = new FourthFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
