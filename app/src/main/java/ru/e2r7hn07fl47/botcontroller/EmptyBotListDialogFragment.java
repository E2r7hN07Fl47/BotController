package ru.e2r7hn07fl47.botcontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class EmptyBotListDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.fragment_title));
        builder.setMessage(getString(R.string.fragment_message));
        builder.setPositiveButton(getString(R.string.fragment_button_restart), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setNegativeButton(getString(R.string.fragment_button_reset), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences.Editor e = MainActivity.sPref.edit();
                e.putBoolean("hasData", false);
                e.commit();
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) requireActivity()).restart();
    }
}
