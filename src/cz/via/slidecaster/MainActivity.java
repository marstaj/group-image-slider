package cz.via.slidecaster;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import cz.via.slidecaster.adapter.RoomAdapter;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.net.MyWebClient;
import cz.via.slidecaster.task.Task;

public class MainActivity extends BaseActivity {

	private ListView listView;
	private boolean isTeacher;
	private Parcelable state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_list);

		// this.isTeacher();

		// Find listview in layout
		listView = (ListView) findViewById(R.id.listview);
		// Set on item click listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

				// Pull room object for selected row
				Object object = view.getTag(R.id.ID_ROOM);
				if (object != null && object.getClass().equals(Room.class)) {
					Room room = (Room) object;

					if (room.isPassword()) {// TODO onItemclick
						askForPassword(room);
					} else {
						enterRoom(room);
					}
				}
			}

		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				// Pull room object for selected row
				Object object = view.getTag(R.id.ID_ROOM);

				if (object != null && object.getClass().equals(Room.class)) {
					final Room room = (Room) object;

					if (room.isYours()) {
						Builder builder = new Builder(MainActivity.this);
						builder.setItems(new CharSequence[] { getText(R.string.edit), getText(R.string.remove) }, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0) { // EDIT
									editRoomInit(room);
									return;
								}
								if (item == 1) { // DELETE
									deleteRoom(room);
									return;
								}
							}

						});
						builder.show();
					} else {
						// TODO kdyz to neni moje
					}

				}

				// if (object != null && object.getClass().equals(Room.class)) {
				// Room room = (Room) object;
				// Intent myIntent = new Intent(MainActivity.this, EditRoomActivity.class);
				// myIntent.putExtra("new", false);
				// myIntent.putExtra("room", room);
				// MainActivity.this.startActivity(myIntent);
				// }
				return true;
			}
		});

		downloadRooms();
	}

	private void editRoomInit(final Room room) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.fill_forms);
		LayoutInflater infl = LayoutInflater.from(this);
		View mDialogLayout = infl.inflate(R.layout.pass_and_name_dialog, null);

		// Forms
		final EditText formName = (EditText) mDialogLayout.findViewById(R.id.EditText_Pwd1);
		final EditText formPass = (EditText) mDialogLayout.findViewById(R.id.EditText_Pwd2);

		builder.setView(mDialogLayout);

		builder.setPositiveButton(getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String name = formName.getText().toString();
				String pass = formPass.getText().toString();

				editRoom(room, name, pass);
				dialog.cancel();
			}
		});
		builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();

	}

	// Create and start new task for downloading rooms list. Task is in a different thread, so loading message can be displayed
	private void downloadRooms() {
		new Task<List<Room>>(this) {
			@Override
			public List<Room> doTask() throws ApplicationException {

				// Download roomsList
				List<Room> list = MyWebClient.getInstance().getRooms();
				return list;
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(List<Room> list) {
				// Do after task finished
				afterRoomsGetFinished(list);
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
				// Do after task failed
			}
		};
	}

	private void afterRoomsGetFinished(List<Room> list) {

		state = listView.onSaveInstanceState();
		// set adapter to a list
		listView.setAdapter(new RoomAdapter(this, R.layout.room_list_item, list));
		listView.onRestoreInstanceState(state);

	}

	private void addRoomInit() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.fill_forms);
		LayoutInflater infl = LayoutInflater.from(this);
		View mDialogLayout = infl.inflate(R.layout.pass_and_name_dialog, null);

		// Forms
		final EditText formName = (EditText) mDialogLayout.findViewById(R.id.EditText_Pwd1);
		final EditText formPass = (EditText) mDialogLayout.findViewById(R.id.EditText_Pwd2);

		builder.setView(mDialogLayout);

		builder.setPositiveButton(getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String name = formName.getText().toString();
				String pass = formPass.getText().toString();

				addRoom(name, pass);
				dialog.cancel();
			}
		});
		builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	private void addRoom(final String name, final String pass) {
		new Task<List<Room>>(this) {
			@Override
			public List<Room> doTask() throws ApplicationException {

				MyWebClient.getInstance().createRoom(name, pass);
				List<Room> list = MyWebClient.getInstance().getRooms();
				return list;
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(List<Room> list) {
				afterRoomsGetFinished(list);
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	private void deleteRoom(final Room room) {
		new Task<List<Room>>(this) {
			@Override
			public List<Room> doTask() throws ApplicationException {

				MyWebClient.getInstance().deleteRoom(room);
				List<Room> list = MyWebClient.getInstance().getRooms();
				return list;
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(List<Room> list) {
				afterRoomsGetFinished(list);
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	private void editRoom(final Room room, final String name, final String pass) {
		new Task<List<Room>>(this) {
			@Override
			public List<Room> doTask() throws ApplicationException {

				MyWebClient.getInstance().editRoom(room, name, pass);
				List<Room> list = MyWebClient.getInstance().getRooms();
				return list;
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(List<Room> list) {
				afterRoomsGetFinished(list);
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	private void enterRoom(Room room) {
		enterRoom(room, null);
	}

	private void enterRoom(Room room, String pass) {
		if (pass == null) {
			Intent myIntent = new Intent(MainActivity.this, PresentationActivity.class);
			myIntent.putExtra("room", room);
			MainActivity.this.startActivity(myIntent);
		} else {
			Intent myIntent = new Intent(MainActivity.this, PresentationActivity.class);
			myIntent.putExtra("room", room);
			myIntent.putExtra("pass", pass);
			MainActivity.this.startActivity(myIntent);
		}

	}

	private void askForPassword(final Room room) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.ask_for_password);
		LayoutInflater infl = LayoutInflater.from(this);
		View mDialogLayout = infl.inflate(R.layout.password_dialog, null);

		// Forms
		final EditText formPass = (EditText) mDialogLayout.findViewById(R.id.EditText_Pwd2);

		builder.setView(mDialogLayout);

		builder.setPositiveButton(getText(R.string.ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String pass = formPass.getText().toString();

				enterRoom(room, pass);
				dialog.cancel();
			}
		});
		builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Set menu
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Plus button pressed
		case R.id.menu_refresh: {
			downloadRooms();
			break;
		}
		case R.id.menu_add_room: {
			addRoomInit();
			break;
		}
		case R.id.menu_settings: {
			break;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	// private void verifyAccess(final Room room) {
	// AlertDialog.Builder alert = new AlertDialog.Builder(this);
	//
	// final String password = room.getPassword();
	// alert.setTitle("Heslo");
	// alert.setMessage("Zadejte prosím heslo pro vstup do místnosti.");
	//
	// // Set an EditText view to get user input
	// final EditText input = new EditText(this);
	// input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
	// alert.setView(input);
	//
	// alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// String value = input.getText().toString();
	// if (password.equals(value)) {
	// Toast.makeText(getApplicationContext(), "Password OK.", Toast.LENGTH_SHORT).show();
	// enterRoom(room);
	// } else {
	// Toast.makeText(getApplicationContext(), "Wrong password.", Toast.LENGTH_SHORT).show();
	// }
	// }
	// });
	//
	// alert.setNegativeButton(
	// "Cancel", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// // Canceled.
	// }
	// });
	//
	// alert.show();
	// }

	// private void isTeacher() {
	// final CharSequence[] items = { "student", "teacher" };
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setTitle("Select role");
	// builder.setItems(items, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int item) {
	// // Toast.makeText(getApplicationContext(), items[item] + " -> " + item, Toast.LENGTH_SHORT).show();
	// isTeacher = item == 1;
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// }

}
