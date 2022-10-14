package com.track.cylinderdelivery.ui.cylinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.company.CompanyDetail;

import java.util.ArrayList;
import java.util.HashMap;

public class CylinderScanedListAdapter extends RecyclerView.Adapter<CylinderScanedListAdapter.ViewHolder>{

    ArrayList<String> qrcodeList;
    CylinderQRActivity context;
    public CylinderScanedListAdapter(ArrayList<String> dataList, CylinderQRActivity activity) {
        qrcodeList=dataList;
        context=activity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQrCodeName;
        ImageView imgActioinQR;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvQrCodeName = (TextView) view.findViewById(R.id.tvQrCodeName);
            imgActioinQR=(ImageView)view.findViewById(R.id.imgActioinQR);
            imgActioinQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgActioinQR.getTag ();
                    context.qcodeList.remove(pos);
                    context.cylinderScanedListAdapter.notifyDataSetChanged();
                    context.txtCount.setText(context.qcodeList.size()+"");
                }
            });
        }
    }

    @NonNull
    @Override
    public CylinderScanedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cylinder_qr_code, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CylinderScanedListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
       // holder.getTextView().setText(localDataSet[position]);
        holder.imgActioinQR.setTag(position);
        holder.tvQrCodeName.setText(qrcodeList.get(position));
    }

    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return qrcodeList.size();
    }
}
