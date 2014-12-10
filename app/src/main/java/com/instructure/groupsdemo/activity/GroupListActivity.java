package com.instructure.groupsdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.instructure.canvasapi.api.GroupAPI;
import com.instructure.canvasapi.api.GroupCategoriesAPI;
import com.instructure.canvasapi.model.CanvasContext;
import com.instructure.canvasapi.model.Course;
import com.instructure.canvasapi.model.Group;
import com.instructure.canvasapi.model.GroupCategory;
import com.instructure.canvasapi.utilities.CanvasCallback;
import com.instructure.canvasapi.utilities.LinkHeaders;
import com.instructure.groupsdemo.R;
import com.instructure.groupsdemo.adapter.CanvasContextArrayAdapter;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class GroupListActivity extends BaseActivity {

    private Course course;
    private GroupCategory category;
    private ListView groupsListView;
    private FloatingActionButton addGroup;
    private CanvasContextArrayAdapter groupArrayAdapter;
    private CanvasCallback<Group[]> groupCanvasCallback;
    private CanvasCallback<Group> createGroupCallback;
    private CanvasCallback<Response> deleteGroupCallback;

    private ActionMode mActionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        if(getIntent() != null && getIntent().hasExtra("course")) {
            course = getIntent().getParcelableExtra("course");
            category = getIntent().getParcelableExtra("category");
        }
        else {
            //something went wrong, we need the course
            return;
        }
        //link the views
        groupsListView = (ListView)findViewById(R.id.groupsListView);
        groupsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        groupsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                onListItemCheck(position);
                return true;
            }
        });


        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mActionMode == null) {
                    CanvasContext group = groupArrayAdapter.getItem(position);
                    Intent groupPeopleIntent = new Intent(GroupListActivity.this, GroupUserActivity.class);
                    groupPeopleIntent.putExtra("group", (Parcelable) group);
                    groupPeopleIntent.putExtra("category", (Parcelable) category);
                    startActivity(groupPeopleIntent);
                }
                else {
                    onListItemCheck(position);
                }
            }
        });

        addGroup = (FloatingActionButton)findViewById(R.id.add);
        addGroup.initBackground();
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GroupListActivity.this);
                alert.setTitle(getString(R.string.addGroup));
                // Set an EditText view to get user input
                final EditText input = new EditText(GroupListActivity.this);
                input.setHint(getString(R.string.enterGroupName));
                alert.setView(input);

                alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(TextUtils.isEmpty(input.getText().toString())) {
                            Toast.makeText(GroupListActivity.this, getString(R.string.groupNameNotNull), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String value = input.getText().toString();
                        GroupAPI.createGroupWithCategory(category.getId(), value, true, createGroupCallback);
                    }
                });

                alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

        groupArrayAdapter = new CanvasContextArrayAdapter(this, 0, new ArrayList<CanvasContext>());
        groupsListView.setAdapter(groupArrayAdapter);
        //callback
        setupCallback();

        //make the api call
        GroupCategoriesAPI.getFirstPageGroupsFromCategory(category.getId(), groupCanvasCallback);
    }


    private void onListItemCheck(int position) {

        groupArrayAdapter.toggleSelection(position);
        boolean hasCheckedItems = groupArrayAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = startActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();


        if(mActionMode != null)
            mActionMode.setTitle(String.valueOf(groupArrayAdapter.getSelectedCount()) + " selected");
    }

    private void setEmptyView() {
        //add an empty view
        TextView emptyView = new TextView(GroupListActivity.this);
        emptyView.setPadding(8,8,8,8);
        emptyView.setText(getString(R.string.emptyViewGroup));
        addContentView(emptyView, groupsListView.getLayoutParams());
    }
    private void setupCallback() {
        groupCanvasCallback = new CanvasCallback<Group[]>(GroupListActivity.this) {
            @Override
            public void cache(Group[] group) {

            }

            @Override
            public void firstPage(Group[] group, LinkHeaders linkHeaders, retrofit.client.Response response) {
                groupArrayAdapter.setCanvasContexts(Arrays.asList((CanvasContext[])group));

                if(group.length == 0 && groupArrayAdapter.getCount() == 0) {
                    setEmptyView();
                }
                // this should be done when the user scrolls to the bottom of the page
                if(linkHeaders.nextURL != null) {
                    GroupAPI.getNextPageGroups(linkHeaders.nextURL, groupCanvasCallback);
                }
            }

            @Override
            public boolean onFailure(RetrofitError retrofitError) {
                setEmptyView();
                return super.onFailure(retrofitError);
            }
        };

        createGroupCallback = new CanvasCallback<Group>(GroupListActivity.this) {
            @Override
            public void cache(Group group) {

            }

            @Override
            public void firstPage(Group group, LinkHeaders linkHeaders, Response response) {
                groupArrayAdapter.addCanvasContext(group);
            }
        };

        deleteGroupCallback = new CanvasCallback<Response>(GroupListActivity.this) {
            @Override
            public void cache(Response response) {

            }

            @Override
            public void firstPage(Response response, LinkHeaders linkHeaders, Response response2) {
                groupArrayAdapter.notifyDataSetChanged();
            }
        };
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.contextual_list_view, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // retrieve selected items and print them out
            SparseBooleanArray selected = groupArrayAdapter.getSelectedIds();
            for (int i = 0; i < selected.size(); i++){
                if (selected.valueAt(i)) {
                    CanvasContext selectedItem = groupArrayAdapter.getItem(selected.keyAt(i));
                    GroupAPI.deleteGroup(selectedItem.getId(), deleteGroupCallback);
                    groupArrayAdapter.remove(selectedItem);
                }
            }

            // close action mode
            mode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            groupArrayAdapter.removeSelection();
            mActionMode = null;
        }

    }
}
