package com.instructure.groupsdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.instructure.canvasapi.api.GroupCategoriesAPI;
import com.instructure.canvasapi.model.Course;
import com.instructure.canvasapi.model.GroupCategory;
import com.instructure.canvasapi.utilities.CanvasCallback;
import com.instructure.canvasapi.utilities.LinkHeaders;
import com.instructure.groupsdemo.R;
import com.instructure.groupsdemo.adapter.GroupCategoryAdapter;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by brady on 12/5/14.
 */
public class GroupCategoryActivity extends BaseActivity {

    private Course course;
    private ListView listView;
    private FloatingActionButton addCategory;
    private GroupCategoryAdapter adapter;
    private CanvasCallback<GroupCategory[]> groupCategoryCallback;
    private CanvasCallback<GroupCategory> createGroupCategoryCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        if(getIntent() != null && getIntent().hasExtra("course")) {
            course = getIntent().getParcelableExtra("course");
        }
        else {
            //something went wrong, we need the course
            return;
        }

        listView = (ListView)findViewById(R.id.groupsListView);

        adapter = new GroupCategoryAdapter(this, 0, new ArrayList<GroupCategory>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent groupCategoryIntent = new Intent(GroupCategoryActivity.this, GroupListActivity.class);
                groupCategoryIntent.putExtra("course", (Parcelable) course);
                groupCategoryIntent.putExtra("category", (Parcelable)adapter.getItem(position));
                startActivity(groupCategoryIntent);
            }
        });

        addCategory = (FloatingActionButton)findViewById(R.id.add);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GroupCategoryActivity.this);

                alert.setTitle(getString(R.string.addGroupCategory));
                // Set an EditText view to get user input
                final EditText input = new EditText(GroupCategoryActivity.this);
                input.setHint(getString(R.string.enterCategoryName));
                alert.setView(input);

                alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(TextUtils.isEmpty(input.getText().toString())) {
                            Toast.makeText(GroupCategoryActivity.this, getString(R.string.groupCategoryNotNull), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String value = input.getText().toString();
                        GroupCategoriesAPI.createGroupCategoryForCourse(course.getId(), value, createGroupCategoryCallback);
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
        setupCallbacks();

        GroupCategoriesAPI.getFirstPageGroupCategoriesInCourse(course.getId(), groupCategoryCallback);
    }

    private void setEmptyView() {
        //add an empty view
        TextView emptyView = new TextView(GroupCategoryActivity.this);
        emptyView.setPadding(8,8,8,8);
        emptyView.setText(getString(R.string.emptyViewCategories));
        addContentView(emptyView, listView.getLayoutParams());
    }
    private void setupCallbacks() {
        groupCategoryCallback = new CanvasCallback<GroupCategory[]>(this) {
            @Override
            public void cache(GroupCategory[] groupCategories) {

            }

            @Override
            public void firstPage(GroupCategory[] groupCategories, LinkHeaders linkHeaders, Response response) {
                adapter.addAll(Arrays.asList(groupCategories));

                if(groupCategories.length == 0 && adapter.getCount() == 0) {
                    setEmptyView();
                }

                if(linkHeaders.nextURL != null) {
                    GroupCategoriesAPI.getNextPageGroupCategoriesInCourse(linkHeaders.nextURL, groupCategoryCallback);
                }
            }

            @Override
            public boolean onFailure(RetrofitError retrofitError) {
                setEmptyView();
                return super.onFailure(retrofitError);
            }
        };

        createGroupCategoryCallback = new CanvasCallback<GroupCategory>(this) {
            @Override
            public void cache(GroupCategory groupCategory) {

            }

            @Override
            public void firstPage(GroupCategory groupCategory, LinkHeaders linkHeaders, Response response) {
                if(groupCategory != null) {
                    adapter.add(groupCategory);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public boolean onFailure(RetrofitError retrofitError) {
                Toast.makeText(GroupCategoryActivity.this, retrofitError.getMessage(), Toast.LENGTH_SHORT).show();
                return super.onFailure(retrofitError);
            }
        };
    }

}
