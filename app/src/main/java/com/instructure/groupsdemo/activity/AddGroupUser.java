package com.instructure.groupsdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.instructure.canvasapi.api.GroupAPI;
import com.instructure.canvasapi.api.GroupCategoriesAPI;
import com.instructure.canvasapi.api.UserAPI;
import com.instructure.canvasapi.model.Course;
import com.instructure.canvasapi.model.Group;
import com.instructure.canvasapi.model.GroupCategory;
import com.instructure.canvasapi.model.User;
import com.instructure.canvasapi.utilities.CanvasCallback;
import com.instructure.canvasapi.utilities.LinkHeaders;
import com.instructure.groupsdemo.R;
import com.instructure.groupsdemo.adapter.GroupUserArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by brady on 12/5/14.
 */
public class AddGroupUser extends BaseActivity {

    private Group group;
    private GroupCategory category;
    private CanvasCallback<Course[]> courseCallback;
    private CanvasCallback<User[]> userCallback;
    private CanvasCallback<Response> createMembershipCallback;
    private ListView userList;
    private GroupUserArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);
        if(getIntent() != null && getIntent().hasExtra("group")) {
            group = getIntent().getParcelableExtra("group");
            category = getIntent().getParcelableExtra("category");
        }
        else {
            //something went wrong, if we don't have the group this activity is worthless
            return;
        }
        adapter = new GroupUserArrayAdapter(this, 0, new ArrayList<User>(), false);
        userList = (ListView)findViewById(R.id.listView);
        userList.setAdapter(adapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //invite the user to the group
                GroupAPI.createMembership(group.getId(), Long.toString(adapter.getItem(position).getId()), createMembershipCallback);
            }
        });

        setupCallbacks();

        GroupCategoriesAPI.getFirstPageUsersInCategory(category.getId(), false, userCallback);
       // CourseAPI.getAllFavoriteCourses(courseCallback);
    }

    private void setupCallbacks() {
        courseCallback = new CanvasCallback<Course[]>(this) {
            @Override
            public void cache(Course[] courses) {

            }

            @Override
            public void firstPage(Course[] courses, LinkHeaders linkHeaders, Response response) {
                for(Course course : courses) {
                    UserAPI.getFirstPagePeople(course, userCallback);
                }
            }
        };

        userCallback = new CanvasCallback<User[]>(this) {
            @Override
            public void cache(User[] users) {

            }

            @Override
            public void firstPage(User[] users, LinkHeaders linkHeaders, Response response) {
                adapter.addUsers(Arrays.asList(users));

                //normally this would be done when the user starts scrolling down the list
                if(linkHeaders.nextURL != null) {
                    GroupCategoriesAPI.getNextPageUsersInCategory(linkHeaders.nextURL, userCallback);
                }
            }
        };

        createMembershipCallback = new CanvasCallback<Response>(this) {
            @Override
            public void cache(Response response) {

            }

            @Override
            public void firstPage(Response response, LinkHeaders linkHeaders, Response response2) {
                Toast.makeText(AddGroupUser.this, getString(R.string.membershipAddedSuccessfully), Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onFailure(RetrofitError retrofitError) {
                Toast.makeText(AddGroupUser.this, retrofitError.getMessage(), Toast.LENGTH_SHORT).show();
                return super.onFailure(retrofitError);
            }
        };
    }
}
