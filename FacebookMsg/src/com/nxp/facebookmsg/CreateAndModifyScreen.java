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
* Filename: CreateAndModifyScreen.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.Charset;

public class CreateAndModifyScreen extends MainScreen {


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle(R.string.msg_create);

        setContentView(R.layout.message_create_screen);

        final EditText te = (EditText) findViewById(R.id.createScreenEdit);
        final Intent i = getIntent();
        final String mode = i.getStringExtra("MODE");
        long id = 0;

        assert mode != null;
        if (mode.equals("Modify")) {
            setTitle(R.string.msg_modify);
            id = i.getLongExtra("MSG", 1);
            te.setText(MessageManager.getInstance().getMessageTextByID(id));
        } else if (mode.equals("Read")) {
            setTitle(R.string.msg_modify);
            te.setText(i.getStringExtra("READMSG"));
        }

        te.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                TextView messageSize = (TextView) findViewById(R.id.messageSize);
                String msg = te.getText().toString();
                byte[] textBytes = msg.getBytes(Charset.forName("UTF-8"));
                messageSize.setText("Message size: " + textBytes.length + " bytes");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        TextView messageSize = (TextView) findViewById(R.id.messageSize);
        String msg = te.getText().toString();
        byte[] textBytes = msg.getBytes(Charset.forName("UTF-8"));
        messageSize.setText("Message size: " + textBytes.length + " bytes");

        final long finalId = id;

        Button button = (Button) findViewById(R.id.store);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CreateAndModifyScreen.this, MessageScreen.class);
                if (mode.equals("Modify")) {
                    MessageManager.getInstance().modifyMessage(finalId, te.getText().toString());
                } else {
                    MessageManager.getInstance().addMessage(te.getText().toString());
                }
                startActivity(i);
            }
        });
    }
}
