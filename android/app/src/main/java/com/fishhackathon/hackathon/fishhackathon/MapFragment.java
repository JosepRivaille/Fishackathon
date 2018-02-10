package com.fishhackathon.hackathon.fishhackathon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MapFragment extends Fragment {
    private View rootView;
    private TextView title;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        setUpElements();
        title.setText(getString(R.string.title_map));

        return rootView;
    }

    private void setUpElements() {
        title = (TextView) rootView.findViewById(R.id.map_fragment_title);
    }
}