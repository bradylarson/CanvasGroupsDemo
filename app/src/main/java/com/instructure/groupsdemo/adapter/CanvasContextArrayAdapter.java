package com.instructure.groupsdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.instructure.canvasapi.model.CanvasContext;
import com.instructure.groupsdemo.R;

import java.util.List;

/**
 * Created by brady on 12/4/14.
 */
public class CanvasContextArrayAdapter extends ArrayAdapter<CanvasContext> {
    private List<CanvasContext> list;
    private LayoutInflater inflater;
    private SparseBooleanArray mSelectedItemsIds;

    public CanvasContextArrayAdapter(Context context, int resource, List<CanvasContext> list) {
        super(context, resource, list);
        this.list = list;
        mSelectedItemsIds = new SparseBooleanArray();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void toggleSelection(int position)
    {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value)
    {
        if(value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();// mSelectedCount;
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void setCanvasContexts(List<CanvasContext> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addCanvasContext(CanvasContext canvasContext) {
        list.add(canvasContext);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CanvasContext getItem(int position) {
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
            holder.groupName = (TextView) rowView.findViewById(R.id.groupName);

            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.groupName.setText(getItem(position).getName());
        rowView.setBackgroundColor(mSelectedItemsIds.get(position)? 0x9934B5E4: Color.TRANSPARENT);
        return rowView;
    }

    static class ViewHolder {
        public TextView groupName;
    }
}
