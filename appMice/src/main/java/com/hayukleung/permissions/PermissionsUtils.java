package com.hayukleung.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * PermissionChecker.java
 * <p>
 * Created by hayukleung on 3/18/16.
 */
public class PermissionsUtils {

  public static boolean lacksPermissions(Context context, String... permissions) {
    for (String permission : permissions) {
      if (lacksPermission(context, permission)) {
        return true;
      }
    }
    return false;
  }

  private static boolean lacksPermission(Context context, String permission) {
    return ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_DENIED;
  }
}
