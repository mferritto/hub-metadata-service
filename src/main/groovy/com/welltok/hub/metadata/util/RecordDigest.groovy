package com.welltok.hub.metadata.util

import java.security.MessageDigest

import org.apache.commons.codec.binary.Hex

class RecordDigest {
	
	def getHexSha1Digest(String obj) {
		MessageDigest md = MessageDigest.getInstance("SHA1")
		md.update(obj.bytes)
		new String(Hex.encodeHex(md.digest()))
	}
}
