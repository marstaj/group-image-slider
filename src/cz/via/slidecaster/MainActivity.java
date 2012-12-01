package cz.via.slidecaster;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cz.via.slidecaster.adapter.RoomAdapter;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.net.MyWebClient;
import cz.via.slidecaster.task.Task;

public class MainActivity extends BaseActivity {

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_list);

		// Find listview in layout
		listView = (ListView) findViewById(R.id.listview);

		downloadRooms();
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

		// set adapter to a list
		listView.setAdapter(new RoomAdapter(this, R.layout.room_list_item, list));

		// Set on item click listener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

				// Pull room object for selected row
				Object object = view.getTag(R.id.ID_ROOM);
				if (object != null && object.getClass().equals(Room.class)) {
					Room room = (Room) object;

					// TODO onItemclick

				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Set menu
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
