package gerber.uchicago.edu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import gerber.uchicago.edu.db.PersonDbAdapter;
import gerber.uchicago.edu.db.PersonSimpleCursorAdapter;
import gerber.uchicago.edu.db.RestosDbAdapter;
import gerber.uchicago.edu.db.RestosGridCursorAdapter;
import gerber.uchicago.edu.sound.SoundVibeUtils;

/**
 * Jennifer Um's awesome code
 */
public class TabGrid extends Fragment {
    //this is the GridView

    //database and adapter
    private RestosDbAdapter mDbAdapter;
    private RestosGridCursorAdapter mCursorAdapter;
    private GridView mGridView;

    private PersonDbAdapter mPersonDbAdapter;
    private PersonSimpleCursorAdapter mCursorAdapterPerson;


    //Don't know if I need this
    private SharedPreferences mPreferences;
    private static final String SORT_ORDER = "sort_order";
    private static final String VERY_FIRST_LOAD = "our_very_first_load_";
    private String mSortOrder;

    //Things that seems like i don't need?
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private OnTab3InteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static TabGrid newInstance(String param1, String param2) {
        TabGrid fragment = new TabGrid();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TabGrid() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_place_grid,container,false);

        //super.onCreate(savedInstanceState);

        mGridView = (GridView) v.findViewById(R.id.restaurant_grid_item);
        mDbAdapter = new RestosDbAdapter(getActivity());
        mDbAdapter.open();

        mPersonDbAdapter = new PersonDbAdapter(getActivity());
        mPersonDbAdapter.open();

        //get the shared preferences
        mPreferences = getActivity().getSharedPreferences(
                "gerber.uchicago.edu", getActivity().MODE_PRIVATE);

        mSortOrder = mPreferences.getString(SORT_ORDER, null);
        Cursor cursor = mDbAdapter.fetchAllRestos(getSortOrder());
        Cursor personCursor = mPersonDbAdapter.fetchAllRestos(getSortOrder());

        //from columns defined in the db
        String[] from = new String[]{

                RestosDbAdapter.COL_NAME
                //RestosDbAdapter.COL_CITY
        };

        //to the ids of views in the layout
        int[] to = new int[]{
                R.id.grid_resto_name
                //R.id.grid_resto_name,
                //R.id.list_city
                //R.id.grid_resto_city
        };

        mCursorAdapter = new RestosGridCursorAdapter(
                //context
                getActivity(),
                //the layout of the row - now pulling from our new layout
                //When I change to grid, the app doesn't work
                R.layout.restos_grid,
                //cursor
                cursor,
                //from columns defined in the db
                from,
                //to the ids of views in the layout
                to,
                //flag - not used
                0);

        mCursorAdapterPerson = new PersonSimpleCursorAdapter(
                //context
                getActivity(),
                //the layout of the row - now pulling from our new layout
                R.layout.restos_row,
                //cursor
                personCursor,
                //from columns defined in the db
                from,
                //to the ids of views in the layout
                to,
                //flag - not used
                0);

        if( ! ((MainActivity)getActivity()).bButtonArray[0]) {
            mGridView.setAdapter(mCursorAdapter);
        }
        else {
            mGridView.setAdapter(mCursorAdapterPerson);
        }

