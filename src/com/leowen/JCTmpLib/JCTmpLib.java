package com.leowen.JCTmpLib;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class JCTmpLib extends Applet{
	
	public static final byte INS_GET_CHALLENGE = (byte) 0x84;
	
	JCRandom jcRandom;
	
	protected JCTmpLib(){
		jcRandom = new JCRandom();
			
	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// GP-compliant JavaCard applet registration
		new JCTmpLib().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu) {
		// Good practice: Return 9000 on SELECT
		if (selectingApplet()) {
			return;
		}

		byte[] apduBuffer= apdu.getBuffer();
		byte ins = apduBuffer[ISO7816.OFFSET_INS];
		byte p1 = apduBuffer[ISO7816.OFFSET_P1];
		byte p2 = apduBuffer[ISO7816.OFFSET_P1];
		short lc = (short)((short)apduBuffer[ISO7816.OFFSET_LC] & 0x00FF);
		
		switch (ins) {
		case (byte) INS_GET_CHALLENGE:
			jcRandom.genRandom((byte)lc);
		    jcRandom.getRandom(apduBuffer, (short)0, (short)lc);
			apdu.setOutgoingAndSend((short)0, (short)lc);
			break;
		default:
			// good practice: If you don't know the INStruction, say so:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
