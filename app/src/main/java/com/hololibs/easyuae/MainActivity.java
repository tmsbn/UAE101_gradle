package com.hololibs.easyuae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {


    @InjectView(R.id.dashList)
    ListView servicesLv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        servicesLv.setAdapter(getServicesAdapter());

    }

    private SimpleAdapter getServicesAdapter() {

        String[] services = getResources().getStringArray(R.array.services);
        String[] servicesDescription = getResources().getStringArray(R.array.services_description);

        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        int length = services.length;
        for (int i = 0; i < length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("text", services[i]);
            map.put("image", Globals.IMAGES_HOME[i]);
            map.put("desc", servicesDescription[i]);
            data.add(map);
        }


        int[] to = {R.id.servicesTv, R.id.servicesIv, R.id.servicesDescTv};
        String[] from = {"text", "image", "desc"};
        return new SimpleAdapter(this, data, R.layout.lv_rw_services, from, to);
    }



    @OnItemClick(R.id.dashList)
    public void goToService(int position) {
        Intent intent;
        switch (position) {

            case 0:

                intent = new Intent(this, GroupActivity.class);
                startActivity(intent);

                break;
            case 3:

                intent = new Intent(this, PayParkActivity.class);
                startActivity(intent);

                break;

            case 2:

                checkCredit();

                break;

            case 1:

                intent = new Intent(this, SendCreditActivity.class);
                startActivity(intent);

                break;

        }
    }

    private void checkCredit() {

        Globals.CellProvider cellProvider = Globals.getCellProvider(this);
        if (cellProvider != null) {
            String phoneNumber = null;
            switch (cellProvider) {
                case ETISALAT:
                    phoneNumber = Configuration.checkCreditEtisalatNumber;
                    break;
                case DU:
                    phoneNumber = Configuration.checkCreditDuNumber;
                    break;
            }
            Log.i(getClass().getSimpleName(), "tel:" + phoneNumber);
            /**
             * URI encode is needed to encode the # character required for checking credit
             */
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(phoneNumber)));
            startActivity(intent);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

}
