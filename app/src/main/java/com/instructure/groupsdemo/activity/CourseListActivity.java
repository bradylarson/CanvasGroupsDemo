package com.instructure.groupsdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.instructure.canvasapi.api.CourseAPI;
import com.instructure.canvasapi.model.CanvasContext;
import com.instructure.canvasapi.model.Course;
import com.instructure.canvasapi.utilities.APIHelpers;
import com.instructure.canvasapi.utilities.CanvasCallback;
import com.instructure.canvasapi.utilities.CanvasRestAdapter;
import com.instructure.canvasapi.utilities.LinkHeaders;
import com.instructure.groupsdemo.R;
import com.instructure.groupsdemo.adapter.CanvasContextArrayAdapter;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by brady on 12/5/14.
 */
public class CourseListActivity extends BaseActivity {

    private static String TOKEN = "PUT_TOKEN_HERE";
    private static String DOMAIN = "PUT_DOMAIN_HERE";

    private CanvasCallback<Course[]> courseCallback;
    private ListView courseList;
    private CanvasContextArrayAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        courseList = (ListView)findViewById(R.id.listView);
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CanvasContext course = courseAdapter.getItem(position);
                Intent groupCategoryIntent = new Intent(CourseListActivity.this, GroupCategoryActivity.class);
                groupCategoryIntent.putExtra("course", (Parcelable) course);
                startActivity(groupCategoryIntent);
            }
        });
        courseAdapter = new CanvasContextArrayAdapter(this, 0, new ArrayList<CanvasContext>());
        courseList.setAdapter(courseAdapter);

        getActionBar().setTitle(getString(R.string.courses));

        setUpCanvasAPI(this);
        setupCallbacks();

        CourseAPI.getAllFavoriteCourses(courseCallback);
    }

    private void setupCallbacks() {
        courseCallback = new CanvasCallback<Course[]>(this) {
            @Override
            public void cache(Course[] courses) {

            }

            @Override
            public void firstPage(Course[] courses, LinkHeaders linkHeaders, Response response) {
                courseAdapter.addAll(courses);
            }

            @Override
            public boolean onFailure(RetrofitError retrofitError) {
                Toast.makeText(CourseListActivity.this, retrofitError.getMessage(), Toast.LENGTH_SHORT).show();
                return super.onFailure(retrofitError);
            }
        };
    }

    /**
     * This is all stuff that should only need to be called once for the entire project.
     */
    public void setUpCanvasAPI(Context context) {
        //Set up the Canvas Rest Adapter.
        boolean success = CanvasRestAdapter.setupInstance(context, TOKEN, DOMAIN);

        //Set up a default error delegate. This will be the same one for all API calls
        //You can override the default ErrorDelegate in any CanvasCallBack constructor.
        //In a real application, this should probably be a standalone class.
        APIHelpers.setDefaultErrorDelegateClass(context, context.getClass().getName());
    }

}
