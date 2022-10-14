package com.track.cylinderdelivery.ui.company;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.user.UserDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> companyList;
    Activity context;
    public CompanyListAdapter(ArrayList<HashMap<String, String>> dataList, Activity activity) {
        companyList=dataList;
        context=activity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtAddress;
        ImageView imgArrow;
        TextView txtAdminName,txtStatus;
        RelativeLayout rv_row;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            txtName = (TextView) view.findViewById(R.id.txtName);
            rv_row=(RelativeLayout)view.findViewById(R.id.rv_row);
            txtAddress=(TextView)view.findViewById(R.id.txtAddress);
            imgArrow=(ImageView)view.findViewById(R.id.imgArrow);
            txtAdminName=(TextView)view.findViewById(R.id.txtAdminName);
            txtStatus=(TextView) view.findViewById(R.id.txtStatus);
            rv_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) rv_row.getTag ();
                    Intent intent=new Intent(context, CompanyDetail.class);
                    intent.putExtra("editData",companyList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                }
            });
            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgArrow.getTag ();
                    Intent intent=new Intent(context, CompanyDetail.class);
                    intent.putExtra("editData",companyList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                }
            });
        }
    }

    @NonNull
    @Override
    public CompanyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_company, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
       // holder.getTextView().setText(localDataSet[position]);
        holder.imgArrow.setTag(position);
        holder.rv_row.setTag(position);
        holder.txtStatus.setText(companyList.get(position).get("status"));
        holder.txtName.setText(companyList.get(position).get("companyName"));
        holder.txtAdminName.setText("User Name: "+companyList.get(position).get("adminName"));
        /*+"\n"+"Company Type: "+companyList.get(position).get("companyType"));*/

        String address="";
        if(companyList.get(position).get("address1").trim().length()!=0
            && !companyList.get(position).get("address1").trim().equals("null")){
            address+=companyList.get(position).get("address1");
        }
        if(companyList.get(position).get("address2").trim().length()!=0
                && !companyList.get(position).get("address2").trim().equals("null")){
            address+=" ,"+companyList.get(position).get("address2");
        }
        if(companyList.get(position).get("city").trim().length()!=0
                && !companyList.get(position).get("city").trim().equals("null")){
            address+=" ,"+companyList.get(position).get("city");
        }
        if(companyList.get(position).get("county").trim().length()!=0
                && !companyList.get(position).get("county").trim().equals("null")){
            address+=" ,"+companyList.get(position).get("county");
        }
        if(companyList.get(position).get("zipCode").trim().length()!=0
                && !companyList.get(position).get("zipCode").trim().equals("null")){
            address+=" ,"+companyList.get(position).get("zipCode");
        }
        holder.txtAddress.setText(address);
    }

    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return companyList.size();
    }
}
