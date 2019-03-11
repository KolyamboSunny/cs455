package cs455.scaling.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Hash {

	private byte[] challenge;
	public byte[] getChallenge() {
		return this.challenge;
	}
	
	private byte[] hash;
	public byte[] getHash() {
		return this.hash;
	}
	
	public Hash(byte[] data) {
		this.challenge = data;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA1");
			this.hash = digest.digest(data);
			
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Could not generate hash: SHA1 algortihm not found!");
		}
	}
	public Hash(byte[] challenge, byte[] hash) {
		this.challenge = challenge;
		this.hash = hash;
	}
	
	public boolean verify(byte[] hash) {
		Hash other = new Hash(this.getChallenge(), hash);
		return verify(other);
	}
	public boolean verify(Hash ideal) {
		if (!Arrays.equals(this.getChallenge(), ideal.getChallenge())) {
			System.err.println("Hashes were made from different data "+Arrays.toString(this.getChallenge())+" and "+Arrays.toString(ideal.getChallenge()));
			return false;
		}
		else
			return Arrays.equals(this.getHash(), ideal.getHash());
	}

	public String toString() {
		BigInteger hashInt = new BigInteger(1,this.getHash());
		
		return hashInt.toString(16);
	}
}
