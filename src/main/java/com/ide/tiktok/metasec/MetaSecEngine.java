/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.unidbg.AndroidEmulator
 *  com.github.unidbg.Emulator
 *  com.github.unidbg.LibraryResolver
 *  com.github.unidbg.Module
 *  com.github.unidbg.linux.android.AndroidEmulatorBuilder
 *  com.github.unidbg.linux.android.AndroidResolver
 *  com.github.unidbg.linux.android.dvm.AbstractJni
 *  com.github.unidbg.linux.android.dvm.BaseVM
 *  com.github.unidbg.linux.android.dvm.DalvikModule
 *  com.github.unidbg.linux.android.dvm.DvmClass
 *  com.github.unidbg.linux.android.dvm.DvmObject
 *  com.github.unidbg.linux.android.dvm.Jni
 *  com.github.unidbg.linux.android.dvm.StringObject
 *  com.github.unidbg.linux.android.dvm.VM
 *  com.github.unidbg.linux.android.dvm.VaList
 *  com.github.unidbg.linux.android.dvm.VarArg
 *  com.github.unidbg.linux.android.dvm.array.ArrayObject
 *  com.github.unidbg.linux.android.dvm.array.ByteArray
 *  com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean
 *  com.github.unidbg.linux.android.dvm.wrapper.DvmInteger
 *  com.github.unidbg.linux.android.dvm.wrapper.DvmLong
 *  com.github.unidbg.memory.Memory
 */
