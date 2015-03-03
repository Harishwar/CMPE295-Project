package cmpe.alpha.fitwhiz.controllers;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmpe.alpha.fitwhiz.R;

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
