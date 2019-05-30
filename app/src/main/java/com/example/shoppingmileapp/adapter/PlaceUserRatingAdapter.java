package com.example.shoppingmileapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shoppingmileapp.R;
import com.example.shoppingmileapp.dominio.PlaceUserRating;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class PlaceUserRatingAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     *
     * Reference Variable for context
     */
    private Context mContext;
    private ArrayList<PlaceUserRating> mPlaceUserRatingArrayList;

    /**
     *
     * Public Constructor
     */
    public PlaceUserRatingAdapter(Context context, ArrayList<PlaceUserRating> placeUserRatingArrayList) {
        mContext = context;
        mPlaceUserRatingArrayList = placeUserRatingArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceUserRatingViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.place_user_rating_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PlaceUserRatingViewHolder)holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return mPlaceUserRatingArrayList.size();
    }

    private class PlaceUserRatingViewHolder extends RecyclerView.ViewHolder{
        /**
         *
         * all view reference
         */
        private TextView mAuthorNameTextView;
        private CircleImageView mAuthorProfilePictureUrlImageView;
        private TextView mPlaceRatingRelativeTimeDescriptionTextView;
        private MaterialRatingBar mPlaceRatingBarView;
        private TextView mAuthorReviewTextTextView;

        private PlaceUserRatingViewHolder(View itemView) {
            super(itemView);

            mAuthorNameTextView =  itemView.findViewById(R.id.user_name_text_view);
            mAuthorProfilePictureUrlImageView =  itemView
                    .findViewById(R.id.user_profile_image_view);
            mPlaceRatingRelativeTimeDescriptionTextView =  itemView
                    .findViewById(R.id.user_rating_time_age_text_view);
            mPlaceRatingBarView = itemView.findViewById(R.id.user_rating);
            mAuthorReviewTextTextView =  itemView.findViewById(R.id.user_rating_description);
        }

        private void bindView(int position){
            mAuthorNameTextView.setText(mPlaceUserRatingArrayList.get(position).getAuthorName());
            mAuthorNameTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                    "Roboto-Regular.ttf"));

            Glide.with(mContext)
                    .load(mPlaceUserRatingArrayList.get(position).getAuthorProfilePictureUrl())
                    .into(mAuthorProfilePictureUrlImageView);

            mPlaceRatingRelativeTimeDescriptionTextView.setText(
                    mPlaceUserRatingArrayList.get(position).getPlaceRatingRelativeTimeDescription());
            mPlaceRatingRelativeTimeDescriptionTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                    "Roboto-Regular.ttf"));
            String ratingValue = String.valueOf(mPlaceUserRatingArrayList.get(position).getAuthorPlaceRating());
            mPlaceRatingBarView.setRating(Float.parseFloat(ratingValue));
            mAuthorReviewTextTextView.setText(
                    mPlaceUserRatingArrayList.get(position).getAuthorReviewText());
            mAuthorReviewTextTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                    "Roboto-Regular.ttf"));
        }
    }
}