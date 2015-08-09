package com.leowen.JCTmpLib;

import javacard.security.DESKey;
import javacard.security.KeyBuilder;
import javacardx.crypto.Cipher;

public class JCDES {
	
	private DESKey deskey;
	Cipher CipherObj;
	
	protected JCDES(){
	}
	
	/**
	 * do DES cryp
	 * @param data
	 * @param offset
	 * @param length
	 * @param key
	 * @param keyOffset
	 * @return
	 */
	public short DESCryp(byte [] data, short offset, short length, byte [] key, short keyOffset){
		// key object
		deskey = (DESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
		deskey.setKey(key, (short)keyOffset);

		// key  crpy object
		CipherObj = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M2, false);
		// set key and cryp mode
		CipherObj.init(deskey, Cipher.MODE_ENCRYPT);
		// cryp
		return CipherObj.doFinal(data, (short)offset, (short)length, data, (short)offset);

	}

}
