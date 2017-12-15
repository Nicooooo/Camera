package net.sourceforge.opencamera.UI;


import android.app.Activity;
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
import android.widget.Switch;
import android.widget.TextView;

import net.sourceforge.opencamera.CameraController.CameraController;
import net.sourceforge.opencamera.CameraController.CameraController2;
import net.sourceforge.opencamera.CameraController.CameraControllerException;
import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.MyDebug;
import net.sourceforge.opencamera.PreferenceKeys;
import net.sourceforge.opencamera.Preview.Preview;
import net.sourceforge.opencamera.R;

import java.util.ArrayList;

/**
 * Created by CTI-OBD-RA on 11/23/2017.
 */

public class FaceBeauty extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ArrayList<Integer> mFaceBeautyPropertiesValue = new ArrayList<Integer>();
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
//    private RotateImageView[] mFaceBeautyImageViews = new RotateImageView[NUMBER_FACE_BEAUTY_ICON];
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

    private static final String TAG = "FaceBeauty";
    private OrientationEventListener orientationEventListener;

    private ImageButton btnWB;
    private static MainActivity mainActivity;
    private Preview preview;
    private View v;
    private ImageButton whiten;
    private ImageButton smooth;
    private ImageButton slim;
    private ImageButton enlarge;
    private ImageButton arrow_up;
    private ImageButton arrow_down;
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
    private int mSupportedMaxValue = 0;
    private String current_beauty = null;
    private int whitenLabel;
    private int smoothLabel;
    private int slimLabel;
    private int enlargeLabel;
    private int current_progress;
    private int mCurrentViewIndex = 0;
    private String mEffectsValue = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
        preview = mainActivity.getPreview();
        preview.faces_detected = null;
        int cameraId = preview.applicationInterface.getCameraIdPref();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = null;
        editor = sharedPreferences.edit();
        editor.putString(PreferenceKeys.getBeauty(), "whiten");
        current_beauty = "whiten";

        editor.putString(PreferenceKeys.getFaceDetectionPreferenceKey(), "true");
        Log.d(TAG, "1usingfacedetect1");
        class MyFaceDetectionListener implements CameraController.FaceDetectionListener {
            @Override
            public void onFaceDetection(CameraController.Face[] faces) {
                preview.faces_detected = new CameraController.Face[faces.length];
                System.arraycopy(faces, 0, preview.faces_detected, 0, faces.length);
                Log.d(TAG, "1usingfacedetect2");
            }
        }


        try {
            CameraController.ErrorCallback cameraErrorCallback = new CameraController.ErrorCallback() {
                public void onError() {
                    if( MyDebug.LOG )
                        Log.e(TAG, "error from CameraController: camera device failed");
                    if( preview.camera_controller != null ) {
                        preview.camera_controller = null;
                        preview.applicationInterface.onCameraError();
                    }
                }
            };
            CameraController.ErrorCallback previewErrorCallback = new CameraController.ErrorCallback() {
                public void onError() {
                    if( MyDebug.LOG )
                        Log.e(TAG, "error from CameraController: preview failed to start");
                    preview.applicationInterface.onFailedStartPreview();
                }
            };
            preview.camera_controller = new CameraController2(this.getContext(), cameraId, previewErrorCallback, cameraErrorCallback);
        } catch (CameraControllerException e) {
            e.printStackTrace();
        }


        preview.camera_controller.setFaceDetectionListener(new MyFaceDetectionListener());
        preview.camera_controller.startFaceDetection();
        Log.d(TAG, "1usingfacedetect3" + preview.camera_controller.startFaceDetection());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.facebeauty_indicator, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PreferenceKeys.getProModePreferenceKey(), true);
        editor.apply();

        Log.d(TAG, "facebeauty_indicator");
        whiten = (ImageButton) v.findViewById(R.id.whiten);
        smooth = (ImageButton) v.findViewById(R.id.smooth);
        slim = (ImageButton) v.findViewById(R.id.slim);
        enlarge = (ImageButton) v.findViewById(R.id.enlarge);
