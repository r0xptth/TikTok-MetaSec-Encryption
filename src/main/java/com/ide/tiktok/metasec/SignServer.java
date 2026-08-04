/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.fastjson.JSONObject
 *  com.alibaba.fastjson.serializer.SerializerFeature
 */
package com.ide.tiktok.metasec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ide.tiktok.metasec.MetaSecEngine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public final class SignServer {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("metasec.port", args.length > 0 ? args[0] : "5099"));
        System.out.println("[metasec] booting unidbg engine...");
        MetaSecEngine engine = new MetaSecEngine();
        System.out.println("[metasec] JNI_OnLoad ok, module base=0x" + Long.toHexString(engine.getModule().base) + " handle=" + engine.getSessionHandle());
        String bind = System.getProperty("metasec.bind", "0.0.0.0").trim();
        if (bind.isEmpty()) {
            bind = "0.0.0.0";
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(bind, port), 0);
        server.createContext("/health", ex -> SignServer.writeJson(ex, 200, Map.of("ok", true, "ready", engine.isReady(), "session_handle", engine.getSessionHandle())));
        server.createContext("/sign", ex -> SignServer.handleSign(ex, engine));
        server.setExecutor(Executors.newFixedThreadPool(2));
        server.start();
        System.out.println("[metasec] listening on http://" + bind + ":" + port + "/sign");
        Runtime.getRuntime().addShutdownHook(new Thread(engine::close));
    }

    private static void handleSign(HttpExchange ex, MetaSecEngine engine) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            SignServer.writeJson(ex, 405, Map.of("ok", false, "error", "POST required"));
            return;
        }
        try {
            Object url;
            String cookie;
            String data;
            String body = new String(SignServer.readAll(ex.getRequestBody()), StandardCharsets.UTF_8);
            JSONObject req = JSON.parseObject((String)body);
            String params = req.getString("params");
            if (params == null) {
                params = "";
            }
            if ((data = req.getString("data")) == null) {
                data = "";
            }
            if ((cookie = req.getString("cookie")) == null) {
                cookie = "";
            }
            if ((url = req.getString("url")) == null || ((String)url).isEmpty()) {
                Object path;
                String host = req.getString("host");
                if (host == null || host.isEmpty()) {
                    host = "https://api16-normal-useast5.tiktokv.us";
                }
                if ((path = req.getString("path")) == null || ((String)path).isEmpty()) {
                    path = "/";
                }
                Object object = url = host.contains("://") ? host : "https://" + host;
                if (!((String)path).startsWith("/")) {
                    path = "/" + (String)path;
                }
                url = params.startsWith("?") ? (String)url + (String)path + params : (!params.isEmpty() ? (String)url + (String)path + "?" + params : (String)url + (String)path);
            }
            String did = req.getString("device_id");
            String iid = req.getString("install_id");
            if (iid == null || iid.isEmpty()) {
                iid = req.getString("iid");
            }
            if ((did == null || did.isEmpty() || iid == null || iid.isEmpty()) && !params.isEmpty()) {
                String fromParams = params.startsWith("?") ? params.substring(1) : params;
                for (String part : fromParams.split("&")) {
                    int eq = part.indexOf(61);
                    if (eq <= 0) continue;
                    String k = part.substring(0, eq);
                    String v = part.substring(eq + 1);
                    if ((did == null || did.isEmpty()) && ("device_id".equals(k) || "device-id".equals(k))) {
                        did = v;
                        continue;
                    }
                    if (iid != null && !iid.isEmpty() || !"iid".equals(k) && !"install_id".equals(k)) continue;
                    iid = v;
                }
            }
            if (did != null && !did.isEmpty() || iid != null && !iid.isEmpty()) {
                engine.ensureIds(did, iid);
            }
            ArrayList<String> flat = new ArrayList<String>();
            if (!cookie.isEmpty()) {
                flat.add("cookie");
                flat.add(cookie);
            }
            if (!data.isEmpty()) {
                flat.add("x-ss-stub");
                flat.add(SignServer.md5Hex(data.getBytes(StandardCharsets.UTF_8)));
            }
            Map<String, String> signed = engine.sign((String)url, flat.toArray(new String[0]));
            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
            for (Map.Entry<String, String> e : signed.entrySet()) {
                result.put(e.getKey().toLowerCase(Locale.ROOT), e.getValue());
            }
            if (!data.isEmpty() && !result.containsKey("x-ss-stub")) {
                result.put("x-ss-stub", SignServer.md5Hex(data.getBytes(StandardCharsets.UTF_8)));
            }
            LinkedHashMap<String, Object> body = new LinkedHashMap<String, Object>();
            boolean ok = !result.isEmpty();
            body.put("success", ok);
            body.put("ok", ok);
            body.put("result", result);
            SignServer.writeJson(ex, 200, body);
        }
        catch (Exception e) {
            e.printStackTrace();
            LinkedHashMap<String, Object> err = new LinkedHashMap<String, Object>();
            err.put("success", false);
            err.put("ok", false);
            err.put("error", String.valueOf(e));
            SignServer.writeJson(ex, 500, err);
        }
    }

    private static String md5Hex(byte[] data) throws Exception {
        byte[] dig = MessageDigest.getInstance("MD5").digest(data);
        StringBuilder sb = new StringBuilder(dig.length * 2);
        for (byte b : dig) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static byte[] readAll(InputStream in) throws IOException {
        int n;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while ((n = in.read(buf)) >= 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }

    private static void writeJson(HttpExchange ex, int code, Object body) throws IOException {
        byte[] bytes = JSON.toJSONBytes((Object)body, (SerializerFeature[])new SerializerFeature[0]);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody();){
            os.write(bytes);
        }
    }
}
