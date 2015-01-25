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
* Filename: AppManager.java
*
*
*******************************************************************************/

package com.nxp.facebookmsg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AppManager {

	private static AppManager instance;

	private Context context;

	public AppManager() {
	}

    public static AppManager getInstance() {
		if (instance == null)
			instance = new AppManager();

		return instance;
	}

	public void setContext(Context context) {
		this.context = context;
	}

    public void openDialog(String title, String message, String positiveText,
			String negativeText,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(positiveText, positiveListener);
		if (negativeText != null)
			builder.setNegativeButton(negativeText, negativeListener);

		AlertDialog altert = builder.create();
		altert.show();
	}

}