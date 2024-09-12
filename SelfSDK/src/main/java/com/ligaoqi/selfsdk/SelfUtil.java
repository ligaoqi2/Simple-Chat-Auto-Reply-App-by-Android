package com.ligaoqi.selfsdk;

import android.content.Context;
import android.widget.Toast;

public class SelfUtil {
    public static void Introduce(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
