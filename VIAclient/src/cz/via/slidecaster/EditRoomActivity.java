/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.via.slidecaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.net.MyWebClient;

/**
 * 
 * @author Lenovo
 */
public class EditRoomActivity extends Activity {

	private Room room;
	private boolean newRoom;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.edit_room);
		Intent intent = getIntent();
		this.newRoom = intent.getBooleanExtra("new", true);
		if (!this.newRoom) {
			this.room = (Room) intent.getSerializableExtra("room");
		} else {
			this.room = new Room();
			this.room.setName("name");
			// TODO this.room.setPassword("pass");
		}

		Button save = (Button) findViewById(R.id.button_save);
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO saveChanges();
				finish();
			}
		});

		Button cancel = (Button) findViewById(R.id.button_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		EditText name = (EditText) findViewById(R.id.editText_name);
		name.setText(room.getName());

		EditText password = (EditText) findViewById(R.id.editText_password);
		// TODOpassword.setText(room.getPassword());
	}

//	TODO private void saveChanges() {
//		if (this.newRoom) {
//			MyWebClient.getInstance().addRoom(this.room);
//		} else {
//			MyWebClient.getInstance().updateRoom(this.room);
//		}
//	}
}
