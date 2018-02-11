package com.fishhackathon.hackathon.fishhackathon.views;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.fishhackathon.hackathon.fishhackathon.R;
import com.fishhackathon.hackathon.fishhackathon.ZoneDetailActivity;
import com.fishhackathon.hackathon.fishhackathon.models.Law;
import com.fishhackathon.hackathon.fishhackathon.models.Zone;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

public class CustomInfoWindow extends BasicInfoWindow {
    private Zone zoneInfo;

    public CustomInfoWindow(final Context context, MapView mapView, final Zone zoneInfo) {
        super(R.layout.custom_bubble, mapView);
        LinearLayout layout = (LinearLayout) (mView.findViewById(R.id.custom_bubble_layout));
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(context, ZoneDetailActivity.class)
                        .putExtra(ZoneDetailActivity.KEY_FOR_ZONE, zoneInfo)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(myIntent);
            }
        });
    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
    }

    @Override
    public void onClose() {

    }
}
