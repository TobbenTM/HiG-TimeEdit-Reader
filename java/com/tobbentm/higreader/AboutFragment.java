package com.tobbentm.higreader;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Tobias on 03.09.13.
 */
public class AboutFragment extends DialogFragment {

    public AboutFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_about, null);
        TextView textV = (TextView) view.findViewById(R.id.about_text);
        String text = textV.getText().toString();
        try {
            text += "\n\n" + getActivity().getResources().getString(R.string.about_version_append) +
                    "\t" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textV.setText(text);
        builder.setCustomView(view)
                .setTitle(getResources().getString(R.string.about_title))
                .setIcon(R.drawable.action_about)
                .setNeutralButton(getResources().getString(R.string.about_close_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
