package com.ruby.gameing;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private View floatingMenu, floatingIcon;
    private WindowManager.LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // API Level check for overlay type
        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100; params.y = 100;

        // 1. Create Floating Icon (Round Logo)
        floatingIcon = new ImageView(this);
        ((ImageView) floatingIcon).setImageResource(android.R.drawable.ic_menu_help); // Change to your logo later
        
        // 2. Load the Mod Menu UI
        floatingMenu = LayoutInflater.from(this).inflate(R.layout.floating_menu, null);

        // Add Icon to screen
        windowManager.addView(floatingIcon, params);

        // Open Menu on Icon Click
        floatingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(floatingIcon);
                windowManager.addView(floatingMenu, params);
            }
        });

        setupMenuLogic();
        setupDragLogic();
    }

    private void setupMenuLogic() {
        Button btnClose = floatingMenu.findViewById(R.id.btnClose);
        Button tabAim = floatingMenu.findViewById(R.id.tabAim);
        Button tabEsp = floatingMenu.findViewById(R.id.tabEsp);
        Button tabBypass = floatingMenu.findViewById(R.id.tabBypass);
        
        final View layoutAim = floatingMenu.findViewById(R.id.layoutAim);
        final View layoutEsp = floatingMenu.findViewById(R.id.layoutEsp);
        final View layoutBypass = floatingMenu.findViewById(R.id.layoutBypass);

        // Close Button
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(floatingMenu);
                windowManager.addView(floatingIcon, params);
            }
        });

        // Tab Switching Logic
        tabAim.setOnClickListener(v -> { layoutAim.setVisibility(View.VISIBLE); layoutEsp.setVisibility(View.GONE); layoutBypass.setVisibility(View.GONE); });
        tabEsp.setOnClickListener(v -> { layoutAim.setVisibility(View.GONE); layoutEsp.setVisibility(View.VISIBLE); layoutBypass.setVisibility(View.GONE); });
        tabBypass.setOnClickListener(v -> { layoutAim.setVisibility(View.GONE); layoutEsp.setVisibility(View.GONE); layoutBypass.setVisibility(View.VISIBLE); });
    }

    private void setupDragLogic() {
        // Simple drag logic for the icon
        floatingIcon.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY; float initialTouchX, initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x; initialY = params.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                        return false; // Let onClick fire
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingIcon, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingIcon != null) windowManager.removeView(floatingIcon);
        if (floatingMenu != null && floatingMenu.getWindowToken() != null) windowManager.removeView(floatingMenu);
    }
}
