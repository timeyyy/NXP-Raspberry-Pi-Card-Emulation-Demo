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
* Filename: AboutScreen.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutScreen extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setTitle(R.string.app_about);

            setContentView(R.layout.about_screen);

            TextView tv = (TextView) findViewById(R.id.aboutText);

            String about = "<b>NFC FacebookMsg by NXP Advanced Features</b><br>" +
                           "<small>Version: 1.0 2013-09-24 13:47:54</small><br><br>" +
                           "<small>&copy 2013, NXP Semiconductors Gratkorn,<br>" +
                           "<a href=\"http://www.nxp.com\"> www.nxp.com </a>";

            tv.setText(Html.fromHtml(about));
        }
    }
