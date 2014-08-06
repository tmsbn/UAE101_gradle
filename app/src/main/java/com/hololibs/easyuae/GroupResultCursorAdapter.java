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
import android.widget.TextView;

import java.text.Normalizer;

import se.emilsjolander.sprinkles.CursorList;


public class GroupResultCursorAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private CursorList<GroupResult> mGroups;
    private String searchText = "";

    public GroupResultCursorAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void swapCursor(CursorList<GroupResult> groups) {
        if (mGroups != null) {
            mGroups.close();
        }
        mGroups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mGroups == null ? 0 : mGroups.size();
    }


    @Override
    public GroupResult getItem(int position) {
        return mGroups.get(position);
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
            row = mInflater.inflate(R.layout.lv_rw_groups, parent, false);
            holder.groupNameTv = (TextView) row.findViewById(R.id.groupName);
            holder.groupEmirateTv = (TextView) row.findViewById(R.id.groupEmirates);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        GroupResult groupResult = mGroups.get(position);


        holder.groupNameTv.setText(highlight(searchText, groupResult.groupName));
        holder.groupEmirateTv.setText("For " + groupResult.emirates);


        return row;
    }


    private static class ViewHolder {

        TextView groupNameTv;
        TextView groupEmirateTv;


    }


    public CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        search = Normalizer.normalize(search, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();


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

}