package com.track.cylinderdelivery.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardList3Adapter extends RecyclerView.Adapter<DashboardList3Adapter.ViewHolder>{
    ArrayList<HashMap<String, String>> listData=new ArrayList<HashMap<String, String>>();
    Activity context;
    public DashboardList3Adapter(ArrayList<HashMap<String, String>> dataSet, Activity activity) {
        listData = dataSet;
        context=activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dashboard, parent, false);
        return new DashboardList3Adapter.ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userName=listData.get(position).get("cylinderNo");
        holder.tvName.setText("Cylinder No: "+userName);
        holder.imgAction.setTag(position);
        holder.rv_background.setTag(position);
        holder.companyName.setText("Client DN: "+MySingalton.convertString(listData.get(position).get("soNumber")));
        holder.tvAddress.setText("Deliver Date: "+MySingalton.convertString(listData.get(position).get("strDeliverDate")));
       /* holder.tvAddress.setText("DN Number: "+MySingalton.convertString(listData.get(position).get("dnNumber"))+"\n"+
                "Holed Date: "+MySingalton.convertString(listData.get(position).get("strHoledDate")));*/
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private int backgroundIndex = 0;
        TextView tvName,companyName,tvAddress;
        ImageView imgAction;
        RelativeLayout rv_background;
        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.backgroundIndex = backgroundIndex;
            tvName=itemView.findViewById(R.id.tvName);
            companyName=itemView.findViewById(R.id.tvCompanyName);
            tvAddress=itemView.findViewById(R.id.tvRemark);
            imgAction=itemView.findViewById(R.id.imgAction);
            rv_background=itemView.findViewById(R.id.rv_background);
            rv_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=(int)v.getTag();
                    Log.d("pos==>",pos+"");
                    Intent intent=new Intent(context, Detial3Activity.class);
                    intent.putExtra("editData",listData.get(pos));
                    context.startActivity(intent);
                }
            });
            imgAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=(int)v.getTag();
                    Log.d("pos==>",pos+"");
                    Intent intent=new Intent(context, Detial3Activity.class);
                    intent.putExtra("editData",listData.get(pos));
                    context.startActivity(intent);
                }
            });
        }
    }
}
