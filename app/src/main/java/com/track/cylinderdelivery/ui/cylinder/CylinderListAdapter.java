package com.track.cylinderdelivery.ui.cylinder;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.company.CompanyDetail;

import java.util.ArrayList;
import java.util.HashMap;

public class CylinderListAdapter extends RecyclerView.Adapter<CylinderListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> cylinderList;
    Activity context;
    public CylinderListAdapter(ArrayList<HashMap<String, String>> dataList, Activity activity) {
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
        RelativeLayout rv_row;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            txtCylinderNo = (TextView) view.findViewById(R.id.txtCylinderNo);
            txtValManuf=(TextView)view.findViewById(R.id.txtValManuf);
            imgArrow=(ImageView)view.findViewById(R.id.imgArrow);
            rv_row=(RelativeLayout)view.findViewById(R.id.rv_row);
            rv_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) rv_row.getTag ();
                    Intent intent=new Intent(context, CylinderDetailActivity.class);
                    intent.putExtra("editData",cylinderList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                }
            });
            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgArrow.getTag ();
                    Intent intent=new Intent(context, CylinderDetailActivity.class);
                    intent.putExtra("editData",cylinderList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                }
            });
        }
    }

    @NonNull
    @Override
    public CylinderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cylinder_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CylinderListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
       // holder.getTextView().setText(localDataSet[position]);
        holder.imgArrow.setTag(position);
        holder.rv_row.setTag(position);
        holder.txtCylinderNo.setText(cylinderList.get(position).get("cylinderNo"));
       // holder.txtValManuf.setText(cylinderList.get(position).get("companyName"));
        String address="";
        if(cylinderList.get(position).get("companyName").length()!=0 && !cylinderList.get(position).get("companyName").equals("null")){
            address+=cylinderList.get(position).get("companyName");
        }
        if(cylinderList.get(position).get("address1").length()!=0 && !cylinderList.get(position).get("address1").equals("null")){
            address+=","+cylinderList.get(position).get("address1");
        }
        if(cylinderList.get(position).get("address2").length()!=0 && !cylinderList.get(position).get("address2").equals("null")){
            address+=","+cylinderList.get(position).get("address2");
        }
        if(cylinderList.get(position).get("city").length()!=0 && !cylinderList.get(position).get("city").equals("null")){
            address+=","+cylinderList.get(position).get("city");
        }
        if(cylinderList.get(position).get("county").length()!=0 && !cylinderList.get(position).get("county").equals("null")){
            address+=","+cylinderList.get(position).get("county");
        }
        if(cylinderList.get(position).get("zipCode").length()!=0 && !cylinderList.get(position).get("zipCode").equals("null")){
            address+="-"+cylinderList.get(position).get("zipCode");
        }

        holder.txtValManuf.setText(address);
    }

    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return cylinderList.size();
    }
}
