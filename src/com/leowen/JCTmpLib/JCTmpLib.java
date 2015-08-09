package com.leowen.JCTmpLib;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;

public class JCTmpLib extends Applet{
	
	public static final byte INS_GET_CHALLENGE = (byte) 0x84;
	public static final byte INS_EXT_AUTH      = (byte) 0x82;
	
	JCRandom jcRandom;
	JCDES    jcDES;
	private byte[] commBuffer;
	private byte[] keyData = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
	

	
	protected JCTmpLib(){
		commBuffer = JCSystem.makeTransientByteArray((short)0x00FF, JCSystem.CLEAR_ON_DESELECT);
		
		jcRandom = new JCRandom();
		jcDES    = new JCDES();
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
		case INS_GET_CHALLENGE:
			jcRandom.genRandom((byte)lc);
		    jcRandom.getRandom(apduBuffer, (short)0, (short)lc);
			apdu.setOutgoingAndSend((short)0, (short)lc);
			break;
			
		case INS_EXT_AUTH:
			doAuthentication(apdu);
			break;
		default:
			// good practice: If you don't know the INStruction, say so:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}
	
	private void doAuthentication(APDU apdu) {
		byte[] apduBuffer= apdu.getBuffer();
		byte ins = apduBuffer[ISO7816.OFFSET_INS];
		byte p1 = apduBuffer[ISO7816.OFFSET_P1];
		byte p2 = apduBuffer[ISO7816.OFFSET_P1];
		short lc = (short)((short)apduBuffer[ISO7816.OFFSET_LC] & 0x00FF);
		
		apdu.setIncomingAndReceive();
		
		// DES
		Util.arrayCopyNonAtomic(apduBuffer, (short)ISO7816.OFFSET_CDATA, commBuffer, (short)0, (short)(lc/2));
		short len = jcDES.DESCryp(commBuffer, (short)0, (short)(lc/2), keyData, (short)0);
		
		// Compare
		if (Util.arrayCompare(commBuffer, (short)0, apduBuffer, (short)13, (short)8)!= 0) {
			ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
		}

	}

}
