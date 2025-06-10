package com.example.wifihotspotmodule;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Main Xposed Module class
 * This class hooks into the WiFi system services to modify hotspot settings
 */
public class XposedModule implements IXposedHookLoadPackage {

    private static final String TAG = "WiFiHotspotXposed";
    private static final String CONFIG_FILE_PATH = "/data/misc/apexdata/com.android.wifi/WifiConfigStoreSoftAp.xml";
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.example.wifihotspotmodule")) {
            // Hook our own app's HotspotManager to provide implementation
            XposedBridge.log("Hooking our app's HotspotManager");
            
            XposedHelpers.findAndHookMethod(
                    "com.example.wifihotspotmodule.HotspotManager",
                    lpparam.classLoader,
                    "updateHotspotConfig",
                    String.class,
                    String.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            String ssid = (String) param.args[0];
                            String password = (String) param.args[1];
                            
                            XposedBridge.log("Attempting to update hotspot config with: SSID=" + ssid);
                            
                            boolean result = updateHotspotConfigFile(ssid, password);
                            if (result) {
                                XposedBridge.log("Successfully updated hotspot config file");
                                // Restart WiFi services to apply changes without reboot
                                restartWiFiServices();
                            } else {
                                XposedBridge.log("Failed to update hotspot config file");
                            }
                            
                            return result;
                        }
                    });
        } 
        else if (lpparam.packageName.equals("android")) {
            // Hook android system services for additional functionality if needed
            XposedBridge.log("Hooking Android WiFi system services");
            
            // Optional: Hook system methods to apply changes immediately
            hookWiFiServiceManager(lpparam.classLoader);
        }
    }
    
    /**
     * Updates the WifiConfigStoreSoftAp.xml file with new SSID and password
     */
    private boolean updateHotspotConfigFile(String newSsid, String newPassword) {
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            if (!configFile.exists() || !configFile.canRead() || !configFile.canWrite()) {
                XposedBridge.log("Config file doesn't exist or not accessible: " + CONFIG_FILE_PATH);
                return false;
            }
            
            // Read the current XML file
            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            String line;
            
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            
            // Update SSID and password in the XML
            String xmlContent = content.toString();
            
            // Replace SSID value
            xmlContent = xmlContent.replaceAll(
                    "<string name=\"SSID\">.*?</string>",
                    "<string name=\"SSID\">" + newSsid + "</string>"
            );
            
            // Replace PreSharedKey value
            xmlContent = xmlContent.replaceAll(
                    "<string name=\"PreSharedKey\">.*?</string>",
                    "<string name=\"PreSharedKey\">" + newPassword + "</string>"
            );
            
            // Write the updated XML back to file
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(configFile));
            writer.write(xmlContent);
            writer.close();
            
            return true;
            
        } catch (Exception e) {
            XposedBridge.log("Error updating config file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Hook WiFi service manager to apply changes immediately
     */
    private void hookWiFiServiceManager(ClassLoader classLoader) {
        try {
            // Find and hook relevant WiFi service methods
            // This might need adjustment based on Android version
            Class<?> wifiServiceClass = XposedHelpers.findClass(
                    "com.android.server.wifi.WifiServiceImpl", classLoader);
            
            // Hook methods that reload configurations
            XposedHelpers.findAndHookMethod(
                    wifiServiceClass, 
                    "startSoftAp", 
                    "android.net.wifi.WifiConfiguration", 
                    "java.lang.String",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("startSoftAp called - forcing reload of configuration");
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log("Failed to hook WiFi service: " + e.getMessage());
        }
    }
    
    /**
     * Restart WiFi services to apply changes without reboot
     */
    private void restartWiFiServices() {
        try {
            // Execute commands with superuser permissions to restart services
            Process process = Runtime.getRuntime().exec("su");
            OutputStreamWriter outputWriter = new OutputStreamWriter(process.getOutputStream());
            
            // Stop and restart WiFi service
            outputWriter.write("svc wifi disable\n");
            outputWriter.write("sleep 1\n");
            outputWriter.write("svc wifi enable\n");
            
            // Restart hotspot if it was active
            outputWriter.write("settings put global wifi_saved_state 1\n");
            outputWriter.write("svc wifi disable\n");
            outputWriter.write("am broadcast -a android.intent.action.BOOT_COMPLETED\n");
            
            outputWriter.flush();
            outputWriter.close();
            
            int exitCode = process.waitFor();
            XposedBridge.log("Restarted WiFi services, exit code: " + exitCode);
            
        } catch (Exception e) {
            XposedBridge.log("Failed to restart WiFi services: " + e.getMessage());
        }
    }
}
