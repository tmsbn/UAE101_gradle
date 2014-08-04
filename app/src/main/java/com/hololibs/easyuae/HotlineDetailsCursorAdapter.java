package com.hololibs.easyuae;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.Normalizer;

import se.emilsjolander.sprinkles.CursorList;


public class HotlineDetailsCursorAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private CursorList<HotlineDetails> mHotlineDetails;
    private String searchText="";
    private HotlineInterface delegate;


    public HotlineDetailsCursorAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    public void setDelegate(HotlineInterface delegate){
        this.delegate=delegate;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void swapCursor(CursorList<HotlineDetails> hotlineDetails) {
        if (mHotlineDetails != null) {
            mHotlineDetails.close();
        }
        mHotlineDetails = hotlineDetails;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mHotlineDetails == null ? 0 : mHotlineDetails.size();
    }


    @Override
    public HotlineDetails getItem(int position) {
        return mHotlineDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View row = convertView;

        if (row == null) {

            holder = new ViewHolder();
            row = mInflater.inflate(R.layout.lv_rw_hotlinedetails, parent, false);
            holder.hotlineNumberTv = (TextView) row.findViewById(R.id.hotlineNumber);
            holder.hotlineNumberEmirateTv = (TextView) row.findViewById(R.id.hotlineNumberEmirate);
            holder.addToContactIbtn = (ImageButton) row.findViewById(R.id.addToContacts);
            holder.callHotlineIbtn = (ImageButton) row.findViewById(R.id.callHotline);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final HotlineDetails hotlineDetails = mHotlineDetails.get(position);

        holder.hotlineNumberTv.setText(highlight(searchText, hotlineDetails.hotlineNumber));
        holder.hotlineNumberEmirateTv.setText(highlight(searchText, hotlineDetails.emirateName));
        holder.callHotlineIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.addToContacts(hotlineDetails.hotlineName,hotlineDetails.hotlineNumber);
            }
        });
        holder.addToContactIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delegate.callHotline(hotlineDetails.hotlineNumber);
            }
        });



        return row;
    }


    private static class ViewHolder {

        TextView hotlineNumberTv;
        TextView hotlineNumberEmirateTv;
        ImageButton addToContactIbtn;
        ImageButton callHotlineIbtn;


    }



    public CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        int start = normalizedText.indexOf(search);
        if (search.equals("") || start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());

                highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }

    public static interface HotlineInterface{

        public void addToContacts(String name,String number);

        public void callHotline(String number);
    }


}