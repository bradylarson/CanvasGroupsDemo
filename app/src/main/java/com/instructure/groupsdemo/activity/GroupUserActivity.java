package com.instructure.groupsdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.instructure.canvasapi.api.GroupAPI;
import com.instructure.canvasapi.model.Group;
import com.instructure.canvasapi.model.GroupCategory;
import com.instructure.canvasapi.model.User;
import com.instructure.canvasapi.utilities.CanvasCallback;
import com.instructure.canvasapi.utilities.LinkHeaders;
import com.instructure.groupsdemo.R;
import com.instructure.groupsdemo.adapter.GroupUserArrayAdapter;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by brady on 12/4/14.
 */
public class GroupUserActivity extends BaseActivity {

    private Group group;
    private GroupCategory category;
    private ListView groupUserList;
    private GroupUserArrayAdapter groupUserAdapter;
    private FloatingActionButton addUsers;
    private CanvasCallback<User[]> groupUserCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        if(getIntent() != null && getIntent().hasExtra("group")) {
            group = getIntent().getParcelableExtra("group");
            category = getIntent().getParcelableExtra("category");
            getActionBar().setTitle(getString(R.string.addUsers));
        }
        else {
            //something went wrong, we don't have a group to display
            return;
        }

        addUsers = (FloatingActionButton)findViewById(R.id.add);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPeopleIntent = new Intent(GroupUserActivity.this, AddGroupUser.class);
                addPeopleIntent.putExtra("group", (Parcelable) group);
                addPeopleIntent.putExtra("category", (Parcelable) category);
                startActivity(addPeopleIntent);
            }
        });
        groupUserList = (ListView)findViewById(R.id.groupsListView);

        groupUserAdapter = new GroupUserArrayAdapter(this, 0, new ArrayList<User>(), true);
        groupUserList.setAdapter(groupUserAdapter);

        setupCallback();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //update the list if the user comes back after adding users
        groupUserAdapter.clear();
        GroupAPI.getGroupUsersWithAvatars(group.getId(), groupUserCallback);
    }

    private void setEmptyView() {
        //add an empty view
        TextView emptyView = new TextView(GroupUserActivity.this);
        emptyView.setPadding(8,8,8,8);
        emptyView.setText(getString(R.string.emptyViewUsers));
        addContentView(emptyView, groupUserList.getLayoutParams());
    }
    private void setupCallback() {
        groupUserCallback = new CanvasCallback<User[]>(GroupUserActivity.this) {
            @Override
            public void cache(User[] users) {

            }

            @Override
            public void firstPage(User[] users, LinkHeaders linkHeaders, Response response) {

                groupUserAdapter.addUsers(Arrays.asList(users));

                if(users.length == 0 && groupUserAdapter.getCount() == 0) {
                    setEmptyView();
                }
                //normally this would be done when the user starts scrolling down the list
                if(linkHeaders.nextURL != null) {
                    GroupAPI.getNextPageGroupUsers(linkHeaders.nextURL, groupUserCallback);
                }
            }

            @Override
            public boolean onFailure(RetrofitError retrofitError) {
                setEmptyView();
                return super.onFailure(retrofitError);
            }
        };
    }
}
