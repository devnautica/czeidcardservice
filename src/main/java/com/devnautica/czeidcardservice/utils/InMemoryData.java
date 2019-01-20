package com.devnautica.czeidcardservice.utils;

import cz.paralelnipolis.obcanka.core.certificates.Certificate;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryData {
	private static InMemoryData ourInstance = new InMemoryData();
	private static TrayIcon trayIcon = null;
	private static Map<String,List<String>> allowedMsgs = new HashMap<>();
	public static InMemoryData getInstance() {
		return ourInstance;
	}

	private InMemoryData() {
	}


	public static final String KEY_LONG_CERT = "longCertBase64";
	public static final String KEY_SHORT_CERT = "shortCertBase64";
	//public static final String KEY_IS_CARD_PRESENT = "isCardPresent";
	public static Map<String,String> errors = new HashMap<>();

	private Certificate longCert;
	private Certificate shortCert;

	private boolean cardPresent = false;
	private boolean readerPresent = false;


	private Map<String, String> data = new HashMap<>();

	public void setPermissions(String website,List<String> perms){
		allowedMsgs.put(website,perms);
	}

	public static Map<String, List<String>> getAllowedMsgs() {
		return allowedMsgs;
	}

	public void setData(String key, String value) {
		data.put(key, value);
	}

	public String getData(String key) {
		return data.get(key);
	}

	public void clearData() {
		data.clear();
	}

	public Set<String> getAvailableDataKeys() {
		return data.keySet();
	}

	public Certificate getLongCert() {
		return longCert;
	}

	public boolean isCardPresent() {
		return cardPresent;
	}

	public boolean isReaderPresent() {
		return readerPresent;
	}

	public void setCardPresent(boolean value) {
		this.cardPresent = value;
	}

	public void setReaderPresent(boolean value) {
		this.readerPresent = value;
	}

	public void setError(String name, String value){
		errors.put(name,value);
	}
	public void removeError(String name){
		errors.remove(name);
	}

	public void setLongCert(Certificate longCert) {
		this.longCert = longCert;
		String certBase64 = FileEncoder.certDataToBase64String(longCert);
		data.put(KEY_LONG_CERT, certBase64);
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public Certificate getShortCert() {
		return shortCert;
	}

	public void setShortCert(Certificate shortCert) {
		this.shortCert = shortCert;
		String certBase64 = FileEncoder.certDataToBase64String(shortCert);
		data.put(KEY_SHORT_CERT, certBase64);
	}

	public static TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public static void showInfo(String head, String msg){
		//trayIcon.displayMessage("",head+" "+msg, TrayIcon.MessageType.NONE);
	}

	public static void setTrayIcon(TrayIcon trayIcon) {
		InMemoryData.trayIcon = trayIcon;
	}
}
