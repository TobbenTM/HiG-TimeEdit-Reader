package com.tobbentm.higreader;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;

/**
 * Created by Tobias on 02.09.13.
 */
public class SubsCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private Context act;
    private boolean updated = false;

    public SubsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        act = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.subs_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context ctx = context;
        final String name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
        final String id = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CLASS_ID));

        TextView tvName = (TextView) view.findViewById(R.id.sd_textName);
        ImageView img = (ImageView) view.findViewById(R.id.cancel_img);

        tvName.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
        img.setImageResource(R.drawable.navigation_cancel);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, ctx.getResources().getString(R.string.subs_unsubscribed) + name, Toast.LENGTH_LONG).show();
                deleteSub(id);
            }
        });
    }

    private void deleteSub(String id){
        DSSubscriptions ds = new DSSubscriptions(act);
        try {
            ds.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ds.deleteSubscription(id);
        this.changeCursor(ds.getCursor());
        ds.close();
        this.notifyDataSetChanged();
        updated = true;
    }

    public boolean update(){
        return updated;
    }
}
