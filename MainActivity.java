package com.ruby.gameing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ye line layout file activity_main.xml ko load karegi
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btnLogin);
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Login button dabane par permission check hogi
                checkOverlayPermission();
            }
        });
    }

    private void checkOverlayPermission() {
        // Android 6.0 (Marshmallow) se upar overlay permission zaroori hai
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please Allow Display Over Other Apps", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, 
                Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 123);
        } else {
            // Agar permission pehle se hai, toh floating service start hogi
            startFloatingMenu();
        }
    }

    private void startFloatingMenu() {
        Intent intent = new Intent(MainActivity.this, FloatingService.class);
        startService(intent);
        // Login screen ko khatam karke game ya home par bhejne ke liye
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startFloatingMenu();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
