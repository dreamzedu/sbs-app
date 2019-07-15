package com.example.sbs.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sbs.R;
import com.example.sbs.data.model.Survey;
import com.example.sbs.data.model.SurveyBasicDetail;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Manoj_Dwivedi on 19/5/2019.
 */
public class SurveyListAdapter extends ArrayAdapter<SurveyBasicDetail> {

    private final Activity context;
    private final List<SurveyBasicDetail> items;


    public SurveyListAdapter(Activity context, List<SurveyBasicDetail> items) {
        super(context, R.layout.survey_list, items);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.items=items;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.survey_list, null,true);

        TextView txtName = (TextView) rowView.findViewById(R.id.lblBenefName);
        TextView txtSurveyNo = (TextView) rowView.findViewById(R.id.lblSurveyNo);
        TextView txtFather = (TextView) rowView.findViewById(R.id.lblBenefFatherName);
        TextView txtPanchayat = (TextView) rowView.findViewById(R.id.lblBenefPanchayat);

        txtName.setText(items.get(position).benfName);
        txtFather.setText(items.get(position).benfHead);
        txtSurveyNo.setText(items.get(position).id);
        txtPanchayat.setText(items.get(position).panchayat);

        ((TextView) rowView.findViewById(R.id.lblCreatedBy)).setText(items.get(position).createdBy);
        ((TextView) rowView.findViewById(R.id.lblTakenAT)).setText(items.get(position).createdAtLocation);
        return rowView;

    }

}
