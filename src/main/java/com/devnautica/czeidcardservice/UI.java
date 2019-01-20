package com.devnautica.czeidcardservice;

import com.devnautica.czeidcardservice.utils.InMemoryData;

import javax.swing.*;

public class UI {
	public static boolean okcancel(String message) {
		return okcancel("Permission request",message);
	}
	public static boolean okcancel(String header, String message) {
		boolean res = false;
		System.out.println("ASKING FOR PERMISSION("+header+"): "+message);
		if(InMemoryData.getInstance().isCardPresent()) {
			Object[] options = {"Allow",
					"Deny"};
			//int result = JOptionPane.showConfirmDialog(null, theMessage,"alert", JOptionPane.OK_CANCEL_OPTION,null,options,options[0]);

			int result = JOptionPane.showOptionDialog(null,
					message,
					header,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,     //do not use a custom Icon
					options,  //the titles of buttons
					options[0]); //default button title

			//System.out.println("ALLOW : "+result);
			res = (result == 0);
		}
		System.out.println("RESULT: "+res);
		return res;
	}
}
