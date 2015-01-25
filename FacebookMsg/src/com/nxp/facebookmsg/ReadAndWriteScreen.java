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
* Filename: ReadAndWriteScreen.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class ReadAndWriteScreen extends MainScreen {

    boolean read;
    String mode;
    Intent i;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read_write_screen);

        i = getIntent();
        mode = i.getStringExtra("MODE");

        assert mode != null;
        if(mode.equals("Read")) {
            setTitle(R.string.msg_read);
            getActionBar().setIcon(R.drawable.main_menu_view);
            initRead();
            read = true;
        } else if (mode.equals("Write")) {
            setTitle(R.string.msg_write);
            getActionBar().setIcon(R.drawable.main_menu_write);
            initWrite();
            read = false;
        }
    }

    private void initRead() {
        TextView first = (TextView) findViewById(R.id.firstText);
        first.setText(R.string.first_text_read);
        TextView sec = (TextView) findViewById(R.id.secondText);
        sec.setText(R.string.second_text_read);
        ImageView image = (ImageView) findViewById(R.id.RWImageView);
        image.setImageResource(R.drawable.tag_scan_illustration);
    }

    private void initWrite() {
        TextView first = (TextView) findViewById(R.id.firstText);
        first.setText(R.string.first_text_write);
        TextView sec = (TextView) findViewById(R.id.secondText);
        sec.setText(R.string.second_text_write);
        ImageView image = (ImageView) findViewById(R.id.RWImageView);
        image.setImageResource(R.drawable.tag_scan_illustration);
    }
    @SuppressWarnings("static-access")
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Parcelable[] msgs = intent.getParcelableArrayExtra(NFCManager.getInstance().getNFCAdapter().EXTRA_NDEF_MESSAGES);

		if (NFCManager.getInstance().getNFCAdapter().ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

			if (mode.equals("Write")) {

				NFCManager.getInstance().writeToDevice(i.getStringExtra("MSG"), intent);

                Intent i = new Intent(ReadAndWriteScreen.this, MessageScreen.class);
                startActivity(i);

			} else {
				if (msgs == null)
					return;

				NdefMessage nmsgs = (NdefMessage) msgs[0];

				if (nmsgs == null)
					return;

				if (nmsgs.getRecords()[0].getTnf() == 1) {
					if (Arrays.equals(nmsgs.getRecords()[0].getType(),
                            NdefRecord.RTD_TEXT)) {

                        Intent i = new Intent(ReadAndWriteScreen.this, CreateAndModifyScreen.class);
                        i.putExtra("MODE", "Read");
                        i.putExtra("READMSG", NFCManager.getInstance().readFromDevice(getIntent()));
                        startActivity(i);

					} else {
						Toast.makeText(getApplicationContext(), R.string.msg_no_text_format,
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(getApplicationContext(), R.string.msg_no_wellknown_type,
							Toast.LENGTH_LONG).show();
				}
			}
		}
    }
}
