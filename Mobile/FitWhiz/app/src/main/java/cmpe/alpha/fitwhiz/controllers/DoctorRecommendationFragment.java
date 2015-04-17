package cmpe.alpha.fitwhiz.controllers;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cmpe.alpha.fitwhiz.HelperLibrary.AllergiesHelper;
import cmpe.alpha.fitwhiz.HelperLibrary.PropertiesReader;
import cmpe.alpha.fitwhiz.HelperLibrary.RecommendationsHelper;
import cmpe.alpha.fitwhiz.R;
import cmpe.alpha.fitwhiz.lib.FitwhizApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorRecommendationFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private String medications="",exercises="";

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getExercises() {
        return exercises;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

    public DoctorRecommendationFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View doctorFragment = inflater.inflate(R.layout.fragment_doctor_recommendation, container, false);
        FitwhizApplication application=(FitwhizApplication)this.getActivity().getApplication();
        RecommendationsHelper rh = new RecommendationsHelper(application);
        rh.execute(new PropertiesReader(doctorFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));
        AllergiesHelper ah = new AllergiesHelper(application);
        ah.execute(new PropertiesReader(doctorFragment.getContext()).getProperties("Fitwhiz.properties").getProperty("FileUploadUrl"));
                ((TextView) doctorFragment.findViewById(R.id.vaccinations_val)).setText(application.getVaccinations());
        ((TextView) doctorFragment.findViewById(R.id.allergies_val)).setText(application.getAllergies());
        return doctorFragment;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((DashboardActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
