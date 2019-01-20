package com.devnautica.czeidcardservice.utils;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.TimerTask;

public class TerminalCheckTimerTask extends TimerTask {
	@Override
	public void run() {
		int terminalsCount = getTerminalsCount();
		System.out.println("PRESENCE: "+terminalsCount);
		if(terminalsCount == 0 && InMemoryData.getInstance().isReaderPresent()){
			InMemoryData.getInstance().setReaderPresent(false);
		}else if(terminalsCount != 0 && !InMemoryData.getInstance().isReaderPresent()){
			InMemoryData.getInstance().setReaderPresent(true);
		}
	}
	private static int getTerminalsCount(){
		try {
			TerminalFactory factory = TerminalFactory.getDefault();
			CardTerminals terminals = factory.terminals();


			List<CardTerminal> allTerminals = terminals.list();
			return allTerminals.size();
		}catch (Exception e){
			return 0;
		}
	}
}