        //when we click an individual item in the listview
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private Place mRestoClicked;
            private int mIdClicked;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {

                //this is just an example, I would put this elsewhere
                SoundVibeUtils.playSound(getActivity(), R.raw.power_up);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                ListView modeList = new ListView(getActivity());

                String[] stringArray = new String[]{"Edit ",  //0
                        "Share ", //1
                        "Map of ", //2
                        "Dial ",  //3
                        "Yelp site ",  //4
                        "Navigate to ",  //5
                        "Delete ", //6
                        "Cancel " //7
                };

                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);
                builder.setView(modeList);
                final Dialog dialog = builder.create();


                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                mIdClicked = getIdFromPosition(masterListPosition);
                mRestoClicked = mDbAdapter.fetchRestoById(mIdClicked);
                dialog.setTitle(mRestoClicked.getName());
                dialog.show();
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FavActionUtility favActionUtility = new FavActionUtility(getActivity());
                        try {
                            switch (position) {
                                case 0:
                                    //edit
                                    ((MainActivity)getActivity()).goToTab(mIdClicked, 2);
                                    break;
                                case 1:
                                    //share
                                    String strSubject = "Check out: " + mRestoClicked.getName();
                                    String strMessage = "\n\n"; //give the user some room to type a message
                                    strMessage += "Place: " + mRestoClicked.getName();
                                    strMessage += "\nAddress: " + mRestoClicked.getAddress() + ", " + mRestoClicked.getCity();
                                    strMessage += " \n\nPhone: " + PhoneNumberUtils.formatNumber(mRestoClicked.getPhone());
                                    strMessage += " \nYelp page: " + mRestoClicked.getYelp();
                                    if (mRestoClicked.getFavorite() == 1){
                                        strMessage += "\n[This is one of my favorite restaurants]";
                                    }
                                    strMessage += "\n\nPowered by Favorite Restaurants on Android by Adam Gerber";
                                    favActionUtility.share(strSubject, strMessage );
                                    break;

                                case 2:
                                    //map of
                                    favActionUtility.mapOf(mRestoClicked.getAddress(), mRestoClicked.getCity());
                                    break;
                                case 3:
                                    //dial
                                    favActionUtility.dial(mRestoClicked.getPhone());
                                    break;
                                case 4:
                                    //yelp site
                                    favActionUtility.yelpSite(mRestoClicked.getYelp());
                                    break;
                                case 5:
                                    //navigate to
                                    favActionUtility.navigateTo(mRestoClicked.getAddress(), mRestoClicked.getCity());
                                    break;
                                case 6:
                                    //delete single resto (we need to keep this for devices running 11 or less)
                                    mDbAdapter.deleteRestoById(mIdClicked);
                                    mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestos(getSortOrder()));
                                    break;
                                case 7:
                                    //cancel
                                    //do nothing and then dismiss
                                    break;
                            }
                        } catch (Exception e) {
                            //gracefully handle exceptions
                            favActionUtility.showErrorMessageInDialog(e.getMessage());
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                });


            }
        });

        return v;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTab3InteractionListener {

        // TODO: Update argument type and name
        public void onTab3Interaction(String id);
    }


    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String strSortOrder) {
        mSortOrder = strSortOrder;
        mPreferences.edit().putString(SORT_ORDER, strSortOrder).commit();

    }


    private int getIdFromPosition(int nPosition) {
        Cursor cursor = mCursorAdapter.getCursor();
        cursor.moveToPosition(nPosition);
        return cursor.getInt(RestosDbAdapter.INDEX_ID);
    }



    @Override
    public void onPause() {
        super.onPause();
        mDbAdapter.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDbAdapter.open();
        // mDbAdapter.insertSomeRestos();
        mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestos(getSortOrder()));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    public void filter(String Category) {
        mCursorAdapter.changeCursor(mDbAdapter.fetchRestoByCategory(Category));
    }

    // for search
    public void showSearchResults(String name) {
        mCursorAdapter.changeCursor(mDbAdapter.fetchRestoByName(name));
    }

    public void showPersonSearchResults(String name) {
        mCursorAdapterPerson.changeCursor(mPersonDbAdapter.fetchPresonByName(name));
    }

    public void undo(){
        mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestos(getSortOrder()));
        mCursorAdapterPerson.changeCursor(mPersonDbAdapter.fetchAllRestos(getSortOrder()));
    }

    // Person handle
    public void viewPerson() {
        mGridView.setAdapter(mCursorAdapterPerson);
    }

    public void viewPlace() {
        mGridView.setAdapter(mCursorAdapter);
    }
}