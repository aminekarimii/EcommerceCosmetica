package com.classroom.app1.Helpers;

import android.net.Uri;

public interface DataStatusImage {

    void onSuccess(Uri uri);

    void onError(String e);
}
