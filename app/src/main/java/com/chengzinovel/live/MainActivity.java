package com.chengzinovel.live;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.baidu.mobads.SplashLpCloseListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends Activity {

    /**
     * Android6.0以上的权限适配简单示例：
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE))) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            lackedPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (lackedPermission.size() == 0) {
            // 权限都已经有了，那么直接调用SDK
            fetchSplashAD();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1000);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && hasAllPermissionsGranted(grantResults)) {
            fetchSplashAD();
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    private void fetchSplashAD() {
        // 默认请求http广告，若需要请求https广告，请设置AdSettings.setSupportHttps为true
        // AdSettings.setSupportHttps(true);
        // 设置视频广告最大缓存占用空间(15MB~100MB),默认30MB,单位MB
        // SplashAd.setMaxVideoCacheCapacityMb(30);
        // adUnitContainer
        RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);

        // 不需要使用lp关闭之后回调方法的，可以继续使用该接口
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                Log.i("RSplashActivity", "onAdDismissed");
                jumpWhenCanClick(); // 跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i("RSplashActivity", "onAdFailed" + arg0);
                jump();
            }

            @Override
            public void onAdPresent() {
                Log.i("RSplashActivity", "onAdPresent");
            }

            @Override
            public void onAdClick() {
                Log.i("RSplashActivity", "onAdClick");
                // 设置开屏可接受点击时，该回调可用
            }
        };
        // 增加lp页面关闭回调，不需要该回调的继续使用原来接口就可以
//        SplashLpCloseListener listener = new SplashLpCloseListener() {
//            @Override
//            public void onLpClosed() {
//                Toast.makeText(MainActivity.this,"lp页面关闭",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdDismissed() {
//                Log.i("RSplashActivity", "onAdDismissed");
////                jumpWhenCanClick(); // 跳转至您的应用主界面
//            }
//
//            @Override
//            public void onAdFailed(String arg0) {
//                Log.i("RSplashActivity", "onAdFailed"+arg0);
////                jump();
//            }
//
//            @Override
//            public void onAdPresent() {
//                Log.i("RSplashActivity", "onAdPresent");
//            }
//
//            @Override
//            public void onAdClick() {
//                Log.i("RSplashActivity", "onAdClick");
//                // 设置开屏可接受点击时，该回调可用
//            }
//        };
        AdSettings.setSupportHttps(false);
        String adPlaceId = "6167716"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        // 如果开屏需要支持vr,needRequestVRAd(true)
//        SplashAd.needRequestVRAd(true);
        // 等比缩小放大，裁剪边缘部分
//        SplashAd.setBitmapDisplayMode(BitmapDisplayMode.DISPLAY_MODE_CENTER_CROP);
        new SplashAd(this, adsParent, listener, adPlaceId, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] bytes = new byte[instance.getBlockSize()];
            String sc="ES4tPFqZ6L/hateTQI4Yv5ZuZWhgjpUs0TxvpSalgqM=";
            byte[] decode = Base64.decode(sc, Base64.DEFAULT);
            // iv
            byte[] temp=new byte[16];
            System.arraycopy(decode,0,temp,0,16);
            IvParameterSpec IV = new IvParameterSpec(temp);
            // sk
            SecretKeySpec secretKeySpec=new SecretKeySpec("bagJaySTW8W9WgCawBn2WX2FV3LdfUS8".getBytes(),"AES");

            instance.init(Cipher.ENCRYPT_MODE, secretKeySpec, IV);
            String temp2 = "zhangsan007";
            byte[] doFinal = instance.doFinal(temp2.getBytes());
            String a = new String(doFinal);
            Log.e("temp==============",a);
            Log.e("temp==============",doFinal.length+"");
            byte[] encode = Base64.encode(doFinal,Base64.DEFAULT);
            Log.e("temp==============", new String(encode));


//            Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            byte[] bytes = new byte[instance.getBlockSize()];
//            Random random = new Random();
//            System.out.println("----------------");
//            String sc="ES4tPFqZ6L/hateTQI4Yv5ZuZWhgjpUs0TxvpSalgqM=";
//            byte[] decode = Base64.decode(sc, Base64.DEFAULT);
//            byte[] temp=new byte[16];
////            for (int i=0;i<decode.length;i++){
////                Log.e("decode",decode[i]+"");
////            }
//            System.arraycopy(decode,0,temp,0,16);
//
////            for (int i=0;i<temp.length;i++){
////                Log.e("temp",decode[i]+"");
////            }
//
//            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec IV = new IvParameterSpec(temp);
//            byte[] temp2 = new byte[16];
//            System.arraycopy(decode,16,temp2,0,16);
//
//
//            SecretKeySpec secretKeySpec=new SecretKeySpec("bagJaySTW8W9WgCawBn2WX2FV3LdfUS8".getBytes(),"AES");
//            c.init(Cipher.DECRYPT_MODE, secretKeySpec, IV);
//
//            for (int i=0;i<temp2.length;i++){
//                Log.e("temp2",temp2[i]+"");
//            }
//
//
//
//            byte[] doFinal = c.doFinal(temp2);
//
//            for (int i=0;i<doFinal.length;i++){
//                Log.e("doFinal",doFinal[i]+"");
//            }
//            Log.e("string",new String(doFinal));
//
////            random.nextBytes(bytes);
//            IvParameterSpec ivParameterSpec = new IvParameterSpec(bytes);
//
//            String s = Base64.encodeToString(bytes, Base64.DEFAULT);
//            System.out.println("ivParameterSpec=="+new String(bytes,StandardCharsets.UTF_8));
//            byte[] test = new String(bytes, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
////            System.out.println("ivParameterSpec=="+new String(bytes).getBytes());
//            for (int i=0;i<test.length;i++){
//                Log.e("bytes",test[i]+"");
//            }
//            Log.e("Base64", s);
            System.out.println("----------------");


            String base64 = Base64.encodeToString(new String(bytes, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            System.out.println("base64==" + base64);
        } catch (NoSuchAlgorithmException e) {
            Log.e("ivParameterSpec2", e.getMessage());
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            Log.e("ivParameterSpec3", e.getMessage());
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


        PackageInfo info;
        int targetVersion = 0;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            targetVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 如果targetSDKVersion >= 23, 并且运行环境在Android6.0版本之上, 就要申请好权限。
        // 否则, 只需要在这里直接调用fetchSplashAD接口
        if (Build.VERSION.SDK_INT >= 23 && targetVersion >= 23) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器, 或者targetSDKVersion < 23，默认在安装时获得了所有权限，可以直接调用SDK
            fetchSplashAD();
        }
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中
     * 调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        Log.d("test", "this.hasWindowFocus():" + this.hasWindowFocus());
        if (canJumpImmediately) {
            this.startActivity(new Intent(MainActivity.this, MainActivity.class));
            this.finish();
        } else {
            canJumpImmediately = true;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
//        this.startActivity(new Intent(MainActivity.this, BaiduSDKDemo.class));
//        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    public static boolean checkSelfPermission(Context context, String permission) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Method method = Context.class.getMethod("checkSelfPermission",
                        String.class);
                return (Integer) method.invoke(context, permission) == PackageManager.PERMISSION_GRANTED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