//        seekBar_whiten = (SeekBar) v.findViewById(R.id.seekBar_whiten);
//        seekBar_smooth = (SeekBar) v.findViewById(R.id.seekBar_smooth);
//        seekBar_slim = (SeekBar) v.findViewById(R.id.seekBar_slim);
//        seekBar_enlarge = (SeekBar) v.findViewById(R.id.seekBar_enlarge);
        seekBar_faceBeauty = (SeekBar) v.findViewById(R.id.seekBar_faceBeauty);
        layout_whiten = (LinearLayout) v.findViewById(R.id.layout_whiten);
        layout_smooth = (LinearLayout) v.findViewById(R.id.layout_smooth);
        layout_slim = (LinearLayout) v.findViewById(R.id.layout_slim);
        layout_enlarge = (LinearLayout) v.findViewById(R.id.layout_enlarge);
        lbl_whiten = (TextView) v.findViewById(R.id.whiten_label);
        lbl_smooth = (TextView) v.findViewById(R.id.smooth_label);
        lbl_slim = (TextView) v.findViewById(R.id.slim_label);
        lbl_enlarge = (TextView) v.findViewById(R.id.enlarge_label);
        horizontalscrollview = (HorizontalScrollView) v.findViewById(R.id.horizontalscrollview);
        arrow_up = (ImageButton) v.findViewById(R.id.facebeauty_arrow_up);
        arrow_down = (ImageButton) v.findViewById(R.id.facebeauty_arrow_down);

//        whiten.setImageResource(R.drawable.fb_whitening_focus);

        whiten.setOnClickListener(this);
        smooth.setOnClickListener(this);
        slim.setOnClickListener(this);
        enlarge.setOnClickListener(this);
        arrow_up.setOnClickListener(this);
        arrow_down.setOnClickListener(this);

//        seekBar_whiten.setOnSeekBarChangeListener(this);
//        seekBar_smooth.setOnSeekBarChangeListener(this);
//        seekBar_slim.setOnSeekBarChangeListener(this);
//        seekBar_enlarge.setOnSeekBarChangeListener(this);
        seekBar_faceBeauty.setOnSeekBarChangeListener(this);

        Log.d(TAG, "1usingfacedetect4");
