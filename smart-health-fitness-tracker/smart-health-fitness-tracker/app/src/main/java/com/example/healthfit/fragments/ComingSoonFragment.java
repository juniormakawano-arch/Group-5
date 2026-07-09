package com.example.healthfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthfit.R;

public class ComingSoonFragment extends Fragment {

    private String title;
    private int iconRes;

    public static ComingSoonFragment newInstance(String title, int iconRes) {
        ComingSoonFragment fragment = new ComingSoonFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("iconRes", iconRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            iconRes = getArguments().getInt("iconRes");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coming_soon, container, false);

        TextView tvTitle = view.findViewById(R.id.tvSoonTitle);
        ImageView ivIcon = view.findViewById(R.id.ivSoonIcon);

        tvTitle.setText(title);
        if (iconRes != 0) {
            ivIcon.setImageResource(iconRes);
        }

        return view;
    }
}
