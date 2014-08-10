package com.hololibs.easyuae.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hololibs.easyuae.Globals;
import com.hololibs.easyuae.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by tmsbn on 12/31/13.
 * Activity to Send Credit to other phones
 */
public class SendCreditActivity extends Activity implements TextWatcher {

    @InjectView(R.id.phoneNumberToSendInput)
    EditText phoneNumberToSendEt;

    @InjectView(R.id.amountToSendInput)
    EditText amountToSendEt;

    @InjectView(R.id.chooseContactBtn)
    ImageButton chooseContactIbtn;

    @InjectView(R.id.contactName)
    TextView contactNameTv;

    @InjectView(R.id.prefix)
    TextView prefixTv;

    /**
     * variables for shared preferences
     */
    private static final String SP_SENDCREDITPREFS = "sp_sendCreditPrefs";
    private static final String SP_AMOUNT = "sp_amount";
    private static final String SP_CONTACTNO = "sp_contactno";
    private static final String SP_CONTACTNAME = "sp_contactname";


    Globals.CellProvider provider;

    private static final int CONTACT_PICKER_RESULT = 1;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendcredit);


        setUpViews();
        provider = Globals.getCellProvider(this.getApplicationContext());
        if (provider == null) {
            finish();
        }
        retrievePreviousValues();


    }


    private void setUpViews() {

        ButterKnife.inject(this);
        phoneNumberToSendEt.addTextChangedListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.ab_send_credit:
                sendCredit();
                break;
        }


        return super.onOptionsItemSelected(item);

    }

    @OnClick(R.id.sendBtn)
    public void sendCredit() {

        if (phoneNumberToSendEt.getText().length() == 0 || amountToSendEt.getText().length() == 0)
            Crouton.makeText(this, getString(R.string.fillAllDetails_msg), Style.ALERT).show();
        else if (phoneNumberToSendEt.length() >= 15)
            Crouton.makeText(this, getString(R.string.invalidPhoneNumber_msg), Style.ALERT).show();
        else {
            switch (provider) {
                case ETISALAT:
                    String numberToDial = "tel:*100*" + phoneNumberToSendEt.getText().toString() + "*" + Integer.parseInt(amountToSendEt.getText().toString()) + "#";
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(numberToDial)));
                    startActivity(intent);

                    break;
                case DU:
                    break;

            }
            savePreviousValues();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_credit, menu);
        return true;
    }


    @OnClick(R.id.chooseContactBtn)
    public void pickAContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:

                    try {

                        Uri uri = data.getData();
                        String id = uri.getLastPathSegment();
                        Cursor pCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                        pCursor.moveToFirst();

                        int phoneIndex = pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int contactNameIndex = pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                        String phoneNumberToSend = pCursor.getString(phoneIndex);

                        if (!phoneNumberToSend.contains(Integer.toString(provider.getCode()))) {
                            clearContactNumberWithMessage("Only " + provider.name() + " numbers allowed!");
                            break;
                        }

                        if (phoneNumberToSend.length() >= 10) {
                            phoneNumberToSendEt.setText(phoneNumberToSend);
                            contactNameTv.setText(pCursor.getString(contactNameIndex));
                        } else
                            throw new Exception();


                    } catch (Exception e) {
                        clearContactNumberWithMessage(getString(R.string.invalidPhoneNumber_msg));

                    }
                    break;
                default:
                    clearContactNumberWithMessage("Could not find phone number");
                    break;


            }
        }
    }

    /**
     * Clear contact number and name
     *
     * @param message message to send
     */
    private void clearContactNumberWithMessage(String message) {
        contactNameTv.setText("");
        phoneNumberToSendEt.setText("");
        Crouton.makeText(this, message, Style.ALERT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        if (contactNameTv.getVisibility() == View.VISIBLE && contactNameTv.getText().toString().length() != 0) {
            contactNameTv.setText("");
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    /**
     * Saves the last entered values in preferences file
     */
    private void savePreviousValues() {

        SharedPreferences prefParking = getSharedPreferences(SP_SENDCREDITPREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefParking.edit();

        editor.putInt(SP_AMOUNT, Integer.parseInt(amountToSendEt.getText().toString()));
        editor.putString(SP_CONTACTNO, phoneNumberToSendEt.getText().toString());
        editor.putString(SP_CONTACTNAME, contactNameTv.getText().toString());


        editor.apply();
    }

    /**
     * Retrieves the previous values entered by the user and populates the form elements
     */
    private void retrievePreviousValues() {

        SharedPreferences prefParking = getSharedPreferences(SP_SENDCREDITPREFS, Context.MODE_PRIVATE);

        if (prefParking.getInt(SP_AMOUNT, 0) != 0) {
            amountToSendEt.setText("" + prefParking.getInt(SP_AMOUNT, 0));//Because Edittext takes only strings
            phoneNumberToSendEt.setText("" + prefParking.getString(SP_CONTACTNO, ""));
            contactNameTv.setText(prefParking.getString(SP_CONTACTNAME, ""));
            Crouton.makeText(this, getString(R.string.previousEntry_txt), new Style.Builder().setBackgroundColor(getResources().getColor(R.color.Gray)).build()).show();

        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
}