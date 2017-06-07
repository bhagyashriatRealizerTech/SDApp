package com.realizer.sallado.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.sallado.R;
import com.realizer.sallado.model.DoctorListModel;
import com.realizer.sallado.model.MedicalPanelListModel;
import com.realizer.sallado.utils.ImageStorage;

import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class DoctorListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<MedicalPanelListModel> doctorList;

    public DoctorListAdapter(List<MedicalPanelListModel> doctorList, Context context){
        this.context = context;
        this.doctorList = doctorList;
        layoutInflater =LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return doctorList.size();
    }

    @Override
    public Object getItem(int position) {
        return doctorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.convertView = convertView;

        if (convertView == null) {
            this.convertView = layoutInflater.inflate(R.layout.doctor_list_item_layout, null);

            holder = new ViewHolder();
            holder.profileImage = (ImageView) this.convertView.findViewById(R.id.img_doctor);
            holder.name = (TextView) this.convertView.findViewById(R.id.txt_doctorname);
            holder.degree = (TextView) this.convertView.findViewById(R.id.txt_doctordegree);
            holder.experience = (TextView) this.convertView.findViewById(R.id.txt_doctorexperience);
            holder.description = (TextView) this.convertView.findViewById(R.id.txt_doctordesc);
            holder.fees = (TextView) this.convertView.findViewById(R.id.txt_fees);
            holder.adrs = (TextView) this.convertView.findViewById(R.id.txt_address);
            holder.timings = (TextView) this.convertView.findViewById(R.id.txt_time);


            holder.degree.setVisibility(View.GONE);
            holder.experience.setVisibility(View.GONE);


            holder.degree.setText(doctorList.get(position).getMedicalPanel().getDoctorDegree());
            holder.name.setText(doctorList.get(position).getMedicalPanel().getDoctorName());
            holder.experience.setText(doctorList.get(position).getMedicalPanel().getDoctorExperience());
            holder.description.setText(doctorList.get(position).getMedicalPanel().getDoctorDescription());
            holder.fees.setText(doctorList.get(position).getMedicalPanel().getDoctorConsultationFess());
            holder.adrs.setText(doctorList.get(position).getDoctorLocations().get(0).getLocationName());
            String time = doctorList.get(position).getDoctorAvalabilities().get(0).getWeekDayStart() + " To " +
            doctorList.get(position).getDoctorAvalabilities().get(0).getWeekDayEnd() + "\n" + doctorList.get(position).getDoctorAvalabilities().get(0).getAvailableStartTime()
                    +" - " +doctorList.get(position).getDoctorAvalabilities().get(0).getAvailableEndTime();
            holder.timings.setText(time);

            if(!doctorList.get(position).getMedicalPanel().getDoctorThumbnail().isEmpty()){
                ImageStorage.setThumbnail(holder.profileImage,doctorList.get(position).getMedicalPanel().getDoctorThumbnail());
            }

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }

        return this.convertView;
    }
    static class ViewHolder{

        ImageView profileImage;
        TextView name,degree,experience,description,fees,adrs,timings;
    }
}


