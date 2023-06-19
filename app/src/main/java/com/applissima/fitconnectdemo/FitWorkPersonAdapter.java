package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 30/01/17.
 */

/*
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.exceptions.RealmException;





public class FitWorkPersonAdapter extends RealmRecyclerViewAdapter<FitWorkPerson, FitWorkPersonAdapter.FitWorkPersonHolder>{

    //private List<FitWorkPerson> fitWorkPersonList;
    //private Context context;
    public static float appHeight;
    private static int gridSize;

    private final FitWorkActivity mFitWorkActivity;

    public FitWorkPersonAdapter(FitWorkActivity fitWorkActivity, OrderedRealmCollection<FitWorkPerson> data){
        super(fitWorkActivity, data, true);
        //super(data, true);

        this.mFitWorkActivity = fitWorkActivity;
        //this.fitWorkPersonList = fitWorkPersonList;
        //this.context = context;

        gridSize = getGridSize();
        appHeight = (Resources.getSystem().getDisplayMetrics().heightPixels);

    }

    public static class FitWorkPersonHolder extends RecyclerView.ViewHolder{

        CardView fitPersonCardView;
        CircularImageView personImageView;
        TextView nicknameTextView;
        TextView currZoneTextView;
        TextView currPerfTextView;
        TextView totalCalTextView;
        TextView currHrTextView;

        ImageView hrImg;
        ImageView totCalImg;

        public FitWorkPersonHolder(View itemView) {
            super(itemView);

            fitPersonCardView = (CardView) itemView.findViewById(R.id.card_view);
            personImageView = (CircularImageView) itemView.findViewById(R.id.personImage);
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

            //int gridSize = FitWorkActivity.gridSize;
            // Resize items
            // Card size
            ViewGroup.LayoutParams params = fitPersonCardView.getLayoutParams();
            params.height = (int) (appHeight/ (gridSize*1.1));
            fitPersonCardView.requestLayout();

            // Profile image
            ViewGroup.LayoutParams params2 = personImageView.getLayoutParams();
            params2.height = (int) (params2.height / gridSize);
            params2.width = (int) (params2.width / gridSize);
            personImageView.requestLayout();

            // Other images (hr & cal)
            ViewGroup.LayoutParams params3 = hrImg.getLayoutParams();
            params3.height = (int) (params3.height / gridSize);
            params3.width = (int) (params3.width / gridSize);
            hrImg.requestLayout();

            ViewGroup.LayoutParams params4 = totCalImg.getLayoutParams();
            params4.height = (int) (params4.height / gridSize);
            params4.width = (int) (params4.width / gridSize);
            totCalImg.requestLayout();

            // Textviews
            float textSizePrm = gridSize * 1.5f;
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
    public FitWorkPersonHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fitperson_cardview, viewGroup, false);
        FitWorkPersonHolder holder = new FitWorkPersonHolder(v);
        return holder;
    }


    @Override
    public void onBindViewHolder(final FitWorkPersonHolder holder, int position) {


        try {

            final FitWorkPerson workPerson = getData().get(position);

            holder.fitPersonCardView.setCardBackgroundColor(ContextCompat.getColor(mFitWorkActivity, getZoneColor(workPerson.getCurrentZone())));

            try {
                if (workPerson.getFitPerson() != null) {

                    Picasso.with(mFitWorkActivity)
                            .load(workPerson.getFitPerson().getPhotoURL())
                            .placeholder(workPerson.getFitPerson().getGender()==1?R.drawable.male:R.drawable.female)
                            //.placeholder(R.drawable)
                            .into(holder.personImageView);

                    */
/*byte[] imageData = workPerson.getFitPerson().getPhoto();
                    if (imageData != null && imageData.length > 0) {
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        //Drawable image = new BitmapDrawable(mFitWorkActivity.getResources(),
                        //     BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
                        //holder.personImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));

                        //Log.i("decodeByteArray", "decoded");
                    }*//*


                    holder.fitPersonCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mFitWorkActivity.showWorkSummary(workPerson.getActHrSensorId());
                        }
                    });

                    */
/*if(!workPerson.getFitPerson().isTestUser()){
                        Date now = Calendar.getInstance().getTime();
                        Date lastUpdateTime = workPerson.getHrLastDataUpdateDate();
                        Calendar nowCal = Calendar.getInstance();
                        nowCal.setTime(now);
                        Calendar updCal = Calendar.getInstance();
                        updCal.setTime(lastUpdateTime);
                        Log.i("PERSONDATA", "PERSONDATA LastUpdateDate: "
                                + AppUtils.getDateString(lastUpdateTime)
                                + ", Now: " + AppUtils.getDateString(now)
                                + ", Difference: " + String.valueOf(nowCal.compareTo(updCal)));
                    }*//*

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.nicknameTextView.setText(workPerson.getFitPerson() == null ? "Unknown" : workPerson.getFitPerson().getNickName());
            holder.currZoneTextView.setText("Zone " + String.valueOf(workPerson.getCurrentZone()));
            holder.currPerfTextView.setText("%" + String.valueOf(workPerson.getCurrentPerf()));
            holder.totalCalTextView.setText(String.valueOf((int) workPerson.getTotalCal()));
            holder.currHrTextView.setText(String.valueOf(workPerson.getCurrentHr()));


        } catch (RealmException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return getData().size();
    }

    public int getGridSize(){
        int gridSize;
        if (getItemCount()<=4) {
            gridSize = 2;
        } else if(getItemCount() <= 9){
            gridSize = 3;
        } else if(getItemCount() <= 16){
            gridSize = 4;
        } else {
            gridSize = 5;
        }
        return gridSize;
    }

    public int getZoneColor(int currentZone){
        switch (currentZone){
            case 1 : return R.color.userGray;
            case 2 : return R.color.userBlue;
            case 3 : return R.color.userGreen;
            case 4 : return R.color.userOrange;
            case 5 : return R.color.userRed;
            default: return R.color.userWhite;
        }
    }

}
*/
