package com.hayukleung.devicepolicy;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.hayukleung.mice.R;
import com.hayukleung.permissions.PermissionsRequiredActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DevicePolicyActivity.java
 * <p/>
 * Created by hayukleung on 3/22/16.
 */
public class DevicePolicyActivity extends PermissionsRequiredActivity implements Observer {

    @Bind(R.id.Activation)
    Button mActivation;
    @Bind(R.id.Deactivation)
    Button mDeactivation;
    @Bind(R.id.Lock)
    Button mLock;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    private Handler mUIHandler;

    @Override
    public void update() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    @Override
    protected String[] requiredPermissions() {
        // 无需检测权限
        return new String[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_policy);
        ButterKnife.bind(this);

        mUIHandler = new Handler(getMainLooper());

        App app = (App) getApplication();
        app.set(this);

        // 取得系统服务
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, AdminReceiver.class);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
        mDevicePolicyManager = null;
        mActivation = null;
        mDeactivation = null;
        mLock = null;
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    private void refresh() {

        if (null == mActivation || null == mDeactivation || null == mLock) {
            return;
        }

        if (null == mDevicePolicyManager) {
            mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        }

        if (mDevicePolicyManager.isAdminActive(mComponentName)) {
            mActivation.setVisibility(View.GONE);
            mDeactivation.setVisibility(View.VISIBLE);
            mLock.setVisibility(View.VISIBLE);
        } else {
            mActivation.setVisibility(View.VISIBLE);
            mDeactivation.setVisibility(View.GONE);
            mLock.setVisibility(View.GONE);
        }
    }

    @OnClick({
            R.id.Activation,
            R.id.Deactivation,
            R.id.Lock
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Activation:
                startDeviceManager();
                break;
            case R.id.Deactivation:
                stopDeviceManager();
                break;
            case R.id.Lock:
                sysLock();
                break;
        }
    }

    /**
     * 启动设备管理权限
     */
    private void startDeviceManager() {

        boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
        if (active) {
            refresh();
        } else {
            // 添加一个隐式意图，完成设备权限的添加
            // 这个Intent (DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)跳转到 权限提醒页面
            // 并传递了两个参数EXTRA_DEVICE_ADMIN 、 EXTRA_ADD_EXPLANATION
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

            // 权限列表
            // EXTRA_DEVICE_ADMIN参数中说明了用到哪些权限，
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);

            // 描述(additional explanation)
            // EXTRA_ADD_EXPLANATION参数为附加的说明
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "乖乖当小白鼠吧~~~~~~");

            startActivityForResult(intent, 0);
        }
    }

    /**
     * 禁用设备管理权限方法实现
     */
    private void stopDeviceManager() {
        boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
        if (active) {
            mDevicePolicyManager.removeActiveAdmin(mComponentName);
        } else {
            refresh();
        }
    }

    /**
     * 调用系统锁方法实现
     */
    private void sysLock() {
        boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
        if (active) {
            mDevicePolicyManager.lockNow();
        } else {
            startDeviceManager();
        }
    }
}
