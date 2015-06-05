package by.remsens.spectrometer;

import android.util.Log;

import com.ftdi.j2xx.FT_Device;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FtdiPacket {
	   ByteBuffer buffer = ByteBuffer.allocate(12);
	   int CS;
	   final static byte  GET_ID = 1;
	   final static byte  SET_EXP = 2;
	   final static byte  GET_SP = 4;
	   
	   FtdiPacket(FT_Device ftDev) {
		   byte[] data = new byte[12];
		   buffer.order(ByteOrder.LITTLE_ENDIAN);
		   ftDev.read(data);
		   buffer.put(data);
	   }
	   
	   FtdiPacket(byte[] array) {
		   buffer.order(ByteOrder.LITTLE_ENDIAN);
		   buffer.put(array);
		   if (!checkCS()) Log.d("FtdiPacket","Error_CS");		  
	   }
	   	  
	   
	   FtdiPacket(byte cmd, int param1, int param2, int param3) {
		   buffer.order(ByteOrder.LITTLE_ENDIAN);
		   buffer.put(0, (byte) 0xa5);		   		   		   
		   buffer.put(1, cmd);
		   buffer.putShort(2, (short)param1);
           buffer.putShort(4, (short)param2);
           buffer.putInt(6, param3);
		   setCS();
		   buffer.put(buffer.capacity()-1,(byte) 0x5a);
		   buffer.put(buffer.capacity()-2,(byte)CS);
	   }
	   
	   int readParam() {
		   return buffer.getInt(2);
	   }
	   
	   byte[] getByteArray() {
		   return buffer.array();
	   }
	   
	   void fromByteArray(byte[] array) {
		   buffer.put(array);
		   
	   }
	   
	   public static int unsignedToBytes(byte b) {
		    return b & 0xFF;
		  }
	   
	   void setCS() {
		   CS=0;
		   for (int i =0; i < 10; i++) {
			    CS += unsignedToBytes(buffer.get(i));
		   }
		   CS = 256-CS;
	   }
	   
	   boolean checkCS()  {
		   int checkCS = 0;
		   for (int i =1; i < 11; i++) {
			   checkCS += unsignedToBytes(buffer.get(i));
		   }
		
		return CS==checkCS;
	   }
	   

}
