package com.example.phat.vnexpressnews.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phat.vnexpressnews.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CommentActivityFragment extends Fragment {

    public CommentActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }
}
