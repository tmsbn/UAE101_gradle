package com.hololibs.easyuae;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;

/**
 * Created by tmsbn on 7/19/14.
 */
public class Globals {

    private static final String CONFIG_PATH = "configuration.json";
    private static final String DATA_PATH = "data.json";


    private static ServerResponse serverResponse;

    public enum CellProvider {
        ETISALAT(50), DU(55);

        private int code;

        CellProvider(int Code) {
            this.code = Code;
        }

        public int getCode() {
            return code;
        }
        }

    public static final int IMAGES_HOME[] = {R.drawable.ic_hotline, R.drawable.ic_credit_transfer, R.drawable.ic_credit_transfer, R.drawable.ic_parking_meter};

    public static ServerResponse getData(Context context) {

        if (serverResponse == null) {

            try {
                Gson gson =new GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                InputStream input = context.getAssets().open(DATA_PATH);
                int size = input.available();
                byte[] buffer = new byte[size];
                input.read(buffer);
                input.close();
                String text = new String(buffer);
                text = text.replaceAll("\\s+", "");
                serverResponse = gson.fromJson(text, ServerResponse.class);
                return serverResponse;

            } catch (Exception e) {
                return null;

            }
        } else
            return serverResponse;
    }

    /**
     * Check if operator is etisalat or Du and sets the provider field, If no operator found, closes the activity
     *
     * @return
     */
    public static CellProvider getCellProvider(Context context) {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tMgr.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE || tMgr.getLine1Number() == null) {
            Toast.makeText(context, context.getString(R.string.networkNotAvaliable_msg), Toast.LENGTH_SHORT).show();
        } else {
            Log.v("globals", tMgr.getNetworkOperatorName());
            String operatorName = tMgr.getNetworkOperatorName();
            if (operatorName.contains("ETISALAT")) {
                return CellProvider.ETISALAT;
            } else if (operatorName.contains("DU")) {
                return CellProvider.DU;
            } else
                Toast.makeText(context, context.getString(R.string.onlyUAENumbers_msg), Toast.LENGTH_SHORT).show();

        }


        return null;
    }

}
