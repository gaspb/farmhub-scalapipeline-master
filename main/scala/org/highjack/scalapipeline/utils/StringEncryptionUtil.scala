package org.highjack.scalapipeline.utils
import java.net.{URLDecoder, URLEncoder}

import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
  * Created by High Jack on 21/10/2018.
  */
object StringEncryptionUtil {

    val key:String = "Bar12345Bar12345"; // 128 bit key
    val initVector:String  = "RandomInitVector"; // 16 bytes IV

    def consistentEncrypt(str:String):String ={
        str //TODO
    }



    def encrypt( value: String): String = {
        try {



            val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
            val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(URLEncoder.encode(value, "UTF-8").getBytes)
            System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted))
            return Base64.encodeBase64String(encrypted);
        } catch {
            case ex: Exception =>
                ex.printStackTrace()
        }
        null
    }

    def decrypt(encrypted: String): String = {
        try {
            val iv = new IvParameterSpec(initVector.getBytes("UTF-8"))
            val skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(Base64.decodeBase64(encrypted))
            return URLDecoder.decode(new String(original), "UTF-8")
        } catch {
            case ex: Exception =>
                ex.printStackTrace()
        }
        null
    }





    def encryptAES(text:String): String = {


        // 128 bit key
        // Create key and cipher
        val aesKey = new SecretKeySpec(key.getBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encrypted = cipher.doFinal(text.getBytes)

        val sb = new StringBuilder
        for (b <- encrypted) {
            sb.append(b.toChar)
        }

        // the encrypted String
       sb.toString

    }

    def decryptAES(enc:String): String = {
        val aesKey = new SecretKeySpec(key.getBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        // now convert the string to byte array
        // for decryption
        val bb = new Array[Byte](enc.length)
        var i = 0
        while ( {
            i < enc.length
        }) {
            bb(i) = enc.charAt(i).toByte

            {
                i += 1; i - 1
            }
        }

        // decrypt the text
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        new String(cipher.doFinal(bb))

    }
}
