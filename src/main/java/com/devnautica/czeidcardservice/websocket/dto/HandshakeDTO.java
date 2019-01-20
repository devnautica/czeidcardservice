package com.devnautica.czeidcardservice.websocket.dto;

public class HandshakeDTO {
	private String birthNumber;
	private String documentNumber;
	private String publicKeyBase64;
	private String shortCertBase64;


	public String getBirthNumber() {
		return birthNumber;
	}

	public void setBirthNumber(String birthNumber) {
		this.birthNumber = birthNumber;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getPublicKeyBase64() {
		return publicKeyBase64;
	}

	public void setPublicKeyBase64(String publicKeyBase64) {
		this.publicKeyBase64 = publicKeyBase64;
	}

	public String getShortCertBase64() {
		return shortCertBase64;
	}

	public void setShortCertBase64(String shortCertBase64) {
		this.shortCertBase64 = shortCertBase64;
	}
}
