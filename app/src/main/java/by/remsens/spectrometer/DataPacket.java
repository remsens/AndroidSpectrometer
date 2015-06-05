    package by.remsens.spectrometer;

import com.ftdi.j2xx.FT_Device;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataPacket {
	  ByteBuffer buffer = ByteBuffer.allocate(10+(16+13+3+3648+16)*2+1);
	  
	  DataPacket(FT_Device ftDev) {
		  byte[] data = new byte[10+(16+13+3+3648+16)*2+1];
		  buffer.order(ByteOrder.LITTLE_ENDIAN);
		  ftDev.read(data);
		
		  buffer.put(data);
		  
	  }
	  
	  DataPacket(byte[] array) {
		  buffer.order(ByteOrder.LITTLE_ENDIAN);
		  buffer.put(array);
	  }
	  
public Spectrum getSpectrum() {
          byte[] spectrumHeader = new byte[74];
          for (int i = 0; i < spectrumHeader.length; i++)
              spectrumHeader[i] = buffer.get(i);

		  int[] spectrumArray = new int[3648];
		  for (int i = 0; i <3648; i++)
			  spectrumArray[i] = buffer.getShort(10+(16+13+3)*2+i*2) & 0xffff;

		return new Spectrum(spectrumArray,spectrumHeader);
		  
	  }
	  
	  int size() {
		  return buffer.capacity();
	  }
}
