package com.hololibs.easyuae;

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
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class GroupActivity extends Activity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener {

    @InjectView(R.id.hotlineCategoryList)
    ListView hotlineCategoryLv;

    @InjectView(R.id.status)
    TextView statusTv;

    // GroupCursorAdapter mCursorAdapter;

    GroupResultCursorAdapter mCursorAdapter;

    SearchView mSearchView;
    MenuItem mSearchItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlines);
        setUpViews();
    }

    private void setUpViews() {
        ButterKnife.inject(this);
        mCursorAdapter = new GroupResultCursorAdapter(this);

        hotlineCategoryLv.setAdapter(mCursorAdapter);
        hotlineCategoryLv.setOnItemClickListener(this);

        if (doesDatabaseExist(this, Globals.DATABASE_NAME))
            loadHotlinesData();
        else {
            //Crouton.makeText(GroupActivity.this, getString(R.string.loadingData_txt), Style.CONFIRM).show();
            if (parseAndSaveDataFromJson())
                loadHotlinesData();
            else {
                statusTv.setText(getString(R.string.CouldNotLoadData_txt));
                statusTv.setVisibility(View.VISIBLE);
            }

        }

    }

    private void loadHotlinesData() {

        mCursorAdapter.setSearchText("");
        String rawQuery = "SELECT g.group_id, g.group_name, GROUP_CONCAT(e.name , ', ') AS 'emirates' FROM ( groups g LEFT JOIN hotlines h ON h.group_id = g.group_id LEFT JOIN emirates e ON e.emirate_id = h.emirate_id) WHERE h.emirate_id IS NOT NULL GROUP BY g.group_id";
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
                        return true;
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

        ((GroupResultCursorAdapter) hotlineCategoryLv.getAdapter()).setSearchText(searchText);
        String rawQuery = "SELECT g.group_id, g.group_name, GROUP_CONCAT(e.name , ', ') AS emirates FROM ( groups g LEFT JOIN hotlines h ON h.group_id = g.group_id LEFT JOIN emirates e ON e.emirate_id = h.emirate_id) WHERE g.group_name LIKE ? AND h.emirate_id IS NOT NULL GROUP BY g.group_id";
        Query.many(GroupResult.class, rawQuery, "%" + searchText + "%").getAsync(getLoaderManager(), OnGroupResultsLoaded);


        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        loadHotlinesData();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Intent intent = new Intent(this, HotlineDetailsActivity.class);
        GroupResult groupResult = ((GroupResultCursorAdapter) hotlineCategoryLv.getAdapter()).getItem(position);
        intent.putExtra("groupId", groupResult.groupId);
        intent.putExtra("groupName", groupResult.groupName);
        startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


}
