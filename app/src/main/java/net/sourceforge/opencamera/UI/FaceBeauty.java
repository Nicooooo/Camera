package net.sourceforge.opencamera.UI;


import android.content.SharedPreferences;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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
 * Created by CTI-OBD-RA on 11/23/2017.
 */

public class FaceBeauty extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private static final int FACE_BEAUTY_WRINKLE_REMOVE = 0;
    private static final int FACE_BEAUTY_WHITENING = 1;
    private static final int FACE_BEAUTY_BEAUTY_SHAPE = 2; // here shape means
    // slim
    private static final int FACE_BEAUTY_BIG_EYES_NOSE = 3;
    private static final int FACE_BEAUTY_MODIFY_ICON = 4;
    private static final int FACE_BEAUTY_ICON = 5;

    // use for hander the effects item hide
    private static final int DISAPPEAR_VFB_UI_TIME = 5000;
    private static final int DISAPPEAR_VFB_UI = 0;
    // Decoupling: 4 will be replaced by parameters
    private int SUPPORTED_FB_EFFECTS_NUMBER = 0;

    // 6 means all the number of icons in the preview
    private static final int NUMBER_FACE_BEAUTY_ICON = 6;
    private static final int SUPPORTED_FB_PROPERTIES_MIN_NUMBER = 2;
    private static final int SUPPORTED_FB_PROPERTIES_MAX_NUMBER = 4;

    private static final int[] FACE_BEAUTY_ICONS_NORMAL = new int[NUMBER_FACE_BEAUTY_ICON];
    private static final int[] FACE_BEAUTY_ICONS_HIGHTLIGHT = new int[NUMBER_FACE_BEAUTY_ICON];

    static {
        FACE_BEAUTY_ICONS_NORMAL[FACE_BEAUTY_WRINKLE_REMOVE] = R.drawable.fb_smooth_normal;
        FACE_BEAUTY_ICONS_NORMAL[FACE_BEAUTY_WHITENING] = R.drawable.fb_whitening_normal;
        FACE_BEAUTY_ICONS_NORMAL[FACE_BEAUTY_BEAUTY_SHAPE] = R.drawable.fb_sharp_normal;
        FACE_BEAUTY_ICONS_NORMAL[FACE_BEAUTY_BIG_EYES_NOSE] = R.drawable.fb_eye_normal;
        FACE_BEAUTY_ICONS_NORMAL[FACE_BEAUTY_MODIFY_ICON] = R.drawable.fb_setting_normal;
        FACE_BEAUTY_ICONS_NORMAL[FACE_BEAUTY_ICON] = R.drawable.ic_mode_facebeauty_normal;
    }

    static {
        FACE_BEAUTY_ICONS_HIGHTLIGHT[FACE_BEAUTY_WRINKLE_REMOVE] = R.drawable.fb_smooth_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[FACE_BEAUTY_WHITENING] = R.drawable.fb_whitening_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[FACE_BEAUTY_BEAUTY_SHAPE] = R.drawable.fb_sharp_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[FACE_BEAUTY_BIG_EYES_NOSE] = R.drawable.fb_eye_presse;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[FACE_BEAUTY_MODIFY_ICON] = R.drawable.fb_setting_pressed;
        FACE_BEAUTY_ICONS_HIGHTLIGHT[FACE_BEAUTY_ICON] = R.drawable.ic_mode_facebeauty_focus;
    }

    private static final String TAG = "FirstFragment";
    private OrientationEventListener orientationEventListener;

    private ImageButton btnWB;
    private static MainActivity mainActivity;
    private Preview preview;
    private View v;
    private ImageButton whiten;
    private ImageButton smooth;
    private ImageButton slim;
    private ImageButton enlarge;
    private SeekBar seekBar_faceBeauty;
    private SeekBar seekBar_whiten;
    private SeekBar seekBar_smooth;
    private SeekBar seekBar_slim;
    private SeekBar seekBar_enlarge;
    private LinearLayout layout_whiten;
    private LinearLayout layout_smooth;
    private LinearLayout layout_slim;
    private LinearLayout layout_enlarge;
    private TextView lbl_whiten;
    private TextView lbl_smooth;
    private TextView lbl_slim;
    private TextView lbl_enlarge;
    private HorizontalScrollView horizontalscrollview;
    private boolean isArrownShown = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity)getActivity();
        preview = mainActivity.getPreview();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.facebeauty_indicator, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PreferenceKeys.getProModePreferenceKey(), true);
        editor.apply();

        whiten = (ImageButton) v.findViewById(R.id.whiten);
        smooth = (ImageButton) v.findViewById(R.id.smooth);
        slim = (ImageButton) v.findViewById(R.id.slim);
        enlarge = (ImageButton) v.findViewById(R.id.enlarge);
        seekBar_whiten = (SeekBar) v.findViewById(R.id.seekBar_whiten);
        seekBar_smooth = (SeekBar) v.findViewById(R.id.seekBar_smooth);
        seekBar_slim = (SeekBar) v.findViewById(R.id.seekBar_slim);
        seekBar_enlarge = (SeekBar) v.findViewById(R.id.seekBar_enlarge);
        seekBar_faceBeauty = (SeekBar) v.findViewById(R.id.seekBar_faceBeauty);
        layout_whiten = (LinearLayout) v.findViewById(R.id.layout_whiten);
        layout_smooth= (LinearLayout) v.findViewById(R.id.layout_smooth);
        layout_slim= (LinearLayout) v.findViewById(R.id.layout_slim);
        layout_enlarge= (LinearLayout) v.findViewById(R.id.layout_enlarge);
        lbl_whiten = (TextView) v.findViewById(R.id.whiten_label);
        lbl_smooth = (TextView) v.findViewById(R.id.smooth_label);
        lbl_slim = (TextView) v.findViewById(R.id.slim_label);
        lbl_enlarge = (TextView) v.findViewById(R.id.enlarge_label);
        horizontalscrollview = (HorizontalScrollView) v.findViewById(R.id.horizontalscrollview);

        whiten.setImageResource(R.drawable.fb_whitening_focus);

        whiten.setOnClickListener(this);
        smooth.setOnClickListener(this);
        slim.setOnClickListener(this);
        enlarge.setOnClickListener(this);

        seekBar_whiten.setOnSeekBarChangeListener(this);
        seekBar_smooth.setOnSeekBarChangeListener(this);
        seekBar_slim.setOnSeekBarChangeListener(this);
        seekBar_enlarge.setOnSeekBarChangeListener(this);
        seekBar_faceBeauty.setOnSeekBarChangeListener(this);

        return v;
    }

    public void orientationChanged(int rotation){

        View view = v.findViewById(R.id.layout_whiten);
        setViewRotation(view, rotation);
        view = v.findViewById(R.id.layout_smooth);
        setViewRotation(view, rotation);
        view = v.findViewById(R.id.layout_slim);
        setViewRotation(view, rotation);
        view = v.findViewById(R.id.layout_enlarge);
        setViewRotation(view, rotation);

    }

    public void setViewRotation(View view, float ui_rotation) {
        //view.setRotation(ui_rotation);
        Log.d(TAG, " setViewRotation ");
        float rotate_by = ui_rotation - view.getRotation();
        if( rotate_by > 181.0f )
            rotate_by -= 360.0f;
        else if( rotate_by < -181.0f )
            rotate_by += 360.0f;
        // view.animate() modifies the view's rotation attribute, so it ends up equivalent to view.setRotation()
        // we use rotationBy() instead of rotation(), so we get the minimal rotation for clockwise vs anti-clockwise
        view.animate().rotationBy(rotate_by).setDuration(35).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.whiten:

                break;
            case R.id.smooth:

                break;
            case R.id.slim:

                break;
            case R.id.enlarge:

                break;

            default:
                break;

        }

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
