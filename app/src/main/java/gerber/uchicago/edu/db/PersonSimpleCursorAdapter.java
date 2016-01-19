package gerber.uchicago.edu.db;

/**
 * Created by Jiaming on 2015/6/8.
 */
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import gerber.uchicago.edu.R;

public class PersonSimpleCursorAdapter extends SimpleCursorAdapter {
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    public PersonSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });
    }

    //to use a viewholder, you must override the following two methods and define a ViewHolder class
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {

            holder = new ViewHolder();

            holder.nName = cursor.getColumnIndexOrThrow(PersonDbAdapter.COL_NAME);
            holder.nEmail = cursor.getColumnIndexOrThrow(PersonDbAdapter.COL_EMAIL);
            holder.nCategory = cursor.getColumnIndexOrThrow(PersonDbAdapter.COL_CATEGORY);
            holder.nPhone = cursor.getColumnIndexOrThrow(RestosDbAdapter.COL_PHONE);
            holder.nNiv = cursor.getColumnIndexOrThrow(RestosDbAdapter.COL_IMG_URL);

            holder.viewCategory =  view.findViewById(R.id.list_tab);
            holder.textViewName = (TextView) view.findViewById(R.id.list_text);
            holder.textCategory = (TextView) view.findViewById(R.id.list_city);
            holder.networkImageView = (NetworkImageView) view.findViewById(R.id.list_niv);


            view.setTag(holder);
        }

        holder.textViewName.setText(cursor.getString(holder.nName));
        holder.textCategory.setText(cursor.getString(holder.nCategory));
        holder.networkImageView.setImageUrl(cursor.getString(holder.nNiv), mImageLoader);

        if (cursor.getInt(holder.nCategory) == 0)
            holder.viewCategory.setBackgroundColor(context.getResources().getColor(R.color.orange));
        else if (cursor.getInt(holder.nCategory) == 1)
            holder.viewCategory.setBackgroundColor(context.getResources().getColor(R.color.blue));
        else if (cursor.getInt(holder.nCategory) == 2)
            holder.viewCategory.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            holder.viewCategory.setBackgroundColor(context.getResources().getColor(R.color.green));



    }

    static class ViewHolder {

        int nName;
        int nEmail;
        int nCategory;
        int nPhone;
        int nNiv;



        TextView textViewName;
        TextView textCategory;
        View viewCategory;
        NetworkImageView networkImageView;

    }
}