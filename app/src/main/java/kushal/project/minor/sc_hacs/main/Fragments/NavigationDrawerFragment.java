package kushal.project.minor.sc_hacs.main.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kushal.project.minor.sc_hacs.R;
import kushal.project.minor.sc_hacs.main.Adapters.DrawerListAdapter;
import kushal.project.minor.sc_hacs.main.classes.DrawerDataItem;
import kushal.project.minor.sc_hacs.main.classes.Setup_Bluetooth;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements DrawerListAdapter.ClickListener {

    //variables

    private RecyclerView mDrawerRecyclerView;
    public static final String User_Learned_drawer_key = "Learned";
    public static final String sPreference_file_name = "SharedPref";
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;

    private Boolean mUserLearnedDrawer;
    private Boolean mFromSavedInstantState;
    private View containerView;

    private DrawerListAdapter drawerListAdapter;


    //variables

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       mUserLearnedDrawer= Boolean.valueOf(readFromPreferences(getActivity(),User_Learned_drawer_key,"false"));

        if(savedInstanceState== null){
            mFromSavedInstantState = true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mDrawerRecyclerView = (RecyclerView) view.findViewById(R.id.drawer_recycler);

        drawerListAdapter = new DrawerListAdapter(getActivity(),getData());
        drawerListAdapter.setClickListener(this);
        mDrawerRecyclerView.setAdapter(drawerListAdapter);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return  view;   }







    public void setUp(int fragmentID, DrawerLayout drawerLayout, final Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentID);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(),User_Learned_drawer_key,mUserLearnedDrawer+"");
                }


                getActivity().invalidateOptionsMenu();


            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);

                getActivity().invalidateOptionsMenu();


            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset < 0.6 && slideOffset > 0){

                    toolbar.setAlpha((float) (0.9 - slideOffset));
                }




            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };


        if(!mUserLearnedDrawer && !mFromSavedInstantState){
            mDrawerLayout.openDrawer(containerView);
        }


        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {


                mDrawerToggle.syncState();

            }
        });


    }

    public static List<DrawerDataItem> getData(){

        List<DrawerDataItem> data = new ArrayList<DrawerDataItem>();
        int [] icons = {R.drawable.ic_action_mic,R.drawable.ic_action_bluetoothd,R.drawable.ic_action_help,R.drawable.ic_menu_manage,R.drawable.ic_action_share};
        String[] titles = {"Voice Command","Setup Bluetooth","Help","Manage","Share"};

        for(int i = 0; i < icons.length && i<titles.length ; i++){

            DrawerDataItem current = new DrawerDataItem();

            current.setIconID(icons[i]);
            current.setItem(titles[i]);

            data.add(current);


        }

        return data;
    }



    public static void saveToPreferences(Context context, String pName, String pValue){

        SharedPreferences sharedPrefrences  = context.getSharedPreferences(sPreference_file_name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefrences.edit();
        editor.putString(pName,pValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context,String pName,String defValue){

        SharedPreferences sharedPrefrences  = context.getSharedPreferences(sPreference_file_name,Context.MODE_PRIVATE);
        return sharedPrefrences.getString(pName,defValue);

    }


    @Override
    public void itemClicked(View view, int position) {


        switch (position){
            case  0:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case 1:
                startActivity(new Intent(getActivity(), Setup_Bluetooth.class));
                break;

            default:
                Toast.makeText(getActivity(),"This is "+position,Toast.LENGTH_SHORT).show();
                Log.e("TAG","DEFAULT");

        }


    }
}
