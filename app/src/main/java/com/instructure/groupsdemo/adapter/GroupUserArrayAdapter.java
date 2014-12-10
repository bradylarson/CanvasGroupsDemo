package com.instructure.groupsdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.instructure.canvasapi.model.User;
import com.instructure.groupsdemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by brady on 12/4/14.
 */
public class GroupUserArrayAdapter extends ArrayAdapter<User> {

    private List<User> users;
    private LayoutInflater inflater;
    private Context context;
    private boolean shouldShowAvatar;

    public GroupUserArrayAdapter(Context context, int resource, List<User> users, boolean shouldShowAvatar) {
        super(context, resource, users);

        this.users = users;
        this.context = context;
        this.shouldShowAvatar = shouldShowAvatar;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addUsers(List<User> newUsers) {
        users.addAll(newUsers);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        // reuse views
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.group_user_row_layout, null);
            // configure view holder
            holder = new ViewHolder();
            holder.userName = (TextView) rowView.findViewById(R.id.userName);
            holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);

            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.userName.setText(getItem(position).getShortName());
        if(shouldShowAvatar) {
            Picasso.with(context).load(getItem(position).getAvatarURL()).into(holder.avatar);
        }
        else {
            holder.avatar.setVisibility(View.GONE);
        }
        return rowView;
    }

    static class ViewHolder {
        public ImageView avatar;
        public TextView userName;
    }
}
