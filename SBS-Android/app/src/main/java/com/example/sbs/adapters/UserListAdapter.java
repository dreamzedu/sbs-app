package com.example.sbs.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.sbs.R;
import com.example.sbs.data.model.Survey;
import com.example.sbs.data.model.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Manoj_Dwivedi on 19/5/2019.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private final Activity context;
    private final List<User> items;


    public UserListAdapter(Activity context, List<User> items) {
        super(context, R.layout.user_list, items);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.items=items;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.user_list, null,true);

        ((TextView) rowView.findViewById(R.id.lblName)).setText(items.get(position).name);
        ((TextView) rowView.findViewById(R.id.lblPhone)).setText(items.get(position).phone);
        ((TextView) rowView.findViewById(R.id.lblUsername)).setText(items.get(position).userid);
        ((TextView) rowView.findViewById(R.id.lblPassword)).setText(items.get(position).password);
        ((TextView) rowView.findViewById(R.id.lblUsertype)).setText(items.get(position).roles.get(0).name);
        ((Button) rowView.findViewById(R.id.btnUpdate)).setTag(items.get(position).id);
        ((Button) rowView.findViewById(R.id.btnDelete)).setTag(items.get(position).id);

        return rowView;

    }

}
