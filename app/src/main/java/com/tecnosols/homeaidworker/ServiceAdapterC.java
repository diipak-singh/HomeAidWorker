package com.tecnosols.homeaidworker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServiceAdapterC extends RecyclerView.Adapter<ServiceAdapterC.serviceViewHolder> {
    ArrayList<ServiceDetail> serviceList = new ArrayList<>();

    public ServiceAdapterC(ArrayList<ServiceDetail> serviceList, Context ctx) {
        this.serviceList = serviceList;

    }


    @NonNull
    @Override
    public serviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_completed_layout, parent, false);
        ServiceAdapterC.serviceViewHolder serviceViewHolder = new ServiceAdapterC.serviceViewHolder(view);

        return serviceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull serviceViewHolder holder, int position) {
        holder.sCategory.setText(serviceList.get(position).getItemCatg());
        holder.sName.setText(serviceList.get(position).getItemName());
        holder.sPrice.setText("Total Price: ₹" + serviceList.get(position).getItemPrice());
        holder.sOtherDetail.setText("Other Details: " + serviceList.get(position).getOtherDetail());
        holder.sExpectedService.setText("Expected on: " + serviceList.get(position).getServiceDay() + " at " + serviceList.get(position).getServiceTime());
        holder.sBookedOn.setText("Booked on: " + serviceList.get(position).getCurrentDay() + " at " + serviceList.get(position).getCurrentTime());
        holder.rating.setText("Rating: " + serviceList.get(position).getRating());

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class serviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView sCategory, sName, sPrice, sOtherDetail, sExpectedService, sBookedOn, rating;

        public serviceViewHolder(@NonNull View itemView) {
            super(itemView);

            sCategory = itemView.findViewById(R.id.textView_serviceCatg);
            sName = itemView.findViewById(R.id.textView_serviceItemname);
            sPrice = itemView.findViewById(R.id.textView_servicePrice);
            sOtherDetail = itemView.findViewById(R.id.textView_serviceAnyOtherDetail);
            sExpectedService = itemView.findViewById(R.id.textView_expectedService);
            sBookedOn = itemView.findViewById(R.id.textView_serviceBooked);
            rating = itemView.findViewById(R.id.textView_serviceRating);


            //claout.setOnLongClickListener(mainActivity);


        }

        @Override
        public void onClick(View v) {

            /*int adapterPosition = getAdapterPosition();
            if (!itemStateArray.get(adapterPosition, false)) {
                checkBox.setChecked(true);
                itemStateArray.put(adapterPosition, true);
                mainActivity.testing(1,getAdapterPosition());
            } else {
                checkBox.setChecked(false);
                itemStateArray.put(adapterPosition, false);
                mainActivity.testing(0,getAdapterPosition());
            }*/

            // mainActivity.prepareSelection(v, getAdapterPosition());


        }
    }
}