package com.ide.tiktok.metasec;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.LibraryResolver;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.BaseVM;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.Jni;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.VaList;
import com.github.unidbg.linux.android.dvm.VarArg;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.linux.android.dvm.wrapper.DvmLong;
import com.github.unidbg.memory.Memory;
import java.io.File;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MetaSecEngine
extends AbstractJni
implements AutoCloseable {
    public static final int OP_DECRYPT_STR = 0x1000001;
    public static final int OP_SDK_INIT = 0x1000003;
    public static final int OP_APP_REGISTER = 0x4000001;
    public static final int OP_APP_GET = 0x4000002;
    public static final int OP_SIGN_HTTP = 0x3000001;
    public static final int OP_SIGN_WS = 0x6000001;
    public static final int OP_HANDLER_N2 = 0x2000001;
    public static final int OP_HANDLER_O2 = 0x3000001;
    public static final int OP_SET_DEVICE_ID = 0x2000002;
    public static final int OP_SET_INSTALL_ID = 0x2000003;
    public static final int OP_SET_CHANNEL = 0x2000004;
    public static final int CB_GET_VERSION = 0x1000011;
    public static final int CB_GET_APK_PATH = 0x1000001;
    private static final String DEFAULT_APP_ID = "1233";
    private static final String DEFAULT_APP_VERSION = "46.2.3";
    private static final String DEFAULT_DEVICE_ID = "7123456789012345678";
    private static final String DEFAULT_INSTALL_ID = "7123456789012345679";
    private static final String METASEC_LICENSE = "Zs81WLZ0TYDnPHhokfrih9f2UVKMYsD6ygiN+al3YbsQTM5dSPr+VANPNr2yt/6QZ3JuYhSmDYBLiuwMS3cvrr+1/8cAwLDSGF69eSzBCn1V3+2mBLMP7NPqp1q3bRDYgEewnEm4DNlPCPW5U2nedHmDVvrEKzBMl8RWBPCnBmvNXNMcPonBLsZYQk+XnBO5ZUm5rFr56W5sezAigX8gmCIS9kaPMASRFaEb65F/f8pFvVlEWoxkWkVeUf8Z9pPT0ZaRrezEtk4JvuNwLu0lL1UHdxpc+feECCxfrESn8hKSFDyzg1zQ0YNT+ILxVw34ElmJ30SP74RN/dhjNJ5pDBtlu+RNtU1ebPwnGxtu79WacDkiCJjsmdrzGrGwbQ4nBl8bugOKsWV4FF/60FPwpZ8iZ+M=";
    private static final byte[] A3_SDK_VER_CIPHER = new byte[]{62, 97, 16, 3, 91, 125, 116, 66, 62, 36, 41, 61, 85, 69, 10, 97, 105};
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass nativeBridge;
    private final Path workDir;
    private final DvmObject<?> appContext;
    private final Map<String, String> prefStore = new LinkedHashMap<String, String>();
    private long sessionHandle;
    private volatile boolean ready;
    private volatile boolean sdkInitialized;
    private String sdkVersionStr = "v05.02.09-alpha.3";
    private String deviceId = "7123456789012345678";
    private String installId = "7123456789012345679";

    public MetaSecEngine() throws Exception {
        DalvikModule dm;
        this.workDir = Files.createTempDirectory("tiktok-metasec-", new FileAttribute[0]);
        File metasec = this.materializeResource("native/libmetasec_ov.so", "libmetasec_ov.so");
        File cxx = this.materializeResource("native/libc++_shared.so", "libc++_shared.so");
        this.emulator = (AndroidEmulator)AndroidEmulatorBuilder.for64Bit().setProcessName("com.zhiliaoapp.musically").build();
        Memory memory = this.emulator.getMemory();
        memory.setLibraryResolver((LibraryResolver)new AndroidResolver(23, new String[0]));
        File apk = MetaSecEngine.resolveApk();
        if (apk != null) {
            System.out.println("[metasec] createDalvikVM from APK " + apk.getAbsolutePath());
            this.vm = this.emulator.createDalvikVM(apk);
        } else {
            System.out.println("[metasec] createDalvikVM without APK (set -Dmetasec.apk=...)");
            this.vm = this.emulator.createDalvikVM();
        }
        this.vm.setJni((Jni)this);
        this.vm.setVerbose(Boolean.getBoolean("metasec.verbose"));
        this.appContext = this.vm.resolveClass("android/app/Application", new DvmClass[]{this.vm.resolveClass("android/content/ContextWrapper", new DvmClass[]{this.vm.resolveClass("android/content/Context", new DvmClass[0])})}).newObject(null);
        DvmClass k = this.vm.resolveClass("ms/bd/o/k", new DvmClass[0]);
        DvmClass a0 = this.vm.resolveClass("ms/bd/o/a0", new DvmClass[]{k});
        this.vm.resolveClass("com/bytedance/mobsec/metasec/ov/MS", new DvmClass[]{a0});
        DvmClass b0 = this.vm.resolveClass("ms/bd/o/b0", new DvmClass[0]);
        this.vm.resolveClass("com/bytedance/mobsec/metasec/ov/MSB", new DvmClass[]{b0});
        this.nativeBridge = k;
        this.vm.loadLibrary(cxx, false);
        System.out.println("[metasec] loading libmetasec_ov.so with init_array...");
        try {
            dm = this.vm.loadLibrary(metasec, true);
        }
        catch (Throwable ex) {
            System.out.println("[metasec] loadLibrary(init=true) failed: " + String.valueOf(ex));
            System.out.println("[metasec] retrying without init_array");
            dm = this.vm.loadLibrary(metasec, false);
        }
        this.module = dm.getModule();
        try {
            dm.callJNI_OnLoad((Emulator)this.emulator);
            System.out.println("[metasec] JNI_OnLoad completed");
        }
        catch (Throwable ex) {
            System.out.println("[metasec] JNI_OnLoad incomplete: " + String.valueOf(ex));
        }
        this.bootstrapSdk(DEFAULT_APP_ID, DEFAULT_DEVICE_ID, DEFAULT_INSTALL_ID);
        this.ready = true;
    }

    public void bootstrapSdk(String appId) {
        this.bootstrapSdk(appId, this.deviceId, this.installId);
    }

    public void bootstrapSdk(String appId, String did, String iid) {
        String id;
        String string = id = appId == null || appId.isEmpty() ? DEFAULT_APP_ID : appId;
        if (did != null && !did.isEmpty()) {
            this.deviceId = did;
        }
        if (iid != null && !iid.isEmpty()) {
            this.installId = iid;
        }
        try {
            DvmObject<?> ver = this.callNative(0x1000001, 0, 0L, "936948", (DvmObject<?>)new ByteArray(this.vm, A3_SDK_VER_CIPHER));
            if (ver instanceof StringObject) {
                String decoded = (String)((StringObject)ver).getValue();
                System.out.println("[metasec] decrypted sdkVersionStr=" + decoded);
                if (decoded != null && decoded.startsWith("v") && decoded.length() < 64) {
                    this.sdkVersionStr = decoded;
                }
            } else {
                System.out.println("[metasec] decrypt a3 returned=" + MetaSecEngine.describe(ver));
            }
        }
        catch (Throwable t) {
            System.out.println("[metasec] decrypt a3 failed (using fallback): " + String.valueOf(t));
        }
        System.out.println("[metasec] bootstrap: calling OP_SDK_INIT (0x1000003) with Application context");
        try {
            DvmObject<?> initRet = this.callNative(0x1000003, 0, 0L, null, this.appContext);
            System.out.println("[metasec] OP_SDK_INIT returned=" + MetaSecEngine.describe(initRet) + " handle=0x" + Long.toHexString(this.sessionHandle));
            this.sdkInitialized = true;
        }
        catch (Throwable t) {
            System.out.println("[metasec] OP_SDK_INIT failed: " + String.valueOf(t));
            t.printStackTrace(System.out);
        }
        String configJson = this.buildAppConfigJson(id);
        System.out.println("[metasec] bootstrap: OP_APP_REGISTER (0x4000001) appId=" + id);
        System.out.println("[metasec] config=" + configJson.substring(0, Math.min(120, configJson.length())) + "...");
        try {
            DvmObject<?> reg = this.callNative(0x4000001, 0, 0L, configJson, null);
            System.out.println("[metasec] OP_APP_REGISTER returned=" + MetaSecEngine.describe(reg) + " handle=0x" + Long.toHexString(this.sessionHandle));
        }
        catch (Throwable t) {
            System.out.println("[metasec] OP_APP_REGISTER failed: " + String.valueOf(t));
            t.printStackTrace(System.out);
        }
        if (this.sessionHandle == 0L) {
            System.out.println("[metasec] bootstrap: OP_APP_GET (0x4000002)");
            try {
                DvmObject<?> got = this.callNative(0x4000002, 0, 0L, id, null);
                System.out.println("[metasec] OP_APP_GET returned=" + MetaSecEngine.describe(got));
                if (got instanceof DvmLong) {
                    this.sessionHandle = (Long)((DvmLong)got).getValue();
                } else if (got != null && got.getValue() instanceof Long) {
                    this.sessionHandle = (Long)got.getValue();
                } else if (got != null && got.getValue() instanceof Number) {
                    this.sessionHandle = ((Number)got.getValue()).longValue();
                }
            }
            catch (Throwable t) {
                System.out.println("[metasec] OP_APP_GET failed: " + String.valueOf(t));
                t.printStackTrace(System.out);
            }
        }
        if (this.sessionHandle != 0L) {
            this.bindSessionIdentity(this.deviceId, this.installId, "googleplay");
        }
        this.ready = true;
        if (this.sessionHandle != 0L) {
            // Zenn/metasec: report→marker→warmup sign fattens device prefs / Argus.
            this.warmupSigns();
        }
        System.out.println("[metasec] bootstrap done sdkInitialized=" + this.sdkInitialized + " sessionHandle=0x" + Long.toHexString(this.sessionHandle) + " did=" + this.deviceId + " iid=" + this.installId);
    }

    /** Two throwaway signs after report/marker — mirrors phone cold-start path. */
    private void warmupSigns() {
        String warmUrl = "https://api16-normal-useast5.tiktokv.us/aweme/v1/config/get/"
                + "?aid=1233&device_id=" + this.deviceId + "&iid=" + this.installId
                + "&version_name=" + DEFAULT_APP_VERSION;
        String[] warmHeaders = new String[]{
                "cookie", "store-idc=useast5; tt-target-idc=useast8",
                "user-agent", "com.zhiliaoapp.musically/" + DEFAULT_APP_VERSION
                        + " (Linux; U; Android 12; en_US; Pixel 6; Build/SP2A.220505.002; Cronet/TTNetVersion:unknown)",
                "x-ss-stub", "D41D8CD98F00B204E9800998ECF8427E"
        };
        for (int i = 0; i < 2; ++i) {
            try {
                Map<String, String> warm = this.sign(warmUrl, warmHeaders);
                String argus = warm.getOrDefault("X-Argus", warm.getOrDefault("x-argus", ""));
                System.out.println("[metasec] warmup#" + (i + 1) + " argus_len=" + argus.length()
                        + " headers=" + warm.size());
            } catch (Throwable t) {
                System.out.println("[metasec] warmup#" + (i + 1) + " failed: " + t);
            }
        }
    }

    public void bindSessionIdentity(String did, String iid, String channel) {
        if (did != null && !did.isEmpty()) {
            this.deviceId = "";
        }
        if (iid != null && !iid.isEmpty()) {
            this.installId = "";
        }
        this.ensureIds(did, iid);
        if (channel != null && !channel.isEmpty()) {
            try {
                this.callNative(0x2000004, 0, this.sessionHandle, channel, this.appContext);
                System.out.println("[metasec] OP_SET_CHANNEL ok channel=" + channel);
            }
            catch (Throwable t) {
                System.out.println("[metasec] OP_SET_CHANNEL failed: " + String.valueOf(t));
            }
        }
        try {
            this.callNative(0x2000001, 0, this.sessionHandle, "cold_start", this.appContext);
            System.out.println("[metasec] OP_REPORT(cold_start) ok");
        }
        catch (Throwable t) {
            System.out.println("[metasec] OP_REPORT failed: " + String.valueOf(t));
        }
        try {
            this.callNative(0x2000009, 0, this.sessionHandle, null, null);
            System.out.println("[metasec] OP_0x2000009 ok");
        }
        catch (Throwable t) {
            System.out.println("[metasec] OP_0x2000009 failed: " + String.valueOf(t));
        }
        // Seed reversed MSSDK pref keys seen at sign-time (ids/sequence/chimes).
        // Richer "sdi" (ids) blob encourages a fatter f23-like device submessage.
        String ns = DEFAULT_APP_ID + "-0-0-";
        String idsBlob = "{"
                + "\"device_id\":\"" + (this.deviceId == null ? "" : this.deviceId) + "\","
                + "\"install_id\":\"" + (this.installId == null ? "" : this.installId) + "\","
                + "\"channel\":\"googleplay\","
                + "\"app_version\":\"" + DEFAULT_APP_VERSION + "\","
                + "\"os\":\"Android\",\"os_version\":\"12\","
                + "\"device_type\":\"Pixel 6\",\"device_brand\":\"google\","
                + "\"resolution\":\"1080x2400\",\"dpi\":420,"
                + "\"rom\":\"SP2A.220505.002\",\"host_abi\":\"arm64-v8a\""
                + "}";
        this.prefStore.put(ns + "sdi", idsBlob);
        this.prefStore.putIfAbsent(ns + "ecneuq", "1");
        this.prefStore.putIfAbsent(ns + "semithc", Long.toHexString(System.currentTimeMillis()));
    }

    public synchronized void ensureIds(String did, String iid) {
        boolean changed;
        boolean first;
        if (this.sessionHandle == 0L) {
            throw new IllegalStateException("no session handle");
        }
        if (did != null && !did.isEmpty()) {
            first = this.deviceId == null || this.deviceId.isEmpty();
            changed = !did.equals(this.deviceId);
            this.deviceId = did;
            if (first || changed) {
                try {
                    this.callNative(0x2000002, 0, this.sessionHandle, this.deviceId, this.appContext);
                    System.out.println("[metasec] OP_SET_DEVICE_ID ok did=" + this.deviceId);
                }
                catch (Throwable t) {
                    System.out.println("[metasec] OP_SET_DEVICE_ID failed: " + String.valueOf(t));
                }
            }
        }
        if (iid != null && !iid.isEmpty()) {
            first = this.installId == null || this.installId.isEmpty();
            changed = !iid.equals(this.installId);
            this.installId = iid;
            if (first || changed) {
                try {
                    this.callNative(0x2000003, 0, this.sessionHandle, this.installId, this.appContext);
                    System.out.println("[metasec] OP_SET_INSTALL_ID ok iid=" + this.installId);
                }
                catch (Throwable t) {
                    System.out.println("[metasec] OP_SET_INSTALL_ID failed: " + String.valueOf(t));
                }
            }
        }
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean isSdkInitialized() {
        return this.sdkInitialized;
    }

    public long getSessionHandle() {
        return this.sessionHandle;
    }

    public void setSessionHandle(long handle) {
        this.sessionHandle = handle;
    }

    public synchronized Map<String, String> sign(String url, String[] headers) {
        if (!this.ready) {
            throw new IllegalStateException("engine not ready");
        }
        DvmObject<?> result = this.callNative(0x3000001, 0, this.sessionHandle, url, (DvmObject<?>)new ArrayObject((DvmObject[])this.wrapStrings(headers)));
        return MetaSecEngine.parseHeaderArray(result);
    }

    public Module getModule() {
        return this.module;
    }

    public VM getVm() {
        return this.vm;
    }

    private DvmObject<?> callNative(int opcode, int i2, long handle, String str, DvmObject<?> obj) {
        return this.nativeBridge.callStaticJniMethodObject((Emulator)this.emulator, "a(IIJLjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", new Object[]{opcode, i2, handle, str == null ? null : new StringObject(this.vm, str), obj});
    }

    private String buildAppConfigJson(String appId) {
        String license = MetaSecEngine.jsonEscape(METASEC_LICENSE);
        String ver = MetaSecEngine.jsonEscape(this.sdkVersionStr);
        return "[\"" + MetaSecEngine.jsonEscape(appId) + "\",\"\",\"musical_ly\",\"" + license + "\",\"" + ver + "\",\"googleplay\",\"" + MetaSecEngine.jsonEscape(this.deviceId) + "\",\"\",\"" + MetaSecEngine.jsonEscape(this.installId) + "\",\"\",0,-1,5,[],[]]";
    }

    private static String jsonEscape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String describe(DvmObject<?> obj) {
        if (obj == null) {
            return "null";
        }
        Object v = obj.getValue();
        return obj.getClass().getSimpleName() + "(" + String.valueOf(v) + ")";
    }

    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        DvmObject<?> stub = this.stubStaticField(vm, signature);
        if (stub != null) {
            System.out.println("[metasec] getStaticObjectField " + signature + " => " + String.valueOf(stub.getValue()));
            return stub;
        }
        System.out.println("[metasec] getStaticObjectField UNHANDLED " + signature);
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

    private DvmObject<?> stubStaticField(BaseVM vm, String signature) {
        switch (signature) {
            case "android/os/Build->BRAND:Ljava/lang/String;": 
            case "android/os/Build->MANUFACTURER:Ljava/lang/String;": {
                return new StringObject((VM)vm, "google");
            }
            case "android/os/Build->MODEL:Ljava/lang/String;": 
            case "android/os/Build->DEVICE:Ljava/lang/String;": 
            case "android/os/Build->PRODUCT:Ljava/lang/String;": {
                return new StringObject((VM)vm, "Pixel 6");
            }
            case "android/os/Build->HARDWARE:Ljava/lang/String;": {
                return new StringObject((VM)vm, "oriole");
            }
            case "android/os/Build->FINGERPRINT:Ljava/lang/String;": {
                return new StringObject((VM)vm, "google/oriole/oriole:12/SP2A.220505.002/8353555:user/release-keys");
            }
            case "android/os/Build->DISPLAY:Ljava/lang/String;": 
            case "android/os/Build->ID:Ljava/lang/String;": {
                return new StringObject((VM)vm, "SP2A.220505.002");
            }
            case "android/os/Build->HOST:Ljava/lang/String;": {
                return new StringObject((VM)vm, "abfarm-release");
            }
            case "android/os/Build->TAGS:Ljava/lang/String;": {
                return new StringObject((VM)vm, "release-keys");
            }
            case "android/os/Build->TYPE:Ljava/lang/String;": {
                return new StringObject((VM)vm, "user");
            }
            case "android/os/Build->USER:Ljava/lang/String;": {
                return new StringObject((VM)vm, "android");
            }
            case "android/os/Build$VERSION->RELEASE:Ljava/lang/String;": {
                return new StringObject((VM)vm, "12");
            }
            case "android/os/Build$VERSION->CODENAME:Ljava/lang/String;": {
                return new StringObject((VM)vm, "REL");
            }
            case "android/os/Build$VERSION->SECURITY_PATCH:Ljava/lang/String;": {
                return new StringObject((VM)vm, "2022-05-05");
            }
        }
        return null;
    }

    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "android/os/Build$VERSION->SDK_INT:I": {
                return 31;
            }
        }
        return super.getStaticIntField(vm, dvmClass, signature);
    }

    public void callStaticVoidMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "ms/bd/o/a0->Bill()V": 
            case "ms/bd/o/a0->Louis()V": 
            case "ms/bd/o/a0->Zeoy()V": 
            case "com/bytedance/mobsec/metasec/ov/MS->Bill()V": 
            case "com/bytedance/mobsec/metasec/ov/MS->Louis()V": 
            case "com/bytedance/mobsec/metasec/ov/MS->Zeoy()V": {
                return;
            }
        }
        super.callStaticVoidMethod(vm, dvmClass, signature, varArg);
    }

    public void callStaticVoidMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "ms/bd/o/a0->Bill()V": 
            case "ms/bd/o/a0->Louis()V": 
            case "ms/bd/o/a0->Zeoy()V": 
            case "com/bytedance/mobsec/metasec/ov/MS->Bill()V": 
            case "com/bytedance/mobsec/metasec/ov/MS->Louis()V": 
            case "com/bytedance/mobsec/metasec/ov/MS->Zeoy()V": {
                return;
            }
        }
        super.callStaticVoidMethodV(vm, dvmClass, signature, vaList);
    }

    public void callVoidMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        if (signature.contains("->Bill()V") || signature.contains("->Louis()V") || signature.contains("->Zeoy()V") || signature.contains("->Francies()V")) {
            return;
        }
        super.callVoidMethod(vm, dvmObject, signature, varArg);
    }

    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        DvmObject<?> stub = this.stubStaticObject(vm, signature, varArg);
        if (stub != null || MetaSecEngine.isStubbedStaticObject(signature)) {
            return stub;
        }
        DvmObject<?> handled = this.handleJavaBridge(signature, varArg);
        if (handled != null || MetaSecEngine.isJavaBridge(signature)) {
            return handled;
        }
        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }

    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        DvmObject<?> stub = this.stubStaticObject(vm, signature, (VarArg)vaList);
        if (stub != null || MetaSecEngine.isStubbedStaticObject(signature)) {
            return stub;
        }
        DvmObject<?> handled = this.handleJavaBridge(signature, (VarArg)vaList);
        if (handled != null || MetaSecEngine.isJavaBridge(signature)) {
            return handled;
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    private static boolean isStubbedStaticObject(String signature) {
        return signature.equals("java/lang/Thread->currentThread()Ljava/lang/Thread;") || signature.equals("android/app/ActivityThread->currentActivityThread()Landroid/app/ActivityThread;") || signature.equals("android/app/ActivityThread->currentApplication()Landroid/app/Application;") || signature.equals("android/app/ActivityThread->currentPackageName()Ljava/lang/String;") || signature.equals("java/lang/Boolean->valueOf(Z)Ljava/lang/Boolean;") || signature.equals("java/lang/Integer->valueOf(I)Ljava/lang/Integer;") || signature.equals("java/lang/Long->valueOf(J)Ljava/lang/Long;") || signature.equals("java/lang/System->getProperty(Ljava/lang/String;)Ljava/lang/String;");
    }

    private DvmObject<?> stubStaticObject(BaseVM vm, String signature, VarArg args) {
        switch (signature) {
            case "java/lang/Thread->currentThread()Ljava/lang/Thread;": {
                return vm.resolveClass("java/lang/Thread", new DvmClass[0]).newObject((Object)Thread.currentThread());
            }
            case "android/app/ActivityThread->currentActivityThread()Landroid/app/ActivityThread;": {
                return vm.resolveClass("android/app/ActivityThread", new DvmClass[0]).newObject(null);
            }
            case "android/app/ActivityThread->currentApplication()Landroid/app/Application;": {
                return vm.resolveClass("android/app/Application", new DvmClass[0]).newObject(null);
            }
            case "android/app/ActivityThread->currentPackageName()Ljava/lang/String;": {
                return new StringObject((VM)vm, "com.zhiliaoapp.musically");
            }
            case "java/lang/Boolean->valueOf(Z)Ljava/lang/Boolean;": {
                return DvmBoolean.valueOf((VM)vm, (args.getIntArg(0) != 0 ? 1 : 0) != 0);
            }
            case "java/lang/Integer->valueOf(I)Ljava/lang/Integer;": {
                return DvmInteger.valueOf((VM)vm, (int)args.getIntArg(0));
            }
            case "java/lang/Long->valueOf(J)Ljava/lang/Long;": {
                return DvmLong.valueOf((VM)vm, (long)args.getLongArg(0));
            }
            case "java/lang/System->getProperty(Ljava/lang/String;)Ljava/lang/String;": {
                String k;
                StringObject key = (StringObject)args.getObjectArg(0);
                String v = switch (k = key == null ? "" : (String)key.getValue()) {
                    case "http.agent" -> "Dalvik/2.1.0 (Linux; U; Android 12; Pixel 6 Build/SP2A.220505.002)";
                    case "os.name" -> "Linux";
                    case "os.arch" -> "aarch64";
                    case "os.version" -> "5.10.66-android12";
                    case "java.vm.version" -> "2.1.0";
                    default -> System.getProperty(k);
                };
                return v == null ? null : new StringObject((VM)vm, v);
            }
        }
        return null;
    }

    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        DvmObject<?> stub = this.stubObjectMethod(vm, dvmObject, signature);
        if (stub != null || MetaSecEngine.isStubbedObjectMethod(signature)) {
            return stub;
        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }

    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        DvmObject<?> stub = this.stubObjectMethod(vm, dvmObject, signature);
        if (stub != null || MetaSecEngine.isStubbedObjectMethod(signature)) {
            return stub;
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    private static boolean isStubbedObjectMethod(String signature) {
        return signature.equals("java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;") || signature.equals("java/lang/Thread->getName()Ljava/lang/String;") || signature.equals("java/lang/StackTraceElement->getClassName()Ljava/lang/String;") || signature.equals("java/lang/StackTraceElement->getMethodName()Ljava/lang/String;") || signature.equals("java/lang/StackTraceElement->getFileName()Ljava/lang/String;") || signature.endsWith("->getClassLoader()Ljava/lang/ClassLoader;") || signature.endsWith("->getPackageName()Ljava/lang/String;") || signature.endsWith("->getAbsolutePath()Ljava/lang/String;") || signature.endsWith("->getApplicationContext()Landroid/content/Context;") || signature.endsWith("->getApplicationContext()Landroid/app/Application;") || signature.endsWith("->getFilesDir()Ljava/io/File;") || signature.endsWith("->getCacheDir()Ljava/io/File;") || signature.endsWith("->getDataDir()Ljava/io/File;") || signature.endsWith("->getPackageCodePath()Ljava/lang/String;") || signature.endsWith("->getPackageResourcePath()Ljava/lang/String;");
    }

    private DvmObject<?> stubObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature) {
            case "java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;": {
                DvmClass ste = vm.resolveClass("java/lang/StackTraceElement", new DvmClass[0]);
                return new ArrayObject(new DvmObject[]{ste.newObject((Object)"com.zhiliaoapp.musically.MainActivity#onCreate"), ste.newObject((Object)"com.bytedance.mobsec.metasec.ov.MS#a"), ste.newObject((Object)"ms.bd.o.k#a")});
            }
            case "java/lang/Thread->getName()Ljava/lang/String;": {
                return new StringObject((VM)vm, "main");
            }
            case "java/lang/StackTraceElement->getClassName()Ljava/lang/String;": {
                String raw = String.valueOf(dvmObject == null ? "" : dvmObject.getValue());
                int hash = raw.indexOf(35);
                return new StringObject((VM)vm, hash >= 0 ? raw.substring(0, hash) : raw);
            }
            case "java/lang/StackTraceElement->getMethodName()Ljava/lang/String;": {
                String raw = String.valueOf(dvmObject == null ? "" : dvmObject.getValue());
                int hash = raw.indexOf(35);
                return new StringObject((VM)vm, hash >= 0 ? raw.substring(hash + 1) : "invoke");
            }
            case "java/lang/StackTraceElement->getFileName()Ljava/lang/String;": {
                return new StringObject((VM)vm, "SourceFile");
            }
        }
        if (signature.endsWith("->getClassLoader()Ljava/lang/ClassLoader;")) {
            return vm.resolveClass("java/lang/ClassLoader", new DvmClass[0]).newObject(null);
        }
        if (signature.endsWith("->getPackageName()Ljava/lang/String;") || signature.equals("android/content/Context->getPackageName()Ljava/lang/String;") || signature.equals("android/content/ContextWrapper->getPackageName()Ljava/lang/String;") || signature.equals("android/app/Application->getPackageName()Ljava/lang/String;")) {
            return new StringObject((VM)vm, "com.zhiliaoapp.musically");
        }
        if (signature.endsWith("->getAbsolutePath()Ljava/lang/String;")) {
            return new StringObject((VM)vm, this.workDir.toAbsolutePath().toString());
        }
        if (signature.endsWith("->getApplicationContext()Landroid/content/Context;") || signature.endsWith("->getApplicationContext()Landroid/app/Application;")) {
            return this.appContext;
        }
        if (signature.endsWith("->getFilesDir()Ljava/io/File;") || signature.endsWith("->getCacheDir()Ljava/io/File;") || signature.endsWith("->getDataDir()Ljava/io/File;")) {
            return vm.resolveClass("java/io/File", new DvmClass[0]).newObject((Object)this.workDir.toFile());
        }
        if (signature.endsWith("->getPackageCodePath()Ljava/lang/String;") || signature.endsWith("->getPackageResourcePath()Ljava/lang/String;")) {
            return new StringObject((VM)vm, this.workDir.resolve("base.apk").toAbsolutePath().toString());
        }
        return null;
    }

    private static boolean isJavaBridge(String signature) {
        return signature.startsWith("ms/bd/o/k->b(IIJLjava/lang/String;Ljava/lang/Object;)") || signature.startsWith("com/bytedance/mobsec/metasec/ov/MS->b(IIJLjava/lang/String;Ljava/lang/Object;)");
    }

    private DvmObject<?> handleJavaBridge(String signature, VarArg args) {
        if (!MetaSecEngine.isJavaBridge(signature)) {
            return null;
        }
        int opcode = args.getIntArg(0);
        int i2 = args.getIntArg(1);
        long handle = args.getLongArg(2);
        StringObject str = (StringObject)args.getObjectArg(3);
        DvmObject obj = args.getObjectArg(4);
        System.out.println("[metasec] k.b callback opcode=0x" + Integer.toHexString(opcode) + " i2=" + i2 + " handle=0x" + Long.toHexString(handle) + " str=" + (str == null ? null : (String)str.getValue()) + " obj=" + MetaSecEngine.describe(obj));
        if (handle != 0L && handle != -1L) {
            this.sessionHandle = handle;
            System.out.println("[metasec] captured sessionHandle=0x" + Long.toHexString(handle) + " via opcode 0x" + Integer.toHexString(opcode));
            return DvmBoolean.valueOf((VM)this.vm, (boolean)true);
        }
        if (opcode == 16777279) {
            return DvmBoolean.valueOf((VM)this.vm, (boolean)true);
        }
        if (opcode == 0x1000011) {
            return new StringObject(this.vm, DEFAULT_APP_VERSION);
        }
        if (opcode == 0x1000001) {
            return new StringObject(this.vm, this.workDir.resolve("base.apk").toAbsolutePath().toString());
        }
        // 0x10003 — env/stack probe; native calls getBytes() on the result (must be String, not Boolean/null).
        if (opcode == 0x10003) {
            return new StringObject(this.vm, "");
        }
        if (opcode == 0x1000022) {
            String key = obj instanceof StringObject ? (String)((StringObject)obj).getValue() : "";
            String got = this.prefStore.get(key);
            System.out.println("[metasec] prefs GET key=" + key + " hit=" + (got != null));
            return got == null ? null : new StringObject(this.vm, got);
        }
        if (opcode == 16777251 || opcode == 0x1000023) {
            String packed = str == null ? "" : (String)str.getValue();
            String value = obj instanceof StringObject ? (String)((StringObject)obj).getValue() : "";
            String key = packed;
            int bar = packed.indexOf(124);
            if (bar >= 0 && bar + 1 < packed.length()) {
                key = packed.substring(bar + 1);
            }
            this.prefStore.put(key, value == null ? "" : value);
            System.out.println("[metasec] prefs SET key=" + key + " len=" + (value == null ? 0 : value.length()));
            return null;
        }
        return null;
    }

    private StringObject[] wrapStrings(String[] values) {
        StringObject[] out = new StringObject[values.length];
        for (int i = 0; i < values.length; ++i) {
            out[i] = new StringObject(this.vm, values[i] == null ? "" : values[i]);
        }
        return out;
    }

    private static Map<String, String> parseHeaderArray(DvmObject<?> result) {
        LinkedHashMap<String, String> out = new LinkedHashMap<String, String>();
        if (result == null) {
            return out;
        }
        if (result instanceof ArrayObject) {
            DvmObject[] arr = (DvmObject[])((ArrayObject)result).getValue();
            if (arr != null) {
                int i = 0;
                while (i + 1 < arr.length) {
                    Object v;
                    Object k = arr[i] == null ? null : arr[i].getValue();
                    Object object = v = arr[i + 1] == null ? null : arr[i + 1].getValue();
                    if (k != null && v != null) {
                        out.put(String.valueOf(k), String.valueOf(v));
                    }
                    i += 2;
                }
            }
            return out;
        }
        Object value = result.getValue();
        if (value instanceof Object[]) {
            Object[] arr = (Object[])value;
            int i = 0;
            while (i + 1 < arr.length) {
                out.put(String.valueOf(arr[i]), String.valueOf(arr[i + 1]));
                i += 2;
            }
        }
        return out;
    }

    private File materializeResource(String resourcePath, String fileName) throws Exception {
        Path dest = this.workDir.resolve(fileName);
        try (InputStream in = MetaSecEngine.class.getClassLoader().getResourceAsStream(resourcePath);){
            if (in == null) {
                throw new IllegalStateException("missing classpath resource: " + resourcePath);
            }
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
        return dest.toFile();
    }

    private static File resolveApk() {
        String[] guesses;
        File f;
        String prop = System.getProperty("metasec.apk", "").trim();
        if (!prop.isEmpty() && (f = new File(prop)).isFile()) {
            return f;
        }
        for (String g : guesses = new String[]{System.getenv("METASEC_APK"), "C:\\Users\\anton\\Downloads\\Telegram Desktop\\TikTok_46.2.3.apk"}) {
            File f2;
            if (g == null || g.isEmpty() || !(f2 = new File(g)).isFile()) continue;
            return f2;
        }
        return null;
    }

    @Override
    public void close() {
        try {
            this.emulator.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            Files.walk(this.workDir, new FileVisitOption[0]).sorted((a, b) -> b.compareTo((Path)a)).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            });
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
