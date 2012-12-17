/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.via.slidecaster;

import java.util.List;

import android.os.Bundle;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.net.MyWebClient;
import cz.via.slidecaster.task.Task;
import cz.via.slidecaster.util.TouchImageView;

/**
 * 
 * @author Lenovo
 */
public class PresentationActivity extends BaseActivity {

	private Room room;
	private boolean teacher;
	private float downX, downY, upX, upY;
	private TouchImageView image;
	private String pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.presentation);

		image = (TouchImageView) findViewById(R.id.displayedImage);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("pass")) {
				pass = bundle.getString("pass");
			}
			if (bundle.containsKey("room")) {
				room = (Room) bundle.get("room");

				getRoom();
			}
		}

	}

	private void getRoom() {
		new Task<List<Room>>(this) {
			@Override
			public List<Room> doTask() throws ApplicationException {

				// TODO
				MyWebClient.getInstance().getRoom(app.getDeviceId(), room, pass);
				return null;
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(List<Room> list) {
				// afterRoomsGetFinished(list);
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	// /**
	// * Called when the activity is first created.
	// */
	// @Override
	// public void onCreate(Bundle icicle) {
	// super.onCreate(icicle);
	// Intent intent = getIntent();
	// room = (Room) intent.getSerializableExtra("key");
	// teacher = intent.getBooleanExtra("teacher", false);
	// this.setTitle(room.getName());
	// ImageView iView = new ImageView(this);
	// iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	// iView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	// iView.setBackgroundColor(Color.WHITE);
	// if (teacher) {
	// iView.setOnTouchListener(new View.OnTouchListener() {
	// public boolean onTouch(View v, MotionEvent event) {
	//
	// final int MIN_DISTANCE = 100;
	//
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN: {
	// downX = event.getX();
	// downY = event.getY();
	// return true;
	// }
	// case MotionEvent.ACTION_UP: {
	// upX = event.getX();
	// upY = event.getY();
	//
	// float deltaX = downX - upX;
	// float deltaY = downY - upY;
	//
	// // swipe horizontal?
	// if (Math.abs(deltaX) > MIN_DISTANCE) {
	// // left or right
	// if (deltaX < 0) {
	//  prevImage();
	// return true;
	// }
	// if (deltaX > 0) {
	//  nextImage();
	// return true;
	// }
	// } else {
	// return false; // We don't consume the event
	// }
	//
	// // swipe vertical?
	// if (Math.abs(deltaY) > MIN_DISTANCE) {
	// // top or down
	// if (deltaY < 0) {
	//  prevImage();
	// return true;
	// }
	// if (deltaY > 0) {
	//  nextImage();
	// return true;
	// }
	// } else {
	// return false; // We don't consume the event
	// }
	//
	// return true;
	// }
	// }
	//
	// return false;
	// }
	// });
	// }
	// this.addContentView(iView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	// }

	//  private void nextImage() {
	// room.nextImage();
	// this.setTitle(room.getName() + " slide: " + room.getImageNumber());
	// Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
	// }
	//
	//  private void prevImage() {
	// room.prevImage();
	// this.setTitle(room.getName() + " slide: " + room.getImageNumber());
	// Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_SHORT).show();
	// }
}
