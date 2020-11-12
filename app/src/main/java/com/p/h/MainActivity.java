package com.p.h;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button scanBtn;
    private EditText contentEt;
    private Button createBtn;
    private ImageView imgQr;
    private Button photoBtn;
    private static final int ScanQrCode=222;    //扫描二维码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPermissions();
        initListener();

    }

    private void initView() {
        scanBtn = (Button) findViewById(R.id.scan_btn);
        contentEt = (EditText) findViewById(R.id.content_et);
        createBtn = (Button) findViewById(R.id.create_btn);
        imgQr = (ImageView) findViewById(R.id.img_qr);
        photoBtn = (Button) findViewById(R.id.photo_btn);
    }

    private void initPermissions() {
        /**
         * 判断是否在 Android11 上的权限
         */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            XXPermissions.with(this)// 申请拍照和存储权限
                    .permission(Permission.CAMERA,Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean all) {
                            if (all) {
//                                Toast.makeText(MainActivity.this, "获取权限成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean never) {
                            if (never) {
                                Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(MainActivity.this, denied);
                            } else {
                                Toast.makeText(MainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }else {
            XXPermissions.with(this)
                    // 申请拍照和存储权限
                    .permission(Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean all) {
                            if (all) {
                                Toast.makeText(MainActivity.this, "获取权限成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean never) {
                            if (never) {
                                Toast.makeText(MainActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(MainActivity.this, denied);
                            } else {
                                Toast.makeText(MainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

    private void initListener(){

        //打开默认二维码扫描界面
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanQrCode();
            }
        });

        //生成图普通二维码
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = contentEt.getText().toString().trim();

                if (TextUtils.isEmpty(text)){
                    Toast.makeText(MainActivity.this, "你输入的内容为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                createQrCode(text);

            }
        });

        //生成图片二维码
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = contentEt.getText().toString().trim();

                if (TextUtils.isEmpty(text)){
                    Toast.makeText(MainActivity.this, "你输入的内容为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                createLogoQrCode(text);
            }
        });
}


    //扫描二维码
    private void  scanQrCode(){
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, ScanQrCode);
    }

    //生成不带图片的二维码
    private void  createQrCode(String content){
        //参数 1.生成内容 2.宽度 3.
        Bitmap image = CodeUtils.createImage(content, 500, 500, null);
        imgQr.setImageBitmap(image);
    }

    //生成带图片的二维码
    private void  createLogoQrCode(String content){
        Bitmap image = CodeUtils.createImage(content, 500, 500, BitmapFactory.decodeResource(getResources(),R.mipmap.shunbusicon));
        imgQr.setImageBitmap(image);
    }


    //数据回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从系统权限设置页返回判断
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.hasPermission(this, Permission.RECORD_AUDIO) &&
                    XXPermissions.hasPermission(this, Permission.Group.CALENDAR)) {
                Toast.makeText(MainActivity.this, "用户已经在权限设置页授予了权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "用户没有在权限设置页授予权限", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 处理二维码扫描结果
         */
        if (requestCode == ScanQrCode) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_SHORT).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
