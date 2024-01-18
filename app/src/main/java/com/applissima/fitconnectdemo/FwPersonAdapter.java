package com.applissima.fitconnectdemo;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ilkerkuscan on 11/05/17.
 */

public class FwPersonAdapter extends RecyclerView.Adapter<FwPersonAdapter.ViewHolder> {

    private List<FitWorkPersonCard> mCardList;
    private Context mContext;
    private int mGridSize;

    public FwPersonAdapter(Context context, List<FitWorkPersonCard> cardList, int gridSize){
        mContext = context;
        mCardList = cardList;
        mGridSize = gridSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView fitPersonCardView;
        CircleImageView personImageView;
        TextView nicknameTextView;
        TextView currZoneTextView;
        TextView currPerfTextView;
        TextView totalCalTextView;
        TextView currHrTextView;

        ImageView hrImg;
        ImageView totCalImg;

        public ViewHolder(View itemView) {
            super(itemView);

            fitPersonCardView = (CardView) itemView.findViewById(R.id.card_view);
            personImageView = (CircleImageView) itemView.findViewById(R.id.personImage);
            nicknameTextView = (TextView) itemView.findViewById(R.id.nicknameTextView);
            currZoneTextView = (TextView) itemView.findViewById(R.id.currZoneTextView);
            currPerfTextView = (TextView) itemView.findViewById(R.id.currPerfTextView);
            totalCalTextView = (TextView) itemView.findViewById(R.id.totalCalTextView);
            currHrTextView = (TextView) itemView.findViewById(R.id.currHrTextView);

            hrImg = (ImageView) itemView.findViewById(R.id.heartRateImage);
            totCalImg = (ImageView) itemView.findViewById(R.id.calfireImage);

            resizeItems();

        }

        public void resizeItems(){
            // Resize items
            // Card size
            ViewGroup.LayoutParams params = fitPersonCardView.getLayoutParams();
            params.height = (int) (StaticVariables.appHeight/ (mGridSize*1.12));
            fitPersonCardView.requestLayout();

            // Profile image
            ViewGroup.LayoutParams params2 = personImageView.getLayoutParams();
            params2.height = (int) (params2.height / mGridSize);
            params2.width = (int) (params2.width / mGridSize);
            personImageView.requestLayout();

            // Other images (hr & cal)
            ViewGroup.LayoutParams params3 = hrImg.getLayoutParams();
            params3.height = (int) (params3.height / mGridSize);
            params3.width = (int) (params3.width / mGridSize);
            hrImg.requestLayout();

            ViewGroup.LayoutParams params4 = totCalImg.getLayoutParams();
            params4.height = (int) (params4.height / mGridSize);
            params4.width = (int) (params4.width / mGridSize);
            totCalImg.requestLayout();

            // Textviews
            float textSizePrm = mGridSize * 1.5f;
            float nickNameTextSize = nicknameTextView.getTextSize();
            float currZoneTextSize = currZoneTextView.getTextSize();
            float currPerfTextSize = currPerfTextView.getTextSize();
            float totalCalTextSize = totalCalTextView.getTextSize();
            float currHrTextSize = currHrTextView.getTextSize();
            if(nicknameTextView.getText().length()>15){
                nicknameTextView.setTextSize(nickNameTextSize/(textSizePrm*1.2f));
            } else if(nicknameTextView.getText().length()>22){
                nicknameTextView.setTextSize(nickNameTextSize/(textSizePrm*1.4f));
            } else {
                nicknameTextView.setTextSize(nickNameTextSize / textSizePrm);
            }
            currZoneTextView.setTextSize(currZoneTextSize/textSizePrm);
            currPerfTextView.setTextSize(currPerfTextSize/textSizePrm);
            totalCalTextView.setTextSize(totalCalTextSize/textSizePrm);
            currHrTextView.setTextSize(currHrTextSize/textSizePrm);

        }

    }

    @Override
    public FwPersonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fitperson_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FwPersonAdapter.ViewHolder holder, int position) {

        FitWorkPersonCard card = mCardList.get(position);
        holder.fitPersonCardView
                .setCardBackgroundColor(ContextCompat.getColor(mContext, getZoneColor(card.getCurrentZone())));

        Picasso.with(mContext)
                .load(card.getImageUrl())
                .noFade()
                .placeholder(card.getGender()==1?R.drawable.male:R.drawable.female)
                //.placeholder(R.drawable)
                .into(holder.personImageView);

        holder.nicknameTextView.setText(card.getNickName());
        holder.currZoneTextView.setText("Zone " + String.valueOf(card.getCurrentZone()));
        holder.currPerfTextView.setText("%" + String.valueOf(card.getCurrentPerf()));
        holder.totalCalTextView.setText(String.valueOf(card.getCaloriesBurned()));
        holder.currHrTextView.setText(String.valueOf(card.getCurrentHr()));

    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    public int getZoneColor(int currentZone){
        switch (currentZone){
            case 1 : return R.color.userGrey;
            case 2 : return R.color.userBlue;
            case 3 : return R.color.userGreen;
            case 4 : return R.color.userOrange;
            case 5 : return R.color.userRed;
            default: return R.color.userWhite;
        }
    }
}
