package com.example.wifihotspotmodule;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private EditText ssidEditText;
    private EditText passwordEditText;
    private Button applyButton;
    private TextView statusTextView;
    
    // Minimum password length for WiFi passwords
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        ssidEditText = findViewById(R.id.ssidEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        applyButton = findViewById(R.id.applyButton);
        statusTextView = findViewById(R.id.statusTextView);
        
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSsid = ssidEditText.getText().toString().trim();
                String newPassword = passwordEditText.getText().toString().trim();
                
                // Basic validation
                if (newSsid.isEmpty()) {
                    Toast.makeText(MainActivity.this, "SSID cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (newPassword.length() < MIN_PASSWORD_LENGTH) {
                    Toast.makeText(MainActivity.this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Try to apply the changes
                boolean success = applyHotspotChanges(newSsid, newPassword);
                
                if (success) {
                    statusTextView.setText(getString(R.string.success_message));
                } else {
                    statusTextView.setText(getString(R.string.error_message));
                }
            }
        });
    }
    
    /**
     * Applies hotspot configuration changes directly using root permissions
     * Note: The actual implementation will be done by the Xposed module
     * This is just a UI method that shows success/failure
     */
    private boolean applyHotspotChanges(String ssid, String password) {
        // In actual app this would call the method implemented via Xposed
        // but for testing UI we will just return true
        
        try {
            // Check if the config file exists (even though we can't access it directly from here)
            File wifiConfigFile = new File("/data/misc/apexdata/com.android.wifi/WifiConfigStoreSoftAp.xml");
            if (!wifiConfigFile.exists()) {
                return false;
            }
            
            // In production this would call a method provided by our Xposed hook
            boolean success = HotspotManager.updateHotspotConfig(ssid, password);
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
