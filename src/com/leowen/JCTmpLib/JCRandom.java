package com.leowen.JCTmpLib;

import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.RandomData;

public class JCRandom {
	
	public static final byte LENGTH_RANDOM_BUFFER = (byte) 0x0010;
	
	byte [] Random;
	
	protected JCRandom(){
		Random = JCSystem.makeTransientByteArray((short)LENGTH_RANDOM_BUFFER, JCSystem.CLEAR_ON_DESELECT);
	}
	
	/**
	 * Generate Random Data
	 * @param randomLen : random length, only less than 0x10
	 */
	public void genRandom(byte randomLen) {
		// Get Random Instance
		RandomData ICC = RandomData.getInstance((byte)RandomData.ALG_PSEUDO_RANDOM);

		// set Random seed
		ICC.setSeed(Random, (short)0, (short)randomLen);
		// generate Random
		ICC.generateData(Random, (short)0, (short)randomLen);
	}
	
	/**
	 * get Random data to specfic buffer
	 * @param buffer
	 * @param offset
	 * @param length
	 */
	public void getRandom(byte[] buffer, short offset, short length){
		Util.arrayCopyNonAtomic(Random, (short)0, buffer, (short)offset, (short)length);
	}
}
