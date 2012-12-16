/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.via.slidecaster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import cz.via.slidecaster.model.Room;

/**
 *
 * @author Lenovo
 */
public class PresentationActivity extends Activity {

    Room room;
    boolean teacher;
    float downX, downY, upX, upY;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("key");
        teacher = intent.getBooleanExtra("teacher", false);
        this.setTitle(room.getName());
        ImageView iView = new ImageView(this);
        iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iView.setLayoutParams(new ImageSwitcher.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        iView.setBackgroundColor(Color.WHITE);
        if (teacher) {
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
        }
        this.addContentView(iView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void nextImage() {
        room.nextImage();
        this.setTitle(room.getName() + " slide: " + room.getImageNumber());
        Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
    }

    private void prevImage() {
        room.prevImage();
        this.setTitle(room.getName() + " slide: " + room.getImageNumber());
        Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_SHORT).show();
    }
}
