package com.devnautica.czeidcardservice.websocket.server;

import com.devnautica.czeidcardservice.UI;
import com.devnautica.czeidcardservice.utils.FileEncoder;
import com.devnautica.czeidcardservice.utils.GlobalListener;
import com.devnautica.czeidcardservice.utils.InMemoryData;
import com.devnautica.czeidcardservice.websocket.dto.HandshakeDTO;
import com.devnautica.czeidcardservice.websocket.dto.WSMessageDTORequest;
import com.devnautica.czeidcardservice.websocket.dto.WSMessageDTOResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebsocketEndpoint extends WebSocketAdapter {
	private ObjectMapper omp = new ObjectMapper();
	private GlobalListener listener = GlobalListener.getInstance();

	private Session session = null;
	private String websiteName = null;
	private List<String> allowedMessages = new ArrayList<>();

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		this.session = sess;
		this.websiteName = sess.getRemote().getInetSocketAddress().getHostName();//.getHostString();
		System.out.println("Socket Connected: " + sess);
		//onWebSocketText("{\"cmd\":\"" + WSMessageDTORequest.CMD_HANDSHAKE + "\", \"msg\":null}");

		onWebSocketText("{\"cmd\":\"" + WSMessageDTORequest.CMD_READER_PRESENT_STATUS + "\", \"msg\":null}");
		onWebSocketText("{\"cmd\":\"" + WSMessageDTORequest.CMD_CARD_PRESENT_STATUS + "\", \"msg\":null}");
		listener.setWsSession(this.session);

		//System.out.println(sess.getRemote().getInetSocketAddress().getHostString());
		//System.out.println(sess.getRemote().getInetSocketAddress().getHostName());
	}

	private void addPermission(String msg) {
		allowedMessages.add(msg);
		InMemoryData.getInstance().setPermissions(websiteName, allowedMessages);
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);


		try {
			if (!message.equalsIgnoreCase("{\"cmd\":\"ping\",\"msg\":null}")) {
				System.out.println("Received TEXT message: " + message);
			}
			WSMessageDTORequest request = omp.readValue(message, WSMessageDTORequest.class);
			Object reqData = request.getMsg();
			Object resObj = null;
			switch (request.getCmd()) {
				case WSMessageDTORequest.CMD_PING:
					//resObj = "pong";
					break;
				case WSMessageDTORequest.CMD_READER_PRESENT_STATUS:
					InMemoryData.showInfo("Response", "Reader Status");
					resObj = InMemoryData.getInstance().isReaderPresent();
					break;
				case WSMessageDTORequest.CMD_CARD_PRESENT_STATUS:
					InMemoryData.showInfo("Response", "Card Status");
					resObj = InMemoryData.getInstance().isCardPresent();

					break;

				case WSMessageDTORequest.CMD_ERRORS:
					resObj = InMemoryData.getInstance().getErrors();

					break;
				case WSMessageDTORequest.CMD_VIEW_AVAILABLE_DATA:
					resObj = InMemoryData.getInstance().getAvailableDataKeys();

					break;
				case WSMessageDTORequest.CMD_HANDSHAKE:
					System.out.println("AWM " + allowedMessages + " |" + message);
					boolean allow = false;
					if (allowedMessages.contains(message)) {
						allow = true;
					}
					if (!allow) {
						allow = UI.okcancel("Website \"" + websiteName + "\" request your data: \nPublic Key, birthunmber, documentNumber and Short Certificate.");
						if (allow) {
							addPermission(message);
							System.out.println("ADDING! - " + allowedMessages);
						}
					}
					if (allow) {
						if(InMemoryData.getInstance().getShortCert() != null) {
							HandshakeDTO handshake = new HandshakeDTO();
							handshake.setBirthNumber(InMemoryData.getInstance().getData("birthNumber"));
							handshake.setDocumentNumber(InMemoryData.getInstance().getData("documentNumber"));
							handshake.setPublicKeyBase64(
									new String(
											FileEncoder.certDataToBase64String(
											InMemoryData.getInstance().getShortCert().getParsedCertificate().getPublicKey().getEncoded()

											)
									)
							);
							//handshake.setPublicKey("123-2-23");
							handshake.setShortCertBase64(FileEncoder.certDataToBase64String(InMemoryData.getInstance().getShortCert().getData()));

							resObj = handshake;
						}else{
							resObj = "card-not-inserted";
						}
					} else {
						resObj = "Permission denied";
					}

					InMemoryData.showInfo("Response", "Handshake");

					break;
				case WSMessageDTORequest.CMD_GET_DATA:


					Map<String, String> res = new LinkedHashMap<>();
					for (String dataField : (List<String>) reqData) {
						String datum = InMemoryData.getInstance().getData(dataField);
						if (datum == null) {
							datum = "not implemented";
							if (!InMemoryData.getInstance().isCardPresent()) {
								datum = "card not present";
							}
						}
						res.put(dataField, datum);
					}


					allow = false;
					if (allowedMessages.contains(message)) {
						System.out.println("REMEMBERED");
						allow = true;
					}
					if (!allow) {
						allow = UI.okcancel("Website " + websiteName + " request list of your data: \n." + res.keySet());
						if (allow) {
							addPermission(message);
						}
					}
					if (allow) {
						resObj = res;
					} else {
						resObj = "Permission denied";
					}

					InMemoryData.showInfo("Response", "Data - " + resObj);

					break;
			}
			if (resObj != null) {
				WSMessageDTOResponse msg = new WSMessageDTOResponse(request.getCmd(), resObj, null);
				String resStr = omp.writeValueAsString(msg);
				System.out.println("SENDING: " + resStr);
				session.getRemote().sendString(resStr);
			}else{
				//System.out.println("NO RESPONSE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println("Received TEXT message: " + message);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		System.out.println("Socket Closed: [" + statusCode + "] " + reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}
}