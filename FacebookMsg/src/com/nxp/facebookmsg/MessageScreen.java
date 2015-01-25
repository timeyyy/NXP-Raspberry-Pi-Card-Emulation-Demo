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
* Filename: MessageScreen.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MessageScreen extends MainScreen implements OnClickListener {

    long messageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.msg_store);

        if (NFCManager.getInstance().checkDeviceHasNFC()) {
            setContentView(R.layout.message_screen);
            init();
        } else
            setContentView(R.layout.activity_main_no_nfc);
    }

    public void init() {
        initViews();
    }

    private void initViews() {
        changeEnableState(false, R.id.Modify);
        changeEnableState(false, R.id.Write);
        changeEnableState(false, R.id.Delete);

        findViewById(R.id.Modify).setOnClickListener(this);
        findViewById(R.id.Create).setOnClickListener(this);
        findViewById(R.id.Write).setOnClickListener(this);
        findViewById(R.id.Delete).setOnClickListener(this);

        final ListView view = (ListView) findViewById(R.id.listView);

        view.setAdapter(MessageManager.getInstance().getAdapter());

        view.setSelector(R.layout.listview_background);

        view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long id) {
                messageID = id;

                changeEnableState(true, R.id.Modify);
                changeEnableState(true, R.id.Write);
                changeEnableState(true, R.id.Delete);
            }
        });
    }

    public void changeEnableState(boolean value, int view) {
        Button button = (Button) findViewById(view);
        button.setEnabled(value);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.empty_store);
        if(item != null)
        item.setVisible(true);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
            	Intent in = new Intent(MessageScreen.this, AboutScreen.class);
                startActivity(in);
                break;
            case R.id.empty_store:
                delteAllMsg();
                changeEnableState(false, R.id.Modify);
                changeEnableState(false, R.id.Write);
                changeEnableState(false, R.id.Delete);
                break;
            default:
                break;
        }

        return true;
    }

    public void delteAllMsg() {

        AppManager.getInstance().openDialog("Delete",
                "All the messagese will be deleted", "OK", "Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MessageManager.getInstance().deleteAllMessages();
                    }

                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                }
        );

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MessageScreen.this, MainScreen.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.Write:
                i = new Intent(MessageScreen.this, ReadAndWriteScreen.class);
                i.putExtra("MODE", "Write");
                i.putExtra("MSG", MessageManager.getInstance().getMessageTextByID(messageID));
                startActivity(i);
                break;

            case R.id.Create:
                i = new Intent(MessageScreen.this, CreateAndModifyScreen.class);
                i.putExtra("MODE", "Add");
                startActivity(i);
                break;

            case R.id.Modify:
                i = new Intent(MessageScreen.this, CreateAndModifyScreen.class);
                i.putExtra("MODE", "Modify");
                i.putExtra("MSG", messageID);
                startActivity(i);
                break;

            case R.id.Delete:
                MessageManager.getInstance().deleteMessageByID(messageID);
                if (MessageManager.getInstance().checkMessagesEmpty()) {
                    changeEnableState(false, R.id.Modify);
                    changeEnableState(false, R.id.Write);
                    changeEnableState(false, R.id.Delete);
                }
                break;

            default:
                break;

        }
    }
}