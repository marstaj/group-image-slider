package cz.via.slidecaster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import java.util.List;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cz.via.slidecaster.adapter.RoomAdapter;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.net.MyWebClient;
import cz.via.slidecaster.task.Task;

public class MainActivity extends BaseActivity {

    private ListView listView;
    private boolean isTeacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_list);

        this.isTeacher();

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

        final Button btnAddMore = new Button(this);
        btnAddMore.setText("new room");
        btnAddMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, EditRoomActivity.class);
                myIntent.putExtra("new", true);
                MainActivity.this.startActivity(myIntent);
            }
        });
        listView.addHeaderView(btnAddMore);

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
                    if (room.isPasswordProtected()) {// TODO onItemclick
                        verifyAccess(room);
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
                    Room room = (Room) object;
                    Intent myIntent = new Intent(MainActivity.this, EditRoomActivity.class);
                    myIntent.putExtra("new", false);
                    myIntent.putExtra("room", room);
                    MainActivity.this.startActivity(myIntent);
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Set menu
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void verifyAccess(final Room room) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final String password = room.getPassword();
        alert.setTitle("Heslo");
        alert.setMessage("Zadejte prosím heslo pro vstup do místnosti.");

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (password.equals(value)) {
                    Toast.makeText(getApplicationContext(), "Password OK.", Toast.LENGTH_SHORT).show();
                    enterRoom(room);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton(
                "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void enterRoom(Room room) {
        Intent myIntent = new Intent(MainActivity.this, PresentationActivity.class);
        myIntent.putExtra("key", room);
        myIntent.putExtra("teacher", isTeacher);
        MainActivity.this.startActivity(myIntent);
    }

    private void isTeacher() {

        final CharSequence[] items = {"student", "teacher"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select role");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //Toast.makeText(getApplicationContext(), items[item] + " -> " + item, Toast.LENGTH_SHORT).show();
                isTeacher = item == 1;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
