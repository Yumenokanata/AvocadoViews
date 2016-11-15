package indi.yume.view.sample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.view.avocadoviews.dataselect.DateSelectPicker;
import rx.functions.Action0;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.date_select_picker)
    DateSelectPicker dateSelectPicker;
    @Bind(R.id.jump_to_refresh_button)
    Button jumpToRefreshButton;
    @Bind(R.id.jump_to_recycler_button)
    Button jumpToRecyclerButton;
    @Bind(R.id.jump_to_loading_button)
    Button jumpToLoadingButton;
    @Bind(R.id.show_dialog_button)
    Button showDialogButton;
    @Bind(R.id.show_alert_dialog_button)
    Button showAlertDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        jumpToRefreshButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DoubleRefreshActivity.class)));
        jumpToRecyclerButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RecyclerRefreshActivity.class)));
        jumpToLoadingButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoadingActivity.class)));

        showDialogButton.setOnClickListener(v -> showProgressDialog());
        showAlertDialogButton.setOnClickListener(v -> showAlertDialog());
    }

    private void showProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void showAlertDialog() {
//        Dialog dialog = new AlertDialog.Builder(this).setTitle("Title").setMessage("Message").create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Title");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.show();
    }
}
