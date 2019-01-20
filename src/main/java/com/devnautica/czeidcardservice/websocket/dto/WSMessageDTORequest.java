package com.devnautica.czeidcardservice.websocket.dto;

public class WSMessageDTORequest {
	public static final String CMD_HANDSHAKE = "handshake";
	public static final String CMD_READER_PRESENT_STATUS = "readerPresentStatus";
	public static final String CMD_CARD_PRESENT_STATUS = "cardPresentStatus";
	public static final String CMD_VIEW_AVAILABLE_DATA = "viewAvailableData";
	public static final String CMD_GET_DATA = "getData";
	public static final String CMD_ERRORS = "viewErrors";
	public static final String CMD_PING = "ping";

	private String cmd;
	private Object msg;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}
}
