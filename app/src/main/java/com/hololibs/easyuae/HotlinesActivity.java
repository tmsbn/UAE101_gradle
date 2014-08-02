package com.hololibs.easyuae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;


public class HotlinesActivity extends Activity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, AdapterView.OnItemClickListener {

    @InjectView(R.id.hotlineCategoryList)
    ListView hotlineCategoryLv;

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
        mCursorAdapter.setSearchText("");
        hotlineCategoryLv.setAdapter(mCursorAdapter);
        hotlineCategoryLv.setOnItemClickListener(this);
        loadLibraryData();

    }

    private void loadLibraryData() {

        Query.all(Group.class).getAsync(getLoaderManager(), onResultsLoaded, Group.class);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private ManyQuery.ResultHandler<Group> onResultsLoaded =
            new ManyQuery.ResultHandler<Group>() {

                @Override
                public boolean handleResult(CursorList<Group> result) {

                    if (result != null && result.size() != 0) {
                        mCursorAdapter.swapCursor(result);
                        return true;
                    } else {
                        if (parseAndSaveDataFromJson()) {
                            loadLibraryData();

                        } else {
                            Crouton.makeText(HotlinesActivity.this, "Some error has occured", Style.ALERT).show();

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
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Intent intent = new Intent(this, HotlineDetailsActivity.class);
        Group group = ((GroupCursorAdapter) hotlineCategoryLv.getAdapter()).getItem(position);
        intent.putExtra("groupId", group.groupId);
        startActivity(intent);

    }
}
