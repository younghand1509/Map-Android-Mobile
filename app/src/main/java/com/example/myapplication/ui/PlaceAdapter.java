package com.example.myapplication.ui;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.VH> {

    public interface OnPlaceClick {
        void onPlaceClicked(Place place);
    }

    private final List<Place> data = new ArrayList<>();
    private String keyword = "";
    private final OnPlaceClick onPlaceClick;

    public PlaceAdapter(OnPlaceClick onPlaceClick) {
        this.onPlaceClick = onPlaceClick;
    }

    public void submit(List<Place> newData, String keyword) {
        this.keyword = keyword == null ? "" : keyword;
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Place p = data.get(pos);
        String name = p.getDisplayName() == null ? "" : p.getDisplayName();

        if (!keyword.isEmpty()) {
            name = name.replaceAll("(?i)(" + java.util.regex.Pattern.quote(keyword) + ")",
                    "<b><font color=\"#FF5722\">$1</font></b>");
            h.title.setText(Html.fromHtml(name, Html.FROM_HTML_MODE_LEGACY));
        } else {
            h.title.setText(name);
        }

        h.itemView.setOnClickListener(v -> onPlaceClick.onPlaceClicked(p));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }
    }
}
