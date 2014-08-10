package com.hololibs.easyuae.activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.hololibs.easyuae.Globals;
import com.hololibs.easyuae.R;
import com.hololibs.easyuae.adapters.GroupResultCursorAdapter;
import com.hololibs.easyuae.models.Group;
import com.hololibs.easyuae.models.GroupResult;
import com.hololibs.easyuae.models.ServerResponse;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class GroupActivity extends Activity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener, GroupResultCursorAdapter.GroupsInterface, RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.hotlineGroupList)
    ListView hotlineGroupLv;

    @InjectView(R.id.status)
    TextView statusTv;

    @InjectView(R.id.groupsOptions)
    RadioGroup mRadioGroup;

    GroupResultCursorAdapter mCursorAdapter;

    SearchView mSearchView;
    MenuItem mSearchItem;

    String searchText = "";

    boolean showingMarked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setUpViews();
    }

    private void setUpViews() {
        ButterKnife.inject(this);
        mCursorAdapter = new GroupResultCursorAdapter(this);


        hotlineGroupLv.setAdapter(mCursorAdapter);
        hotlineGroupLv.setOnItemClickListener(this);

        mRadioGroup.setOnCheckedChangeListener(this);

        if (doesDatabaseExist(this, Globals.DATABASE_NAME))
            loadCursor();
        else {
            if (parseAndSaveDataFromJson())
                loadCursor();
            else {
                statusTv.setText(getString(R.string.CouldNotLoadData_txt));
                statusTv.setVisibility(View.VISIBLE);
            }

        }

    }

    private void loadCursor() {

        mCursorAdapter.setIsMarkedShown(showingMarked);
        String rawQuery = "SELECT g.group_id, g.group_name, g.marked, GROUP_CONCAT(e.name , ', ') AS 'emirates' FROM ( groups g LEFT JOIN hotlines h ON h.group_id = g.group_id LEFT JOIN emirates e ON e.emirate_id = h.emirate_id) WHERE h.emirate_id IS NOT NULL";
        if (showingMarked)
            rawQuery += " AND g.marked=1";

        if (!searchText.equals(""))
            rawQuery += " AND g.group_name LIKE '%" + searchText + "%'";

        rawQuery += " GROUP BY g.group_id";
        Query.many(GroupResult.class, rawQuery).getAsync(getLoaderManager(), OnGroupResultsLoaded);

    }


    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    private boolean parseAndSaveDataFromJson() {

        ServerResponse serverResponse = Globals.getData(this);
        try {
            serverResponse.emirates.saveAll();
            serverResponse.groups.saveAll();
            serverResponse.hotlines.saveAll();
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hotlines, menu);

        mSearchItem = menu.findItem(R.id.searchItem);
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchItem.setOnActionExpandListener(this);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    private ManyQuery.ResultHandler<GroupResult> OnGroupResultsLoaded =
            new ManyQuery.ResultHandler<GroupResult>() {

                @Override
                public boolean handleResult(CursorList<GroupResult> result) {


                    if (result != null) {
                        mCursorAdapter.swapCursor(result);
                        if (result.size() == 0) {
                            statusTv.setText(getString(R.string.noResults_txt));
                            statusTv.setVisibility(View.VISIBLE);
                        } else
                            statusTv.setVisibility(View.GONE);
                        return false;
                    }
                    return false;

                }
            };

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        ((GroupResultCursorAdapter) hotlineGroupLv.getAdapter()).setSearchText(searchText);
        this.searchText = searchText;
        loadCursor();

        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Intent intent = new Intent(this, HotlineDetailsActivity.class);
        GroupResult groupResult = ((GroupResultCursorAdapter) hotlineGroupLv.getAdapter()).getItem(position);
        intent.putExtra("groupId", groupResult.groupId);
        intent.putExtra("groupName", groupResult.groupName);
        startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


    @Override
    public void groupWasMarked(GroupResult groupResult) {

        Group group = Query.one(Group.class, "SELECT * FROM groups WHERE group_id=?", groupResult.groupId).get();
        if (group != null) {
            group.marked = !group.marked;
            group.save();

        }

        loadCursor();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        int checked = radioGroup.getCheckedRadioButtonId();
        switch (checked) {
            case R.id.allGroups:
                showingMarked = false;

                break;

            case R.id.markedGroup:
                showingMarked = true;
                break;

        }

        loadCursor();

    }
}
