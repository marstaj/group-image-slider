package cz.via.slidecaster.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cz.via.slidecaster.R;
import cz.via.slidecaster.model.Room;

public class RoomAdapter extends ArrayAdapter<Room> {

	private List<Room> items;

	private LayoutInflater mInflater;

	private int textViewResourceId;

	public RoomAdapter(Context context, int textViewResourceId, List<Room> items) {
		super(context, textViewResourceId, items);
		mInflater = LayoutInflater.from(context);
		this.textViewResourceId = textViewResourceId;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(textViewResourceId, null);
			ViewHolder holder = new ViewHolder();
			holder.roomName = (TextView) convertView.findViewById(R.id.room_name);
			holder.roomPass = (ImageView) convertView.findViewById(R.id.room_password);
			holder.roomMy = (ImageView) convertView.findViewById(R.id.room_my);
			convertView.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();

		Room room = items.get(position);

		holder.roomName.setText(room.getName());

		if (room.isPassword()) {
			holder.roomPass.setImageResource(R.drawable.lock);
		} else {
			holder.roomPass.setImageResource(R.drawable.transparent);
		}
		
		if (room.isYours()) {
			holder.roomMy.setImageResource(R.drawable.star);
		} else {
			holder.roomMy.setImageResource(R.drawable.transparent);
		}

		convertView.setTag(R.id.ID_ROOM, room);

		return convertView;
	}

	static class ViewHolder {
		TextView roomName;
		ImageView roomPass;
		ImageView roomMy;
	}
}