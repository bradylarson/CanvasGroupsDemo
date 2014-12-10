package com.instructure.groupsdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.instructure.canvasapi.model.GroupCategory;
import com.instructure.groupsdemo.R;

import java.util.List;

/**
 * Created by brady on 12/5/14.
 */
public class GroupCategoryAdapter extends ArrayAdapter<GroupCategory> {

    private List<GroupCategory> list;
    private LayoutInflater inflater;

    public GroupCategoryAdapter(Context context, int resource, List<GroupCategory> list) {
        super(context, resource, list);
        this.list = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public GroupCategory getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolder holder;
        // reuse views
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.group_row_layout, null);
            // configure view holder
            holder = new ViewHolder();
            holder.groupCategoryName = (TextView) rowView.findViewById(R.id.groupName);

            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.groupCategoryName.setText(getItem(position).getName());
        return rowView;
    }

    static class ViewHolder {
        public TextView groupCategoryName;
    }
}
