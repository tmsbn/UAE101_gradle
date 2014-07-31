package com.hololibs.easyuae;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Pay for parking module
 */
public class PayParkActivity extends Activity implements AdapterView.OnItemSelectedListener, TextWatcher {


    @InjectView(R.id.zoneInput)
    EditText zoneEt;

    @InjectView(R.id.durationInput)
    EditText durationEt;

    @InjectView(R.id.plateNumberInput)
    EditText plateNumberEt;

    @InjectView(R.id.pay)
    Button payBtn;

    @InjectView(R.id.plateCodeDropDown)
    Spinner plateCodeSpn;

    Globals.CellProvider provider;


    /**
     * variables for shared preferences, don't change these words on production, unless absolutely required
     */
    private static final String SP_PARKINGPREFS = "sp_parkingPrefs";
    private static final String SP_DURATION = "sp_duration";
    private static final String SP_PLATECODE = "sp_platecode";
    private static final String SP_PLATENUMBER = "sp_plateNumber";
    private static final String SP_ZONE = "sp_zone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parking);
        setUpAllViews();

        provider = Globals.getCellProvider(getApplicationContext());
        if (provider == null) {
            finish();
        }

        retrievePreviousValues();

    }

    private void setUpAllViews() {


        ButterKnife.inject(this);

        FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_send))
                .withButtonColor(Color.RED)
                .withGravity(Gravity.TOP | Gravity.RIGHT)
                .withMargins(40, 40, 40, 40)

                .create();

        fabButton.setVisibility(View.GONE);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payforParking();
            }
        });


        zoneEt.addTextChangedListener(this);

        plateCodeSpn = (Spinner) findViewById(R.id.plateCodeDropDown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plate_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plateCodeSpn.setAdapter(adapter);
        plateCodeSpn.setOnItemSelectedListener(this);


    }


    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        if (smsManager != null)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        else
            Crouton.makeText(this, getString(R.string.unableToFindNetwork_txt), new Style.Builder().setBackgroundColor(getResources().getColor(R.color.DarkGray)).build()).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_pay:
                payforParking();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.pay)
    public void payforParking() {

        if (zoneEt.getText().length() == 0 || plateCodeSpn.getSelectedItemPosition() == 0 || plateNumberEt.getText().length() == 0 || durationEt.getText().length() == 0) {
            Crouton.makeText(this, getString(R.string.fillAllDetails_msg), Style.CONFIRM).show();
        } else if (Integer.parseInt(durationEt.getText().toString()) > 24)
            Crouton.makeText(this, getString(R.string.duration_msg), Style.CONFIRM).show();
        else {

            String zone = zoneEt.getText().toString();
            String plateCodeNumber = plateCodeSpn.getSelectedItem().toString() + plateNumberEt.getText().toString();
            String duration = durationEt.getText().toString();
            String message = plateCodeNumber + " " + zone + " " + duration;
            final String finalMessage = message;
            final String phoneNumber = Configuration.mParkingDubaiNumber;

            new AlertDialog.Builder(this).setMessage("The SMS that will be send to " + phoneNumber + " is:" + message).setPositiveButton(getString(R.string.send_txt), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendSMS(phoneNumber, finalMessage);
                    savePreviousValues();

                }
            }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
        }

    }

    /**
     * Saves the last entered values in preferences file
     */
    private void savePreviousValues() {

        SharedPreferences prefParking = getSharedPreferences(SP_PARKINGPREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefParking.edit();

        editor.putInt(SP_DURATION, Integer.parseInt(durationEt.getText().toString()));
        editor.putInt(SP_PLATECODE, plateCodeSpn.getSelectedItemPosition());
        editor.putString(SP_ZONE, zoneEt.getText().toString());
        editor.putString(SP_PLATENUMBER, plateNumberEt.getText().toString());


        // Commit the edits!
        editor.commit();
    }

    /**
     * Retrieves the previous values entered by the user and populates the form elements
     */
    private void retrievePreviousValues() {

        SharedPreferences prefParking = getSharedPreferences(SP_PARKINGPREFS, Context.MODE_PRIVATE);

        if (prefParking.getInt(SP_PLATECODE, 0) != 0) {
            zoneEt.setText(prefParking.getString(SP_ZONE, ""));
            durationEt.setText("" + prefParking.getInt(SP_DURATION, 0));
            plateNumberEt.setText(prefParking.getString(SP_PLATENUMBER, ""));
            plateCodeSpn.setSelection(prefParking.getInt(SP_PLATECODE, 0));
            Crouton.makeText(this, getString(R.string.previousEntry_txt), new Style.Builder().setBackgroundColor(getResources().getColor(R.color.Gray)).build()).show();
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        TextView tvSelected = (TextView) adapterView.getSelectedView();

        if (position != 0)
            tvSelected.setTextColor(getResources().getColor(R.color.Black));
        else
            tvSelected.setTextColor(getResources().getColor(R.color.LightGrey));

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        TextView tvSelected = (TextView) adapterView.getSelectedView();
        tvSelected.setTextColor(getResources().getColor(R.color.LightGrey));


    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        if (!charSequence.toString().toUpperCase().equals(zoneEt.getText().toString())) {
            zoneEt.setText(charSequence.toString().toUpperCase());
            zoneEt.setSelection(charSequence.length());
        }


    }

    @Override
    public void afterTextChanged(Editable editable) {


    }
}
