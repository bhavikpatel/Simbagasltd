package com.track.cylinderdelivery.ui.acknowledge;

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
import com.track.cylinderdelivery.ui.company.CompanyDetail;
import com.track.cylinderdelivery.ui.user.UserListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class AcknowledgeListAdapter extends RecyclerView.Adapter<AcknowledgeListAdapter.ViewHolder>{
    ArrayList<HashMap<String, String>> listData=new ArrayList<HashMap<String, String>>();
    Activity context;
    public AcknowledgeListAdapter(ArrayList<HashMap<String, String>> dataSet,Activity activity) {
        listData = dataSet;
        context=activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_acknoweledge, parent, false);
        return new AcknowledgeListAdapter.ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userName=listData.get(position).get("userName");
        holder.tvName.setText(userName);
        holder.imgAction.setTag(position);
        holder.rv_background.setTag(position);
        holder.companyName.setText(MySingalton.convertString(listData.get(position).get("companyName")));
        holder.tvRemark.setText(MySingalton.convertString(listData.get(position).get("remark")));
        holder.tvStatus.setText(MySingalton.convertString(listData.get(position).get("status")));
        String createdBy="Created by: "+MySingalton.convertString(listData.get(position).get("createdByName"));
        holder.tvCreatedby.setText(createdBy);
        String CreatedDate="Created Date: "+MySingalton.convertString(listData.get(position).get("createdDateStr"));
        holder.tvCreated.setText(CreatedDate);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private int backgroundIndex = 0;
        TextView tvName,companyName,tvRemark,tvStatus,tvCreatedby,tvCreated;
        ImageView imgAction;
        RelativeLayout rv_background;
        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.backgroundIndex = backgroundIndex;
            tvName=itemView.findViewById(R.id.tvName);
            companyName=itemView.findViewById(R.id.tvCompanyName);
            tvRemark=itemView.findViewById(R.id.tvRemark);
            tvStatus=itemView.findViewById(R.id.tvStatus);
            tvCreatedby=itemView.findViewById(R.id.tvCreatedby);
            tvCreated=itemView.findViewById(R.id.tvCreated);
            imgAction=itemView.findViewById(R.id.imgAction);
            rv_background=itemView.findViewById(R.id.rv_background);
            rv_background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=(int)v.getTag();
                    Log.d("pos==>",pos+"");
                    Intent intent=new Intent(context, AcknowledgeDetailActivity.class);
                    intent.putExtra("editData",listData.get(pos));
                    context.startActivity(intent);
                }
            });
            imgAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=(int)v.getTag();
                    Log.d("pos==>",pos+"");
                    Intent intent=new Intent(context, AcknowledgeDetailActivity.class);
                    intent.putExtra("editData",listData.get(pos));
                    context.startActivity(intent);
                }
            });
        }
    }
}
