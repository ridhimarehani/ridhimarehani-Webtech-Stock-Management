package com.example.stocks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> {

    List<HomeSections> homeSectionsList;

    public HomeRecyclerAdapter(List<HomeSections> homeSectionsList) {
        this.homeSectionsList = homeSectionsList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater sectionLayoutInflater = LayoutInflater.from(parent.getContext());
        View sectionView = sectionLayoutInflater.inflate(R.layout.ticker_section, parent, false);
        View sectionView1 = sectionLayoutInflater.inflate(R.layout.portfolio_header, parent, false);
        return new HomeViewHolder(sectionView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {

        HomeSections section = homeSectionsList.get(position);
        String secHeading = section.getSectionName();
        List<List<String>> secItem = section.getSecItems();
        holder.sectionHeading.setText(secHeading);

//        setContentView(portConstraintLayout );


        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(secItem);
        holder.childRecView.setAdapter(childRecyclerAdapter);
//        holder.childRecView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }

    @Override
    public int getItemCount() {
        return homeSectionsList.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder{
        TextView sectionHeading;
        RecyclerView childRecView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionHeading = itemView.findViewById(R.id.sectionHeading);
            childRecView = itemView.findViewById(R.id.childRecView);
        }
    }
}
