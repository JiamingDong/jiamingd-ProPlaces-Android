package gerber.uchicago.edu;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the mCharSequences of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    int mPlacesId;
    Context mContext;
    Boolean isPlace = false;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }
    public ViewPagerAdapter(Context context, FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.mContext = context;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    public int getPlacesId() {
        return mPlacesId;
    }

    public void setPlacesId(int nPlacesId) {
        this.mPlacesId = nPlacesId;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    TabList tabList;
    TabGrid tabGrid;
    TabEdit tabEdit;
    TabNew tabNew;
    TabNewPerson mTabNewPerson;

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        switch (position){

            //list view
            case 0:
                tabList = new TabList();

                return tabList;
            //grid view
            case 1:
                tabGrid = new TabGrid();

                return tabGrid;
            case 2:
                tabEdit = new TabEdit();

                return tabEdit;
            case 3:
                if (isPlace) {
                    tabNew = new TabNew();

                    return tabNew;
                }
                else {
                    mTabNewPerson = new TabNewPerson();

                    return mTabNewPerson;
                }

            default:
                TabEdit tabdefault = new TabEdit();

                return tabdefault;
        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    public void filter(String category) {
        if (tabList != null) {
            tabList.filter(category);Toast.makeText(tabList.getActivity(), "ViewPagerAdapter filter called", Toast.LENGTH_LONG).show();
        }

        if (tabGrid != null) {
            tabGrid.filter(category);Toast.makeText(tabList.getActivity(), "ViewPagerAdapter filter called", Toast.LENGTH_LONG).show();
        }

    }

    public void showSearchResults(String name) {
        if (tabList != null) {
            tabList.showSearchResults(name);
        }

        if (tabGrid != null) {
            tabGrid.showSearchResults(name);
        }
    }

    public void showPersonSearchResults(String name) {
        if (tabList != null) {
            tabList.showPersonSearchResults(name);
        }

        if (tabGrid != null) {
            tabGrid.showPersonSearchResults(name);
        }
    }

    public void undo(){
        if(tabList != null ){
            tabList.undo();
        }

        if(tabGrid != null ){
            tabGrid.undo();
        }
    }

    // person handle
    public void viewPerson() {
        if(tabList == null) {
            tabList = new TabList();
        }

        if (tabGrid == null) {
            tabGrid = new TabGrid();
        }

        tabList.viewPerson();
        tabGrid.viewPerson();
    }

    public void viewPlace(){
        if (tabList == null) {
            tabList = new TabList();
        }

        if (tabGrid == null) {
            tabGrid = new TabGrid();
        }
        tabList.viewPlace();
        tabGrid.viewPlace();
    }

}