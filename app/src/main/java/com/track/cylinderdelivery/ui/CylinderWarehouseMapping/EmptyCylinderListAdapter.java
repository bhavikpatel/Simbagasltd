package com.track.cylinderdelivery.ui.CylinderWarehouseMapping;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.R;

import java.util.ArrayList;
import java.util.HashMap;

public class EmptyCylinderListAdapter extends RecyclerView.Adapter<EmptyCylinderListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> cylinderList;
    Activity context;
    public EmptyCylinderListAdapter(ArrayList<HashMap<String, String>> dataList, Activity activity) {
        cylinderList=dataList;
        context=activity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCylinderNo;
        TextView txtValManuf;
        ImageView imgArrow;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            txtCylinderNo = (TextView) view.findViewById(R.id.txtCylinderNo);
            txtValManuf=(TextView)view.findViewById(R.id.txtValManuf);
            imgArrow=(ImageView)view.findViewById(R.id.imgArrow);
            imgArrow.setVisibility(View.GONE);
            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgArrow.getTag ();
                    /*Intent intent=new Intent(context, CylinderDetailActivity.class);
                    intent.putExtra("editData",cylinderList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);*/
                }
            });
        }
    }

    @NonNull
    @Override
    public EmptyCylinderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filled_cylinder_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyCylinderListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
       // holder.getTextView().setText(localDataSet[position]);
        holder.imgArrow.setTag(position);
        holder.txtCylinderNo.setText(cylinderList.get(position).get("cylinderNo"));
        holder.txtValManuf.setText("Warehouse Name: "+cylinderList.get(position).get("warehouseName")+
                "\nCreated By: "+cylinderList.get(position).get("createdByName")+
                "\nCreated Date: "+ cylinderList.get(position).get("createdDateStr"));
       /* holder.txtValManuf.setText(cylinderList.get(position).get("companyName")+
                ","+cylinderList.get(position).get("address1")+
                ","+cylinderList.get(position).get("address2")+
                ","+cylinderList.get(position).get("city")+
                ","+cylinderList.get(position).get("county")+
                ","+cylinderList.get(position).get("zipCode"));*/
    }

    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return cylinderList.size();
    }
}
