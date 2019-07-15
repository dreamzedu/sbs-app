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
import com.example.sbs.data.model.ResponsePercentage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Manoj_Dwivedi on 19/5/2019.
 */
public class ReportListAdapter extends ArrayAdapter<ResponsePercentage> {

    private final Activity context;
    private final List<ResponsePercentage> items;
    HashMap<Integer, String> questionsMap;

    public ReportListAdapter(Activity context, List<ResponsePercentage> items, HashMap<Integer, String> questionsMap) {
        super(context, R.layout.report_list, items);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.items=items;
        this.questionsMap = questionsMap;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.report_list, null,true);

        //((TextView) rowView.findViewById(R.id.lblQtext)).setText(items.get(position).responsePercentage + "");
        if(questionsMap.containsKey(items.get(position).questionId))
        {
            ((TextView) rowView.findViewById(R.id.lblQtext)).setText(questionsMap.get(items.get(position).questionId));
        }
        else
        {
           ((TextView) rowView.findViewById(R.id.lblQtext)).setText("Question text missing");
        }
        ((TextView) rowView.findViewById(R.id.lblPercentage)).setText(items.get(position).responsePercentage + " %");

        return rowView;

    }

}
