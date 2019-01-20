package com.devnautica.czeidcardservice.websocket;

import com.devnautica.czeidcardservice.utils.CardCheckTimerTask;
import com.devnautica.czeidcardservice.utils.GlobalListener;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestHWStatus {
	private static boolean checking = false;
	public static void startChecking(){
		if(!checking){
			checking = true;
			TimerTask timerTask = new CardCheckTimerTask();
			Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(timerTask, 0, 250);
		}
	}
	private static void checkChange() throws Exception {
		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list();

		if (terminals.size() == 1) {
			for (CardTerminal terminal : terminals) {
				System.out.println("CARD PRESENCE"+terminal.isCardPresent());
				if(terminal.isCardPresent()){
					GlobalListener.getInstance().setCardPresent(true);
					terminal.waitForCardAbsent(99999999999L);
					checkChange();
				}else{
					GlobalListener.getInstance().setCardPresent(false);
					terminal.waitForCardPresent(9999999999L);
					checkChange();
				}
			}
		} else if (terminals.size() == 0) {
			throw new Exception("There are no terminals connected");
		} else {
			throw new Exception("There are more then 1 terminals connected");
		}
	}

}
