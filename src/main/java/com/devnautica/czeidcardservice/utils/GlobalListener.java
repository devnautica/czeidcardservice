package com.devnautica.czeidcardservice.utils;

import com.devnautica.czeidcardservice.websocket.dto.WSMessageDTORequest;
import com.devnautica.czeidcardservice.websocket.dto.WSMessageDTOResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.paralelnipolis.obcanka.core.HexUtils;
import cz.paralelnipolis.obcanka.core.card.Card;
import cz.paralelnipolis.obcanka.core.certificates.Certificate;
import cz.paralelnipolis.obcanka.desktop.lib.DesktopCardInterface;
import org.eclipse.jetty.websocket.api.Session;

import java.util.LinkedList;
import java.util.Queue;

public class GlobalListener {
	private static Session wsSession = null;
	private static Queue<String> queue = new LinkedList();

	private static GlobalListener ourInstance = new GlobalListener();

	public static GlobalListener getInstance() {
		return ourInstance;
	}

	private GlobalListener() {
	}

	private ObjectMapper omp = new ObjectMapper();

	public void setReaderPresent(boolean readerPresent) {
		if (InMemoryData.getInstance().isReaderPresent() != readerPresent) {
			System.out.println("PRESENCE CHANGED READER PRESENT: " + readerPresent);

			sendMessage(WSMessageDTORequest.CMD_READER_PRESENT_STATUS, readerPresent);

			if (readerPresent) {
				InMemoryData.getInstance().removeError("terminal-usb");
			} else {
				InMemoryData.getInstance().setError("terminal-usb", "There are no terminals connected");
			}

			InMemoryData.getInstance().setReaderPresent(readerPresent);
		}

	}

	public void setCardPresent(boolean cardPresent) {

		if (InMemoryData.getInstance().isCardPresent() != cardPresent) {
			System.out.println("PRESENCE CHANGED CARD PRESENT: " + cardPresent);
			InMemoryData.getInstance().setCardPresent(cardPresent);
			sendMessage(WSMessageDTORequest.CMD_CARD_PRESENT_STATUS, cardPresent);
			if (cardPresent) {
				try {
					DesktopCardInterface ci = DesktopCardInterface.create();
					Card cm = new Card(ci);
					String cardID = cm.getCardNumber();
					InMemoryData.getInstance().setData("cardID", cardID);
					byte[] serialNumber = cm.getSerialNumber();
					InMemoryData.getInstance().setData("serialNumber", HexUtils.bytesToHexStringWithSpacesAndAscii(serialNumber));
					InMemoryData.getInstance().setData("DokState", cm.getDokState() + "");
					InMemoryData.getInstance().setData("DokTryLimit", cm.getDokTryLimit() + "");
					InMemoryData.getInstance().setData("DokMaxTryLimit", cm.getDokMaxTryLimit() + "");

					InMemoryData.getInstance().setData("documentNumber", cm.getCardNumber());

					InMemoryData.getInstance().setData("IokState", cm.getIokState() + "");
					InMemoryData.getInstance().setData("IokMaxTryLimit", cm.getIokMaxTryLimit() + "");
					InMemoryData.getInstance().setData("IokTryLimit", cm.getIokTryLimit() + "");

					Certificate longCert = cm.getCertificate(Certificate.CertificateType.IDENTIFICATION);
					InMemoryData.getInstance().setLongCert(longCert);
					//InMemoryData.getInstance().setData("longCert = " + longCert);

					Certificate shortCert = cm.getCertificate(Certificate.CertificateType.AUTHORIZATION);
					InMemoryData.getInstance().setShortCert(shortCert);
					//InMemoryData.getInstance().setData("shortCert = " + longCert);


					for (String pairS : longCert.getParsedCertificate().getSubjectDN().getName().split(",")) {
						String[] pair = pairS.split("=");
						String name = pair[0];
						if (name.startsWith(" ")) {
							name = name.substring(1);
						}
						String value = pair[1];

						switch (name) {
							case "C"://country
								InMemoryData.getInstance().setData("country_short", value);
								break;
							case "O"://issuer
								InMemoryData.getInstance().setData("issuer", value);
								break;
							case "OID.1.2.203.7064.1.1.11.1"://country
								InMemoryData.getInstance().setData("birthday", value);
								break;
							case "OID.1.2.203.7064.1.1.11.2"://sex
								InMemoryData.getInstance().setData("sex", value);
								break;
							case "OID.1.2.203.7064.1.1.11.3"://birthplace
								InMemoryData.getInstance().setData("birthplace", value);
								break;
							case "OID.1.2.203.7064.1.1.11.4"://country
								InMemoryData.getInstance().setData("okres", value);
								break;
							case "OID.1.2.203.7064.1.1.11.5"://country
								InMemoryData.getInstance().setData("birthNumber", value);
								break;
							case "OID.1.2.203.7064.1.1.11.6"://country
								InMemoryData.getInstance().setData("status", value);
								break;
							case "OID.1.2.203.7064.1.1.11.7"://country
								InMemoryData.getInstance().setData("country", value);
								break;
						}
						System.out.println("|" + name + "=" + value + "|");
					}
					InMemoryData.getInstance().setData("shortCert", FileEncoder.certDataToBase64String(shortCert.getData()));
				} catch (Exception e) {

				}
			} else {
				InMemoryData.getInstance().clearData();
			}
		}
	}

	public void sendMessage(String cmd, Object msg) {
		try {
			WSMessageDTOResponse res = new WSMessageDTOResponse(cmd, msg, "");
			String r = omp.writeValueAsString(res);
			sendMessage(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg) {
		queue.add(msg);
		System.out.println("SEND MESSAGE: " + msg);
		sendMessagesInQueue();
	}

	public void sendMessagesInQueue() {
		if (wsSession != null && wsSession.isOpen()) {
			for (int i = 0; i < queue.size(); i++) {
				String msg = queue.poll();
				if (msg != null) {
					try {
						wsSession.getRemote().sendString(msg);
					} catch (Exception e) {
						queue.add(msg);
					}
				}
			}
			/*try {

				WSMessageDTOResponse res = new WSMessageDTOResponse(WSMessageDTORequest.CMD_CARD_PRESENT_STATUS,
						InMemoryData.getInstance().isCardPresent(), "");



			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}

	public static Session getWsSession() {
		return wsSession;
	}

	public void setWsSession(Session wsSession) {
		this.wsSession = wsSession;
		sendMessagesInQueue();
	}
}
