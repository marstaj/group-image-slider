/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.via.slidecaster;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Photo;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.net.MyWebClient;
import cz.via.slidecaster.task.Task;
import cz.via.slidecaster.util.TouchImageView;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class PresentationActivity extends BaseActivity {

    private static final int ACTIVITY_SELECT_IMAGE = 0;
    private Room room;
    private float downX, downY, upX, upY;
    private TouchImageView image;
    private String pass;
    Handler handler;
    private int numOfActivePhoto = 0;
    private Bitmap actualPhoto;
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
                    setTouch(image);
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

                return MyWebClient.getInstance().getRoom(room, pass);
            }

            @Override
            public String getProgressDialogMessage() {
                if (room.isYours()) {
                    return getString(R.string.loading);
                } else {
                    return null;
                }
            }

            @Override
            public void doAfterTask(Room t) {
                if (t == null) {
                    Toast.makeText(PresentationActivity.this, "Třídu nelze načíst. Špatné heslo ?", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (room.isYours()) {
                        getAllPhotos();
                    } else {
                        getActualPhoto();
                    }
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

                Photo p = MyWebClient.getInstance().getActivePhotoInRoom(room, pass);
                if (p != null) {

                    try {
                        File f = new File(getCacheDir(), String.valueOf(p.getId()));
                        if (!f.exists()) {
                            URL url = new URL(p.getFilename());
                            InputStream is = (InputStream) url.getContent();
                            actualPhoto = BitmapFactory.decodeStream(is);
                            is.close();

                            FileOutputStream fos = new FileOutputStream(f.getAbsoluteFile());
                            actualPhoto.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                        }
                        actualPhoto = BitmapFactory.decodeFile(f.getAbsolutePath());
                    } catch (MalformedURLException e) {
                        return null;
                    } catch (IOException e) {
                        return null;
                    }

                }

                return p;

            }

            @Override
            public String getProgressDialogMessage() {
                // Set message to be displayed while task is running
                if (room.isYours()) {
                    return getString(R.string.loading);
                } else {
                    return null;
                }
            }

            @Override
            public void doAfterTask(Photo t) {
                if (t == null) {
                    Toast.makeText(PresentationActivity.this, "Photo is null", Toast.LENGTH_SHORT).show();
                    noPhoto();
                } else {
                    image.setImageBitmap(actualPhoto);
                }
            }

            @Override
            public void doAfterTaskFailed() {
                super.doAfterTaskFailed();
            }
        };
    }

    private void noPhoto() {
        image.setImageResource(R.drawable.no_image_available);
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

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);

                break;
            }
            case R.id.menu_set_active: {

                getAllPhotos();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    File file = new File(filePath);
                    addPhoto(file);
                }
        }
    }

    private void addPhoto(final File file) {
        new Task<Object>(this) {
            @Override
            public Object doTask() throws ApplicationException {
                FileOutputStream fos = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    float heightRatio = (float) options.outHeight / 600;
                    float widthRatio = (float) options.outWidth / 800;

                    float ratio;
                    if (heightRatio > 1 && widthRatio > 1) {
                        if (heightRatio > widthRatio) {
                            ratio = widthRatio / heightRatio;
                        } else {
                            ratio = heightRatio / widthRatio;
                        }
                    } else if (heightRatio > 1) {
                        ratio = 1 / heightRatio;
                    } else if (widthRatio > 1) {
                        ratio = 1 / widthRatio;
                    } else {
                        ratio = 1;
                    }

                    int newHeight = Math.round(ratio * options.outHeight);
                    int newWidth = Math.round(ratio * options.outWidth);

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                    yourSelectedImage = Bitmap.createScaledBitmap(yourSelectedImage, newWidth, newHeight, true);

                    File tempFile = File.createTempFile(file.getName(), null, getFilesDir());
                    fos = new FileOutputStream(tempFile);
                    yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    MyWebClient.getInstance().postPhoto(room, tempFile);
                    tempFile.delete();
                    return null;
                } catch (IOException ex) {
                    Logger.getLogger(PresentationActivity.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PresentationActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }

            @Override
            public String getProgressDialogMessage() {
                return getString(R.string.loading);
            }

            @Override
            public void doAfterTask(Object t) {
                numOfActivePhoto = list.size();
                getAllPhotos();
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
                list = MyWebClient.getInstance().getPhotosInRoom(room, pass);
                return list;
            }

            @Override
            public String getProgressDialogMessage() {
                return getString(R.string.loading);
            }

            @Override
            public void doAfterTask(List<Photo> t) {
                if (t.isEmpty()) {
                    noPhoto();
                } else {
                    if (room.isYours()) {
                        setActivePhoto(numOfActivePhoto);
                    }
                }
            }

            @Override
            public void doAfterTaskFailed() {
                super.doAfterTaskFailed();
            }
        };
    }

    private void setActivePhoto(int newActivePhotoIndex) {
        if (list.isEmpty()) {
            return;
        }
        if (newActivePhotoIndex >= list.size()) {
            newActivePhotoIndex = 0;
        }
        if (newActivePhotoIndex < 0) {
            newActivePhotoIndex = list.size() - 1;
        }
        numOfActivePhoto = newActivePhotoIndex;
        new Task<Object>(this) {
            @Override
            public Object doTask() throws ApplicationException {

                MyWebClient.getInstance().setPhotoAsActive(room, list.get(numOfActivePhoto));
                return null;
            }

//            @Override
//            public String getProgressDialogMessage() {
//                return getString(R.string.loading);
//            }
            @Override
            public void doAfterTask(Object t) {
                getActualPhoto();
            }

            @Override
            public void doAfterTaskFailed() {
                super.doAfterTaskFailed();
            }
        };


    }

    @Override
    public void finish() {
        if (handler != null) {
            handler.removeCallbacks(getActivePhoto);
        }
        super.finish();
    }

    public void setTouch(View iView) {
        iView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                final int MIN_DISTANCE = 100;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        downY = event.getY();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        upX = event.getX();
                        upY = event.getY();

                        float deltaX = downX - upX;
                        float deltaY = downY - upY;

                        // swipe horizontal?
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // left or right
                            if (deltaX < 0) {
                                prevImage();
                                return true;
                            }
                            if (deltaX > 0) {
                                nextImage();
                                return true;
                            }
                        } else {
                            return false; // We don't consume the event
                        }

                        // swipe vertical?
                        if (Math.abs(deltaY) > MIN_DISTANCE) {
                            // top or down
                            if (deltaY < 0) {
                                prevImage();
                                return true;
                            }
                            if (deltaY > 0) {
                                nextImage();
                                return true;
                            }
                        } else {
                            return false; // We don't consume the event
                        }

                        return true;
                    }
                }

                return false;
            }
        });
//        super.onCreate(icicle);
//        Intent intent = getIntent();
//        room = (Room) intent.getSerializableExtra("key");
//        teacher = intent.getBooleanExtra("teacher", false);
//        this.setTitle(room.getName());
//        ImageView iView = new ImageView(this);
//        iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        iView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        iView.setBackgroundColor(Color.WHITE);
//        if (teacher) {
//            
//        }
//        this.addContentView(iView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void nextImage() {
        this.setActivePhoto(numOfActivePhoto + 1);
    }

    private void prevImage() {
        this.setActivePhoto(numOfActivePhoto - 1);
    }
}
