package com.fishhackathon.hackathon.fishhackathon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.fishhackathon.hackathon.fishhackathon.models.Profile;

public class ProfileFragment extends Fragment {
    public static final String TAG = ProfileFragment.class.getSimpleName();
    private View rootView;
    private ArrayAdapter<String> sizeOfShipAdapter;
    private ArrayAdapter<String> typeOfShipAdapter;
    private Spinner sizeOfShipSpinner;
    private Spinner typeOfShipSpinner;
    private Switch professionalSwitch;

    private Profile profile;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        setUpElements();
        setUpListeners();

        //Get default profile from DB
        profile = new Profile(getContext());

        // Set adapters for Spinners
        sizeOfShipAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Profile.SIZE_OF_SHIP);
        sizeOfShipSpinner.setAdapter(sizeOfShipAdapter);

        typeOfShipAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Profile.TYPE_OF_SHIP);
        typeOfShipSpinner.setAdapter(typeOfShipAdapter);

        // Adding preselected item
        sizeOfShipSpinner.setSelection(profile.returnPositionFromRaw(Profile.SIZE_OF_SHIP, profile.getSizeOfShip()));
        typeOfShipSpinner.setSelection(profile.returnPositionFromRaw(Profile.TYPE_OF_SHIP, profile.getTypeOfShip()));
        professionalSwitch.setChecked(profile.isProfessionalShip());

        return rootView;
    }

    private void setUpElements() {
        sizeOfShipSpinner = rootView.findViewById(R.id.profile_tamano_embarcacion_spinner);
        typeOfShipSpinner = rootView.findViewById(R.id.profile_tipo_embarcacion_spinner);
        professionalSwitch = rootView.findViewById(R.id.profile_uso_embarcacion_switch);
    }

    private void setUpListeners() {
        sizeOfShipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                profile.setSizeOfShip(Profile.SIZE_OF_SHIP[position]);
                profile.saveSizeToDB(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        typeOfShipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                profile.setTypeOfShip(Profile.TYPE_OF_SHIP[position]);
                profile.saveTypeToDB(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        professionalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setProfessionalShip(isChecked);
                profile.saveProfessionalToDB(getContext());
            }
        });
    }
}