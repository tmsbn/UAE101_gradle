package com.hololibs.easyuae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

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

    GroupCursorAdapter mCursorAdapter;

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
        mCursorAdapter = new GroupCursorAdapter(this);
        hotlineCategoryLv.setAdapter(mCursorAdapter);
        hotlineCategoryLv.setOnItemClickListener(this);
        loadHotlinesData();

    }

    private void loadHotlinesData() {

        mCursorAdapter.setSearchText("");
        Query.all(Group.class).getAsync(getLoaderManager(), onResultsLoaded, Group.class);
        statusTv.setText(getString(R.string.loading_txt));
        statusTv.setVisibility(View.VISIBLE);


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

    private ManyQuery.ResultHandler<Group> onResultsLoaded =
            new ManyQuery.ResultHandler<Group>() {

                @Override
                public boolean handleResult(CursorList<Group> result) {

                    if (result != null) {
                        mCursorAdapter.swapCursor(result);
                        if (result.size() == 0) {
                            statusTv.setText(getString(R.string.noResults_txt));
                            statusTv.setVisibility(View.VISIBLE);
                        } else
                            statusTv.setVisibility(View.GONE);
                        return true;
                    } else {
                        if (parseAndSaveDataFromJson()) {
                            loadHotlinesData();

                        } else {
                            Crouton.makeText(GroupActivity.this, "Some error has occured", Style.ALERT).show();

                        }
                        return false;
                    }


                }
            };

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        ((GroupCursorAdapter) hotlineCategoryLv.getAdapter()).setSearchText(searchText);
        Query.many(Group.class, "SELECT * FROM groups WHERE group_name LIKE ? ", "%" + searchText + "%").getAsync(getLoaderManager(), onResultsLoaded, Group.class);


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
        Group group = ((GroupCursorAdapter) hotlineCategoryLv.getAdapter()).getItem(position);
        intent.putExtra("groupId", group.groupId);
        intent.putExtra("groupName", group.groupName);
        startActivity(intent);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }


}
