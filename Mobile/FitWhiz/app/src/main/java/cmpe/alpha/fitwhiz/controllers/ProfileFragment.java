package cmpe.alpha.fitwhiz.controllers;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cmpe.alpha.fitwhiz.HelperLibrary.ProfileUpdater;
import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;
import cmpe.alpha.fitwhiz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private String name="", gender="";
    private int age=0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ProfileFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);
        ProfileUpdater profileUpdater = new ProfileUpdater(this);
        profileUpdater.execute(new PropertiesReader(profileFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));

        return profileFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
