package com.devnautica.czeidcardservice.utils;

import cz.paralelnipolis.obcanka.desktop.lib.DesktopCardInterface;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.TimerTask;

public class CardCheckTimerTask extends TimerTask {
	@Override
	public void run() {
		try {

			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			if (terminals.size() == 0) {//&& InMemoryData.getInstance().isReaderPresent()){
				GlobalListener.getInstance().setReaderPresent(false);
			} else {// && !InMemoryData.getInstance().isReaderPresent()) {
				GlobalListener.getInstance().setReaderPresent(true);

				if (terminals.size() == 1) {
					InMemoryData.getInstance().removeError("terminal-count");

					for (CardTerminal terminal : terminals) {
						if (terminal.isCardPresent()) {
							GlobalListener.getInstance().setCardPresent(true);
						} else if (!terminal.isCardPresent()) {
							GlobalListener.getInstance().setCardPresent(false);
						}
					}
				} else {
					InMemoryData.getInstance().setError("terminal-count", "There are more then 1 terminals connected");
					//throw new Exception("There are more then 1 terminals connected");
				}
			}
			InMemoryData.getInstance().removeError("terminal");
		}catch (CardException e) {
			GlobalListener.getInstance().setReaderPresent(false);
			GlobalListener.getInstance().setCardPresent(false);

		}catch (Exception e) {
			e.printStackTrace();
			InMemoryData.getInstance().setError("terminal", e.getMessage());
		}
	}

	private static void checkTerminalsAndCard() {

	}
}
