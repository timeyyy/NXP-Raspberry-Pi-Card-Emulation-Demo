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
* Filename: MainScreen.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainScreen extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.app_name);
        ActionBar bar = getActionBar();

        if (bar != null)
            bar.setIcon(R.drawable.nxp_logo);

        initManager();

        if (NFCManager.getInstance().checkDeviceHasNFC()) {
            setContentView(R.layout.main_screen);
            initView();
        } else
            setContentView(R.layout.activity_main_no_nfc);

    }

    private void initManager() {
        MessageManager.getInstance().setContext(this);
        NFCManager.getInstance().setContext(this);
        AppManager.getInstance().setContext(this);
    }

    private void initView() {
        ListView lv = (ListView) findViewById(R.id.listViewMain);

        ItemObject item_data[] = new ItemObject[] {
            new ItemObject(R.string.header_read, R.string.description_read, R.drawable.main_menu_view),
            new ItemObject(R.string.header_write, R.string.description_write, R.drawable.main_menu_write),
        };

        lv.setAdapter(new MainArrayAdapter(this, item_data));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.firstLine);

                    if (tv == null)
                        return;

                    if ("Read".equals(tv.getText())) {
                        Intent myIntent = new Intent(getApplicationContext(), ReadAndWriteScreen.class);
                        myIntent.putExtra("MODE", "Read");
                        startActivity(myIntent);

                    } else if ("Write".equals(tv.getText())) {
                        Intent myIntent = new Intent(getApplicationContext(), MessageScreen.class);
                        startActivity(myIntent);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem m = menu.findItem(R.id.empty_store);
        if (m != null)
            m.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent in = new Intent(MainScreen.this, AboutScreen.class);
                startActivity(in);
                break;
            default:
                break;
        }

        return true;
    }

    public class ItemObject {
        public int header;
        public int description;
        public int image;

        public ItemObject(int header, int description, int image) {
            super();
            this.header = header;
            this.description = description;
            this.image = image;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if (NFCManager.getInstance().checkDeviceHasNFC()) {
            PendingIntent intent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass())
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
            if (adapter != null)
                adapter.enableForegroundDispatch(this, intent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter != null)
            adapter.disableForegroundDispatch(this);

    }

    public class MainArrayAdapter extends ArrayAdapter<String> {

        Context context;
        ItemObject[] item_data;

        public MainArrayAdapter(Context context, ItemObject[] item_data) {
            super(context, R.layout.main_screen_items);
            this.context = context;
            this.item_data = item_data;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RowObject obj = new RowObject();

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.main_screen_items, parent, false);

            assert v != null;
            obj.header = (TextView) v.findViewById(R.id.firstLine);
            obj.descripion = (TextView) v.findViewById(R.id.secondLine);
            obj.image = (ImageView) v.findViewById(R.id.icon);

            obj.header.setText(item_data[position].header);
            obj.descripion.setText(item_data[position].description);
            obj.image.setImageResource(item_data[position].image);

            return v;
        }

        @Override
        public int getCount() {
            return item_data.length;
        }

        private class RowObject {
            TextView header;
            TextView descripion;
            ImageView image;
        }
    }
}