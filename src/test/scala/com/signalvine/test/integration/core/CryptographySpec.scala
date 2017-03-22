package com.signalvine.test.integration.core

import com.signalvine.integration.core.Cryptography
import org.specs2.mutable.Specification

class CryptographySpec extends Specification {
  "Cryptography" should {
    "match the encrypted and actual text with cipher text when decrypted" >> {
      val key = "signalVine"
      val value = "NullProvider authentication information"
      val encryptedData = Cryptography.encrypt(key, value)
      Cryptography.decrypt(key, encryptedData) mustEqual value
    }
  }
}

