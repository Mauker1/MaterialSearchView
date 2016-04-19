package br.com.mauker.materialsearchview.lib.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by mauker on 19/04/2016.
 */
public class CursorSearchAdapter extends CursorAdapter {

    public CursorSearchAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (view != null) {

        }
    }

    private class MyViewHolder {
        TextView textView;

        public MyViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(android.R.id.text1);
        }
    }
}
