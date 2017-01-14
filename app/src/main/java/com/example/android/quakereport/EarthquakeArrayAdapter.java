package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 1/5/17.
 */

public class EarthquakeArrayAdapter extends ArrayAdapter {

//test comment for git push
    private final String REGEX_STRING_SPLIT_SEPARATOR = "of";

    private Context mContext;


    public EarthquakeArrayAdapter(Context context, List objects) {
        super(context, 0, objects);

        mContext = context;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        ViewHolder viewHolder;

        Earthquake earthquake = (Earthquake) getItem(position);

        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String magnitude = decimalFormat.format(earthquake.getMagnitude());

        String placeString = earthquake.getPlace();
        String[] stringSplit = placeString.split(REGEX_STRING_SPLIT_SEPARATOR);
        String relativeLocation;
        String cityStateName;

        if (stringSplit.length > 1) {
            relativeLocation = stringSplit[0].trim() + " of";
            cityStateName = stringSplit[1].trim();
        } else {
            relativeLocation = mContext.getString(R.string.near_the);
            cityStateName = stringSplit[0].trim();
        }

        Date dateObject = new Date(earthquake.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_earthquake, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mMagnitude = (TextView) listItemView.findViewById(R.id.textView_magnitude);
            viewHolder.mRelativeLocation = (TextView) listItemView.findViewById(R.id.textView_relativeLocation);
            viewHolder.mCity = (TextView) listItemView.findViewById(R.id.textView_city);
            viewHolder.mDate = (TextView) listItemView.findViewById(R.id.textView_date);
            viewHolder.mTime = (TextView) listItemView.findViewById(R.id.textView_time);
            listItemView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) listItemView.getTag();
        }

        GradientDrawable magCircle = (GradientDrawable) viewHolder.mMagnitude.getBackground();
        int magColor = getMagnitudeColor(earthquake.getMagnitude());
        magCircle.setColor(magColor);

        viewHolder.mMagnitude.setText(magnitude);
        viewHolder.mCity.setText(cityStateName);
        viewHolder.mRelativeLocation.setText(relativeLocation);
        viewHolder.mDate.setText(dateFormat.format(dateObject));
        viewHolder.mTime.setText(timeFormat.format(dateObject));

        return listItemView;
    }

    public int getMagnitudeColor(double magnitude) {

        int magnitudeColorResourceId;
        int magFloor = (int) Math.floor(magnitude);

        switch (magFloor) {

            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;

            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;

            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;

            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;

            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;

            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;

            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;

            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;

            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;

            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);

    }

}

class ViewHolder {

    TextView mMagnitude;
    TextView mRelativeLocation;
    TextView mCity;
    TextView mDate;
    TextView mTime;
}
