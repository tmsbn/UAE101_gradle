package com.hololibs.easyuae.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;

import com.hololibs.easyuae.R;
import com.hololibs.easyuae.adapters.HotlineDetailsCursorAdapter;
import com.hololibs.easyuae.models.HotlineDetails;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class HotlineDetailsActivity extends Activity implements HotlineDetailsCursorAdapter.HotlineInterface {

    @InjectView(R.id.hotlineDetailsList)
    StickyListHeadersListView hotlineDetailsLv;

    HotlineDetailsCursorAdapter mCursorAdapter;

    int groupId;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline_details);
        setUpViews();

        groupId = getIntent().getIntExtra("groupId", 0);
        groupName = getIntent().getStringExtra("groupName");

        if (groupId == 0)
            finish();
        else {
            getActionBar().setTitle(groupName);
            getHotlineNumbers();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hotline_details, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpViews() {

        ButterKnife.inject(this);
        mCursorAdapter = new HotlineDetailsCursorAdapter(this);
        mCursorAdapter.setDelegate(this);
        hotlineDetailsLv.setAdapter(mCursorAdapter);

    }

    private void getHotlineNumbers() {

        String rawQuery = "SELECT hotlines.*, emirates.name ,emirates.emirate_id FROM hotlines LEFT JOIN emirates ON hotlines.emirate_id = emirates.emirate_id WHERE hotlines.group_id=?";
        Query.many(HotlineDetails.class, rawQuery, groupId).getAsync(getLoaderManager(), new ManyQuery.ResultHandler<HotlineDetails>() {

            @Override
            public boolean handleResult(CursorList<HotlineDetails> result) {

                if (result != null && result.size() != 0) {
                    mCursorAdapter.swapCursor(result);
                }
                return true;


            }
        });

    }

    @Override
    public void addToContacts(String name, String number) {


        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);


        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);

        int PICK_CONTACT = 100;
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void callHotline(String number) {

        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


}
