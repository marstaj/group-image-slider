/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.via.slidecaster;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Photo;
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
	Handler handler;
	private int numOfPhoto = 0;
	private int numOfActivePhoto = 0;

	Runnable getActivePhoto = new Runnable() {
		@Override
		public void run() {
			getRoom();
			handler.postDelayed(getActivePhoto, 10000);
		}
	};
	protected List<Photo> list;

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

				if (room.isYours()) {

				} else {
					handler = new Handler();
					handler.postDelayed(getActivePhoto, 10000);
				}

			}
		}

	}

	private void getRoom() {
		new Task<Room>(this) {
			@Override
			public Room doTask() throws ApplicationException {

				return MyWebClient.getInstance().getRoom(app.getDeviceId(), room, pass);
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(Room t) {
				if (t == null) {
					Toast.makeText(PresentationActivity.this, "Room is null", Toast.LENGTH_SHORT).show();
				} else {
					getActualPhoto();
				}
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	private void getActualPhoto() {
		new Task<Photo>(this) {
			@Override
			public Photo doTask() throws ApplicationException {

				return MyWebClient.getInstance().getActivePhotoInRoom(app.getDeviceId(), room, pass);
			}

			@Override
			public String getProgressDialogMessage() {
				// Set message to be displayed while task is running
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(Photo t) {
				if (t == null) {
					Toast.makeText(PresentationActivity.this, "Photo is null", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(PresentationActivity.this, "" + t.getFilename() + " - " + t.getId(), Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Set menu
		if (room.isYours()) {
			getMenuInflater().inflate(R.menu.activity_presentation, menu);
			return true;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_photo: {
			addPhoto();
			break;
		}
		case R.id.menu_set_active: {

			getAllPhotos();
			break;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	private void addPhoto() {
		new Task<Object>(this) {

			@Override
			public Object doTask() throws ApplicationException {

				Photo photo = new Photo();
				photo.setFilename("Fotka " + numOfPhoto);
				MyWebClient.getInstance().postPhoto(app.getDeviceId(), room, photo);
				numOfPhoto++;
				return null;
			}

			@Override
			public String getProgressDialogMessage() {
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(Object t) {
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	private void getAllPhotos() {
		new Task<List<Photo>>(this) {

			@Override
			public List<Photo> doTask() throws ApplicationException {
				list = MyWebClient.getInstance().getPhotosInRoom(app.getDeviceId(), room, pass);
				return list;
			}

			@Override
			public String getProgressDialogMessage() {
				return getString(R.string.loading);
			}

			@Override
			public void doAfterTask(List<Photo> t) {

				setActivePhoto();
			}

			@Override
			public void doAfterTaskFailed() {
				super.doAfterTaskFailed();
			}
		};
	}

	private void setActivePhoto() {
		if (list != null) {
			if (numOfActivePhoto >= list.size()) {
				numOfActivePhoto = 0;
			}

			new Task<Object>(this) {

				@Override
				public Object doTask() throws ApplicationException {

					MyWebClient.getInstance().setPhotoAsActive(app.getDeviceId(), room, list.get(numOfActivePhoto));
					numOfActivePhoto++;
					return null;
				}

				@Override
				public String getProgressDialogMessage() {
					return getString(R.string.loading);
				}

				@Override
				public void doAfterTask(Object t) {
				}

				@Override
				public void doAfterTaskFailed() {
					super.doAfterTaskFailed();
				}
			};

		}
	}

	@Override
	public void finish() {
		if (handler != null) {
			handler.removeCallbacks(getActivePhoto);
		}
		super.finish();
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
	// prevImage();
	// return true;
	// }
	// if (deltaX > 0) {
	// nextImage();
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
	// prevImage();
	// return true;
	// }
	// if (deltaY > 0) {
	// nextImage();
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

	// private void nextImage() {
	// room.nextImage();
	// this.setTitle(room.getName() + " slide: " + room.getImageNumber());
	// Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
	// }
	//
	// private void prevImage() {
	// room.prevImage();
	// this.setTitle(room.getName() + " slide: " + room.getImageNumber());
	// Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_SHORT).show();
	// }

}
