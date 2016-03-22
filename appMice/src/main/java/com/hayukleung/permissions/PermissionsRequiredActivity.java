package com.hayukleung.permissions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 有权限需求的Activity
 */
public abstract class PermissionsRequiredActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;

    protected abstract String[] requiredPermissions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionsUtils.lacksPermissions(PermissionsRequiredActivity.this, requiredPermissions())) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsRequestActivity.startActivityForResult(this, REQUEST_CODE, requiredPermissions());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsRequestActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }
}
