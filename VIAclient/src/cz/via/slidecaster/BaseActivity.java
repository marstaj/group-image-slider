package cz.via.slidecaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;

public class BaseActivity extends Activity {

	protected MyApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		app = (MyApp) this.getApplication();
	}

	public void showMsgDialog(String s, boolean alignCenter, final boolean finishOnClick) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater mInflater = LayoutInflater.from(this);

		TextView message = (TextView) mInflater.inflate(R.layout.show_message_dialog, null);
		message.setText(s);
		if (alignCenter) {
			message.setGravity(Gravity.CENTER_HORIZONTAL);
		}

		builder/* .setMessage(s) */.setView(message).setCancelable(true).setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				if (finishOnClick) {
					finish();
					return;
				}
			}
		});
		AlertDialog alert = builder.create();
		// System.out.println(s);
		if (!this.isFinishing())
			alert.show();
	}

	public void showMsgDialog(String s) {
		showMsgDialog(s, false, false);
	}

	public void showMsgDialog(String s, boolean alignCenter) {
		showMsgDialog(s, alignCenter, false);
	}

	public void showMsgDialog(int id, boolean alignCenter) {
		showMsgDialog(getResources().getString(id), alignCenter, false);
	}

	public void showMsgDialog(int id) {
		showMsgDialog(id, false);
	}

	// pokus

}