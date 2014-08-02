package com.hololibs.easyuae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;


public class HotlineDetailsActivity extends Activity {

    @InjectView(R.id.hotlineDetailsList)
    ListView hotlineDetailsLv;

    HotlineDetailsCursorAdapter mCursorAdapter;
    int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline_details);
        setUpViews();
        groupId = getIntent().getIntExtra("groupId", 0);
        if (groupId == 0)
            finish();
        getHotlineNumbers();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hotline_details, menu);
        return true;
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
        hotlineDetailsLv.setAdapter(mCursorAdapter);
    }

    private void getHotlineNumbers() {

        String rawQuery = "SELECT hotlines.*, emirates.name FROM hotlines LEFT JOIN emirates ON hotlines.emirate_id = emirates.emirate_id WHERE hotlines.group_id=?";
        CursorList<HotlineDetails> hotlineDetailsList=Query.many(HotlineDetails.class, rawQuery, groupId).get();
        mCursorAdapter.swapCursor(hotlineDetailsList);


    }


    public void addToContacts(String name, String number) {


        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);


        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);

        int PICK_CONTACT = 100;
        startActivityForResult(intent, PICK_CONTACT);
    }

    private ManyQuery.ResultHandler<HotlineDetails> onResultsLoaded =
            new ManyQuery.ResultHandler<HotlineDetails>() {

                @Override
                public boolean handleResult(CursorList<HotlineDetails> result) {

                    if (result != null && result.size() != 0) {
                        mCursorAdapter.swapCursor(result);
                    }
                    return true;


                }
            };
}
