package com.example.sbs.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sbs.R;
import com.example.sbs.data.model.Survey;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Manoj_Dwivedi on 19/5/2019.
 */
public class SurveySyncListAdapter extends ArrayAdapter<Survey> {

    private final Activity context;
    private final List<Survey> items;
    HashMap<String, String> panchayatMap;


    public SurveySyncListAdapter(Activity context, List<Survey> items, HashMap<String, String> panchayatMap) {
        super(context, R.layout.survey_sync_list, items);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.items=items;
        this.panchayatMap = panchayatMap;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.survey_sync_list, null,true);

        TextView txtName = (TextView) rowView.findViewById(R.id.lblBenefName);
        TextView txtSurveyNo = (TextView) rowView.findViewById(R.id.lblSurveyNo);
        TextView txtFather = (TextView) rowView.findViewById(R.id.lblBenefFatherName);
        TextView txtPanchayat = (TextView) rowView.findViewById(R.id.lblBenefPanchayat);

        rowView.findViewById(R.id.btnSync).setTag(items.get(position).id);
        rowView.findViewById(R.id.btnDelete).setTag(items.get(position).id);
        rowView.findViewById(R.id.btnUpdate).setTag(items.get(position).id);

        txtName.setText(items.get(position).beneficiary.name);
        txtFather.setText(items.get(position).beneficiary.fatherOrHusbandName);
        txtSurveyNo.setText(items.get(position).id);
        txtPanchayat.setText(panchayatMap!=null? panchayatMap.get(Integer.parseInt(items.get(position).panchayat)): items.get(position).panchayat);


        return rowView;

    }

}
