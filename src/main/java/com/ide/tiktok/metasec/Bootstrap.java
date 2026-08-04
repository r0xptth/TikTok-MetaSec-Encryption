/*
 * Decompiled with CFR 0.152.
 */
package com.ide.tiktok.metasec;

import com.ide.tiktok.metasec.MetaSecEngine;
import java.util.Base64;
import java.util.Map;

public final class Bootstrap {
    public static void main(String[] args) throws Exception {
        System.setProperty("metasec.verbose", System.getProperty("metasec.verbose", "false"));
        try (MetaSecEngine engine = new MetaSecEngine();){
            System.out.println("ready=" + engine.isReady() + " sdkInit=" + engine.isSdkInitialized() + " base=0x" + Long.toHexString(engine.getModule().base) + " size=0x" + Long.toHexString(engine.getModule().size) + " handle=0x" + Long.toHexString(engine.getSessionHandle()));
            String deviceId = "7123456789012345678";
            String url = "https://api16-normal-useast5.tiktokv.us/passport/email/register_verify_login/?aid=1233&device_id=" + deviceId + "&version_name=46.2.3";
            String[] headers = new String[]{"user-agent", "com.zhiliaoapp.musically/2024602030 (Linux; U; Android 12; en_US; Pixel 6; Build/SP2A.220505.002; Cronet/TTNetVersion:xxx)", "cookie", "store-idc=useast5; tt-target-idc=useast8", "x-ss-stub", "3872C9AE3F427AF0BE0EAD09D07AE2CF", "x-ss-req-ticket", String.valueOf(System.currentTimeMillis())};
            try {
                Map<String, String> signed = engine.sign(url, headers);
                System.out.println("sign_result=" + String.valueOf(signed));
                String argus = signed.getOrDefault("X-Argus", signed.getOrDefault("x-argus", ""));
                System.out.println("x-argus_len=" + argus.length());
                if (!argus.isEmpty()) {
                    byte[] raw = Base64.getDecoder().decode(argus);
                    StringBuilder hex = new StringBuilder();
                    for (int i = 0; i < Math.min(8, raw.length); ++i) {
                        hex.append(String.format("%02x", raw[i]));
                    }
                    System.out.println("x-argus_prefix=" + String.valueOf(hex) + " raw_len=" + raw.length);
                }
            }
            catch (Throwable t) {
                System.out.println("sign_failed: " + String.valueOf(t));
                t.printStackTrace(System.out);
            }
        }
    }
}