//        preview.camera_controller.startFaceDetection();
        return v;
    }

    public void orientationChanged(int rotation) {

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
        if (rotate_by > 181.0f)
            rotate_by -= 360.0f;
        else if (rotate_by < -181.0f)
            rotate_by += 360.0f;
        // view.animate() modifies the view's rotation attribute, so it ends up equivalent to view.setRotation()
        // we use rotationBy() instead of rotation(), so we get the minimal rotation for clockwise vs anti-clockwise
        view.animate().rotationBy(rotate_by).setDuration(35).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Override
    public void onClick(View v) {
        final MainActivity main_activity = (MainActivity) getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
        SharedPreferences.Editor editor = editor = sharedPreferences.edit();

        switch (v.getId()) {
            case R.id.whiten:
                whiten.setImageResource(R.drawable.fb_whitening_focus);
                smooth.setImageResource(R.drawable.fb_smooth_normal);
                slim.setImageResource(R.drawable.fb_sharp_normal);
                enlarge.setImageResource(R.drawable.fb_eye_normal);

                editor.putString(PreferenceKeys.getBeauty(), "whiten");
                current_beauty = "whiten";
                whitenLabel = sharedPreferences.getInt("whiten_key", current_progress);
                seekBar_faceBeauty.setProgress(whitenLabel);
                Log.d(TAG, " wwwwwwwww w" + current_progress + " qwe " + whitenLabel);
                break;

            case R.id.smooth:
                whiten.setImageResource(R.drawable.fb_whitening_normal);
                smooth.setImageResource(R.drawable.fb_smooth_focus);
                slim.setImageResource(R.drawable.fb_sharp_normal);
                enlarge.setImageResource(R.drawable.fb_eye_normal);

                editor.putString(PreferenceKeys.getBeauty(), "smooth");
                current_beauty = "smooth";
                smoothLabel = sharedPreferences.getInt("smooth_key", current_progress);
                seekBar_faceBeauty.setProgress(smoothLabel);
                Log.d(TAG, " wwwwwwwww sm" + current_progress + " qwe " + smoothLabel);

                break;

            case R.id.slim:
                whiten.setImageResource(R.drawable.fb_whitening_normal);
                smooth.setImageResource(R.drawable.fb_smooth_normal);
                slim.setImageResource(R.drawable.fb_sharp_focus);
                enlarge.setImageResource(R.drawable.fb_eye_normal);

                editor.putString(PreferenceKeys.getBeauty(), "slim");
                current_beauty = "slim";
                slimLabel = sharedPreferences.getInt("slim_key", current_progress);
                seekBar_faceBeauty.setProgress(slimLabel);
                Log.d(TAG, " wwwwwwwww s" + current_progress + " qwe " + slimLabel);
                break;

            case R.id.enlarge:
                whiten.setImageResource(R.drawable.fb_whitening_normal);
                smooth.setImageResource(R.drawable.fb_smooth_normal);
                slim.setImageResource(R.drawable.fb_sharp_normal);
                enlarge.setImageResource(R.drawable.fb_eye_focus);
                seekBar_faceBeauty.setProgress(current_progress);

                editor.putString(PreferenceKeys.getBeauty(), "enlarge");
                current_beauty = "enlarge";
                enlargeLabel = sharedPreferences.getInt("enlarge_key", current_progress);
                seekBar_faceBeauty.setProgress(enlargeLabel);
                Log.d(TAG, " wwwwwwwww e" + current_progress + " " + enlargeLabel);
                break;

            case R.id.facebeauty_arrow_up:
                arrow_down.setVisibility(View.VISIBLE);
                arrow_up.setVisibility(View.GONE);
                horizontalscrollview.setVisibility(View.VISIBLE);
                break;

            case R.id.facebeauty_arrow_down:
                arrow_up.setVisibility(View.VISIBLE);
                arrow_down.setVisibility(View.GONE);
                horizontalscrollview.setVisibility(View.GONE);
                break;

            default:
                break;

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        final MainActivity main_activity = (MainActivity) getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(main_activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        current_progress = progress;
//        switch (seekBar.getId()){

//            case R.id.seekBar_faceBeauty:

        current_beauty = sharedPreferences.getString(PreferenceKeys.getBeauty(), current_beauty);
        Log.d(TAG, "progress111 " + current_beauty);
        if (current_beauty.equals("whiten")) {
            Log.d(TAG, "progressw " + " whiten1 " + progress + " / " + current_beauty + " / " + current_progress);
            SharedPreferences sp = main_activity.getSharedPreferences("whiten_pref", Activity.MODE_PRIVATE);
            editor.putInt("whiten_key", progress);
            editor.commit();

        } else if (current_beauty.equals("smooth")) {
            Log.d(TAG, "progressw " + " smooth2 " + progress);
            SharedPreferences sp = main_activity.getSharedPreferences("smooth_pref", Activity.MODE_PRIVATE);
            editor.putInt("smooth_key", progress);
            editor.commit();

        } else if (current_beauty.equals("slim")) {
            Log.d(TAG, "progressw " + " slim3 " + progress);
            SharedPreferences sp = main_activity.getSharedPreferences("slim_pref", Activity.MODE_PRIVATE);
            editor.putInt("slim_key", progress);
            editor.commit();

        } else if (current_beauty.equals("enlarge")) {
            Log.d(TAG, "progressw " + " enlarge4 " + progress);
            SharedPreferences sp = main_activity.getSharedPreferences("enlarge_pref", Activity.MODE_PRIVATE);
            editor.putInt("enlarge_key", progress);
            editor.commit();
        }
//                break;
//        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private void setProgressValue(int value) {
        // because the effects properties list is stored as parameters value
        // so need revert the value
        // but the progress bar is revert the max /min , so not need revert
        seekBar_faceBeauty.setProgress(convertToParamertersValue(value));
    }

    private int convertToParamertersValue(int value) {
        // one:in progress bar,the max value is at the end of left,and current
        // max value is 8;
        // but in our UI,the max value is at the begin of right.
        // two:the parameters supported max value is 4 ,min value is -4
        // above that,the parameters value should be :[native max - current
        // progress value]
        return mSupportedMaxValue - value;
    }




}