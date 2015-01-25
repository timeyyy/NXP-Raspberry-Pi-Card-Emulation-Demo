/*******************************************************************************
* Copyright (c), NXP Semiconductors Gratkorn / Austria
*
* (C)NXP Semiconductors
* All rights are reserved. Reproduction in whole or in part is
* prohibited without the written consent of the copyright owner.
* NXP reserves the right to make changes without notice at any time.
* NXP makes no warranty, expressed, implied or statutory, including but
* not limited to any implied warranty of merchantability or fitness for any
* particular purpose, or that the use will not infringe any third party patent,
* copyright or trademark. NXP must not be liable for any loss or damage
* arising from its use.
********************************************************************************
*
* Filename: MessageManager.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MessageManager {


    public MessageManager() {
    }

    private static MessageManager instance;

    public void setContext(Context context) {
        this.context = context;
        init();
    }

    Context context;

    public static MessageManager getInstance() {
        if (instance == null)
            instance = new MessageManager();

        return instance;
    }

	private SQLiteDatabase db;
	private MessageManagerHelper managerHelper;
	private String[] allColumns = { MessageManagerHelper.COLUMN_ID,
			MessageManagerHelper.COLUMN_MESSAGE };

    ArrayList<MessageObject> msgObjs;

    public ArrayAdapter<MessageObject> getAdapter() {
        return adapter;
    }

    ArrayAdapter<MessageObject> adapter;

	private void init() {
        managerHelper = new MessageManagerHelper(context);
		open();
		msgObjs = new ArrayList<MessageObject>();
		msgObjs = (ArrayList<MessageObject>) getAllMessages();
		adapter = new MessageStoreArrayAdapter(context);
	}


	public void open() throws SQLException {
		db = managerHelper.getWritableDatabase();
	}

    private MessageObject getObjAtListPostion(int pos) {
		return adapter.getItem(pos);
	}

	public MessageObject addNewMessage(String comment) {
		ContentValues values = new ContentValues();
		values.put(MessageManagerHelper.COLUMN_MESSAGE, comment);
		long insertId = db.insert(MessageManagerHelper.TABLE_MESSAGES, null,
				values);
		Cursor cursor = db.query(MessageManagerHelper.TABLE_MESSAGES,
				allColumns, MessageManagerHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		MessageObject newMsg = cursorToMessage(cursor);
		cursor.close();
		return newMsg;
	}


	public void deleteMessageByID(long id) {
		long msgId = getObjAtListPostion((int) id).getId();
        db.delete(MessageManagerHelper.TABLE_MESSAGES,
				MessageManagerHelper.COLUMN_ID + " = " + msgId, null);

        if (!checkMessagesEmpty())
		    msgObjs.remove(getObjAtListPostion((int) id));

		adapter.notifyDataSetChanged();
	}

    public boolean checkMessagesEmpty() {
        return msgObjs.size() <= 0;
    }

	public void deleteAllMessages() {
		db.delete(MessageManagerHelper.TABLE_MESSAGES, null, null);
        msgObjs.clear();
		adapter.notifyDataSetChanged();
	}

	public void modifyMessage(long id, String msg) {
		MessageObject obj = getObjAtListPostion((int) id);
		obj.setMessage(msg);

		ContentValues values = new ContentValues();
		values.put(MessageManagerHelper.COLUMN_MESSAGE, msg);
		db.update(MessageManagerHelper.TABLE_MESSAGES, values,
				MessageManagerHelper.COLUMN_ID + "=" + obj.getId(), null);

		adapter.notifyDataSetChanged();
	}

	public List<MessageObject> getAllMessages() {
		List<MessageObject> msgs = new ArrayList<MessageObject>();

		Cursor cursor = db.query(MessageManagerHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			MessageObject msg = cursorToMessage(cursor);
			msgs.add(msg);
			cursor.moveToNext();
		}
		cursor.close();
		return msgs;
	}

	public MessageObject cursorToMessage(Cursor cursor) {
		MessageObject msg = new MessageObject();
		msg.setId(cursor.getLong(0));
		msg.setMessage(cursor.getString(1));
		return msg;
	}

	public String getMessageTextByID(long id) {

		return getObjAtListPostion((int) id).getMessage();
	}

	public void addMessage(String msg) {
		if (!msg.isEmpty()) {
			MessageObject obj;
			obj = addNewMessage(msg);
			msgObjs.add(obj);
			adapter.notifyDataSetChanged();
		}
	}

    public class MessageStoreArrayAdapter extends ArrayAdapter<MessageObject> {

        private Context context;

        public MessageStoreArrayAdapter(Context context) {
            super(context, R.layout.message_listview_items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.message_listview_items, parent, false);

            assert v != null;
            TextView text = (TextView) v.findViewById(R.id.message_view);
            TextView byteView = (TextView) v.findViewById(R.id.messsage_view_bytes);

            MessageObject obj = msgObjs.get(position);

            if (obj != null) {
                text.setText(obj.getMessage().toString());
                byte[] textBytes = obj.getMessage().toString().getBytes(Charset.forName("UTF-8"));
                byteView.setText(textBytes.length + " bytes");

            }

            return v;
        }

        @Override
        public int getCount() {
            return msgObjs.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public MessageObject getItem(int position) {
            return msgObjs.get(position);
        }

    }
}
