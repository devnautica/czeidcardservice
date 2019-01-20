package com.devnautica.czeidcardservice.websocket.dto;

public class WSMessageDTOResponse {

	private String cmd;
	private Object msg;
	private String signature;

	public WSMessageDTOResponse(String cmd, Object msg, String signature) {
		this.cmd = cmd;
		this.msg = msg;
		this.signature = signature;
	}

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

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
