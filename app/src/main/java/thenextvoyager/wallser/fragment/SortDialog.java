package thenextvoyager.wallser.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.callback.sortcallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortDialog extends DialogFragment {

    public SortDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        RadioButton latest = (RadioButton) view.findViewById(R.id.latest);
        RadioButton popular = (RadioButton) view.findViewById(R.id.popular);
        switch (PageFragment.order_By) {
            case "latest":
                latest.setChecked(true);
                break;
            case "popular":
                popular.setChecked(true);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton radioButton = (RadioButton) view.findViewById(i);
                radioButton.setChecked(true);
                switch (i) {
                    case R.id.latest:
                        sendBackResult("latest");
                        break;
                    case R.id.popular:
                        sendBackResult("popular");
                        break;
                }
            }
        });

    }

    public void sendBackResult(String order_by) {
        sortcallback sortcallback = (sortcallback) getTargetFragment();
        sortcallback.onDialogFinish(order_by);
        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort_dialog, container, false);
    }

}
