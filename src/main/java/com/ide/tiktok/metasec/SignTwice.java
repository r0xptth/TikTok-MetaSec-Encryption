/*
 * Decompiled with CFR 0.152.
 */
package com.ide.tiktok.metasec;

import com.ide.tiktok.metasec.MetaSecEngine;
import java.util.Map;

public final class SignTwice {
    public static void main(String[] args) throws Exception {
        System.setProperty("metasec.verbose", "false");
        try (MetaSecEngine engine = new MetaSecEngine();){
            String did = "7123456789012345678";
            String url = "https://api16-normal-useast5.tiktokv.us/aweme/v1/user/profile/self/?aid=1233&device_id=" + did + "&version_name=46.2.3";
            String[] headers = new String[]{"cookie", "store-idc=useast5; tt-target-idc=useast8", "x-ss-stub", "3872C9AE3F427AF0BE0EAD09D07AE2CF", "user-agent", "com.zhiliaoapp.musically/2024602030 (Linux; U; Android 12; en_US; Pixel 6)"};
            for (int i = 1; i <= 3; ++i) {
                Map<String, String> signed = engine.sign(url, headers);
                String argus = signed.getOrDefault("X-Argus", "");
                System.out.println("pass=" + i + " argus_len=" + argus.length() + " keys=" + String.valueOf(signed.keySet()));
            }
        }
    }
}
