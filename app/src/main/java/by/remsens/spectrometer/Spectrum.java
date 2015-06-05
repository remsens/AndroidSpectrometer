package by.remsens.spectrometer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;




public class Spectrum {
	 int[] spectrum;
     byte[] header;
	 
	 public Spectrum() {
		 spectrum = new int[3648]; header = new byte[74];
	 }
	 
	 public Spectrum(String FileName) {
		 spectrum = new int[3648];
         header = new byte[74];
		 LoadFromFile(FileName);
	 }
	 
	 public Spectrum(int[] fromArray, byte[] headerArray) {
			 spectrum = fromArray;
             header = headerArray;

	 }
	 
	 public Spectrum(int size, int headerSize) {
		 spectrum = new int[size];
         header = new byte[headerSize];
	 }
	 
	 void addSpectrum(Spectrum other) {		 
		 int array_size = (this.size()<=other.size()) ? this.size() : other.size();
		 
		 for (int i = 0; i < array_size; i++) {
			 spectrum[i] +=other.getItem(i);
		 }
					 		 
	 }
	 
	 void subSpectrum(Spectrum other) {		 
		 int array_size = (this.size()<=other.size()) ? this.size() : other.size();
		 for (int i = 0; i < array_size; i++) {
			 spectrum[i] -=other.getItem(i);
		 }
			 		 
	 }
	 
	 void devideOnValue(float value) {
		 for (int i =0; i < spectrum.length; i++) {
			 spectrum[i] /= value;
		 }
	 }
	 
	 void devideOnValue(int value) {
		 for (int i =0; i < spectrum.length; i++) {
			 spectrum[i] /= value;
		 }
	 }
	 
	 public int getItem(int index) {
		 return spectrum[index];
	 }
	 
	 public void setItem(int index, int value) {
		 spectrum[index] = value;
	 }
	 
	 int size() {
		 return spectrum.length;
	 }
	 
	public int[] toArray() {
		return spectrum;
	}
	
	public int getMaxSignal() {
		 int max = 0;
		 for (int i =100; i <spectrum.length-100; i++) {
			// Log.d("Spectrum",i+" "+spectrum[i]);
			 if (spectrum[i]>=max) max = spectrum[i]; 
		 }
		 return max;
	}
	
	void SaveToFIle(String FileName)  {
		try{
		FileOutputStream fos;
	    fos = new FileOutputStream(FileName);
		DataOutputStream dos = new DataOutputStream(fos);
        for (int i =0; i < header.length; i++) dos.writeByte(header[i]);
		for (int i =0; i < spectrum.length; i++)	dos.writeInt(spectrum[i]);
		dos.close();
	}
		 catch(FileNotFoundException fe)
		    {
		      System.out.println("FileNotFoundException : " + fe);
		    }
		    catch(IOException ioe)
		    {
		      System.out.println("IOException : " + ioe);
		    }
	}
	
	void LoadFromFile(String FileName) {
		  try {
			  FileInputStream fin = new FileInputStream(FileName);		          
			  DataInputStream din = new DataInputStream(fin);
              for (int i =0; i < header.length; i++)  header[i] = din.readByte();
			  for (int i =0; i < spectrum.length; i++)	spectrum[i]=din.readInt();
			  din.close();
			  }
		  catch(FileNotFoundException fe)
		    {
		      System.out.println("FileNotFoundException : " + fe);
		    }
		    catch(IOException ioe)
		    {
		      System.out.println("IOException : " + ioe);
		    }
		  
	}
	

}
