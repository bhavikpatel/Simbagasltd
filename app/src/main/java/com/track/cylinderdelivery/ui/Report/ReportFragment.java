package com.track.cylinderdelivery.ui.Report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.product.AddProductActivity;

public class ReportFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reportr, container, false);
       // final TextView textView = root.findViewById(R.id.text_home);
       // final Button button=root.findViewById(R.id.button);
        Button btnAddProduct=root.findViewById(R.id.btnAddProduct);
        RecyclerView recyclerView=root.findViewById(R.id.rv_product_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddProductActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return root;
    }
}