package com.acod.play.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.acod.play.app.Activities.HomescreenActivity;

/**
 * Created by Andrew on 10/21/2014.
 */
public class FloatingControlsView extends View {
    final WindowManager mWinManager;
    private final View mPopupLayout;

    private final ViewGroup mParentView;
    Context context;

    public FloatingControlsView(final Context context, WindowManager.LayoutParams params) {
        super(context);

        this.context = context;


        mWinManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupLayout = inflater.inflate(R.layout.popupcontrols, null);
        mPopupLayout.setVisibility(GONE);
        mParentView = new FrameLayout(context);
        mWinManager.addView(mParentView, params);
        mPopupLayout.setVisibility(GONE);
        setupControls();
    }

    /**
     * Shows view
     */
    public void show(WindowManager.LayoutParams params) {
        mPopupLayout.setLayoutParams(params);
        mParentView.setLayoutParams(params);
        mParentView.removeAllViews();
        mParentView.addView(mPopupLayout);
        mWinManager.updateViewLayout(mParentView, params);
        mPopupLayout.setVisibility(VISIBLE);
    }

    public void setupControls() {
        final PendingIntent stopIntent = PendingIntent.getBroadcast(context, 0, new Intent().setAction(HomescreenActivity.STOP_ACTION), 0);
        final PendingIntent pauseIntent = PendingIntent.getBroadcast(context, 0, new Intent().setAction(HomescreenActivity.PAUSE_ACTION), 0);
        final PendingIntent playIntent = PendingIntent.getBroadcast(context, 0, new Intent().setAction(HomescreenActivity.PLAY_ACTION), 0);
        mPopupLayout.findViewById(R.id.play_pause_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        });
        mPopupLayout.findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        });
        mPopupLayout.findViewById(R.id.play_pause_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pauseIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Hides view
     */
    public void hide() {
        mPopupLayout.setVisibility(GONE);
        mParentView.removeView(mPopupLayout);
    }
}
