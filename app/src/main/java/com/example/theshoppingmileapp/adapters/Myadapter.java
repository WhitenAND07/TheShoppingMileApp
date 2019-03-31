package com.example.theshoppingmileapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.theshoppingmileapp.R;
import com.example.theshoppingmileapp.dominio.ListPlace;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder> {

   private List<ListPlace> listPlaces;
    private Context context;

    public Myadapter(List<ListPlace> listPlaces, Context context) {
        this.listPlaces = listPlaces;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_places, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int position) {

        final ListPlace listPlace = listPlaces.get(position);
        viewHolder.textViewHead.setText(listPlace.getHead());
        viewHolder.textViewDesc.setText(listPlace.getDesc());

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View view){
                Toast.makeText(context,"Se ha clicado " + listPlace.getHead(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.textViewHead) TextView textViewHead;
        @BindView(R.id.textViewDescription) TextView textViewDesc;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }


}
