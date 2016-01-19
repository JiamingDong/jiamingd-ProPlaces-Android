package gerber.uchicago.edu;

import gerber.uchicago.edu.R;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

public class GuideActivity extends Activity {
    private ViewPager viewPager;

    /**store the paged views*/
    private ArrayList<View> pageViews;
    private ImageView imageView;

    /**display the small dots in an array*/
    private ImageView[] imageViews;

    //contain pictures LinearLayout
    private ViewGroup viewPics;

    //contain dots LinearLayout
    private ViewGroup viewPoints;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //put View into the array
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.viewpager_page1, null));
        pageViews.add(inflater.inflate(R.layout.viewpager_page2, null));

        // create imageviews array, the size of which is the number of pictures.
        imageViews = new ImageView[pageViews.size()];
        //load the view from the assigned XML file
        viewPics = (ViewGroup) inflater.inflate(R.layout.activity_guide, null);

        //instantiate dots' linearLayout and viewpager
        viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);

        //add pictures of the dots
        for(int i=0;i<pageViews.size();i++){
            imageView = new ImageView(GuideActivity.this);
            //set the params of dots' imageview
            imageView.setLayoutParams(new LayoutParams(20,20));//create a layout with 20 height and width
            imageView.setPadding(20, 0, 20, 0);
            //add the dot layout into the array
            imageViews[i] = imageView;

            //select the first dot as default, for now the first dot is selected while others not
            if(i==0){
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                imageViews[i].setBackgroundResource(R.drawable.page_indicator);
            }

            viewPoints.addView(imageViews[i]);
        }

        //show the views of pictures
        setContentView(viewPics);

        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

    private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //set guided
            setGuided();

            //jump
            Intent mIntent = new Intent();
            mIntent.setClass(GuideActivity.this, MainActivity.class);
            GuideActivity.this.startActivity(mIntent);
            GuideActivity.this.finish();
        }
    };

    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }


    class GuidePageAdapter extends PagerAdapter{

        //destroy the view at position
        @Override
        public void destroyItem(View v, int position, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager)v).removeView(pageViews.get(position));

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        //get the number of current views
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pageViews.size();
        }

        //initialize the view of position
        @Override
        public Object instantiateItem(View v, int position) {
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(pageViews.get(position));

            // test the button event in view page 1
            if (position == 1) {
                Button btn = (Button) v.findViewById(R.id.btn_close_guide);
                btn.setOnClickListener(Button_OnClickListener);
            }

            return pageViews.get(position);
        }

        // judge if the view is created by the object
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }



        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }


    class GuidePageChangeListener implements OnPageChangeListener{

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            for(int i=0;i<imageViews.length;i++){
                imageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
                // not current selected page, set the state to unselected
                if(position !=i){
                    imageViews[i].setBackgroundResource(R.drawable.page_indicator);
                }
            }

        }
    }
}
