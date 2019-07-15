package com.example.sbs.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.sbs.R;
import com.example.sbs.data.model.Block;
import com.example.sbs.data.model.Panchayat;

import java.util.List;

/**
 * Created by Manoj_Dwivedi on 19/5/2019.
 */
public class PanchayatListAdapter extends ArrayAdapter<Panchayat> {

    private final Activity context;
    private final List<Panchayat> items;


    public PanchayatListAdapter(Activity context, List<Panchayat> items) {
        super(context, R.layout.panchayat_list, items);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.items=items;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.panchayat_list, null,true);

        ((TextView) rowView.findViewById(R.id.lblPanchayatName)).setText(items.get(position).name);
        ((Button) rowView.findViewById(R.id.btnUpdate)).setTag(items.get(position).id);
        ((Button) rowView.findViewById(R.id.btnDelete)).setTag(items.get(position).id);

        return rowView;

    }

}
