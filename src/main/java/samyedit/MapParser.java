/**
 * @author polskafan <polska at polskafan.de>
 * @author rayzyt <rayzyt at mail-buero.de>
 * @version 0.47c2
  
	Copyright 2009 by Timo Dobbrick
	adjustments for C-Series made by rayzyt
	For more information see http://www.polskafan.de/samsung
 
    This file is part of SamyGO ChanEdit.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package samyedit;

import gui.ErrorMessage;
import gui.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.TreeMap;

public class MapParser {

	
/**/
	public MapParser(String path, TreeMap<Integer, Channel> channelList) {

		/* select file type by file name instead of file size */

		if(path.endsWith("map-AirD") || path.endsWith("map-CableD")){
			Main.mapType = Channel.TYPE_CABLE; // save setting will be overwritten in parseAirCable below
			parseAirCable(path, channelList);
			Main.table.setRedraw(false);
			Main.deleteColumns();
			if(Main.mapType == Channel.TYPE_AIR) {
				Main.createColumnsAir();
			} else {
				Main.createColumnsCable();
			}
			Main.table.setRedraw(true);
		} else if(path.endsWith("map-SateD")){
			switch (Main.scmVersion) {
				case 'C': 
				case 'D': { 
					Main.mapType = Channel.TYPE_SAT;
					parseSat(path, channelList);
					Main.table.setRedraw(false);
					Main.deleteColumns();
					Main.createColumnsSat();
					Main.table.setRedraw(true);
					break;
				}
				default: {
					new ErrorMessage("Function not implemented for "+ Main.scmVersion +"-Series file: " + path);
				}
			}
		} else if(path.endsWith("clon.bin")){
			/* clone.bin */
			Main.mapType = Channel.TYPE_CLONE;
			parseClone(path, channelList);
			Main.table.setRedraw(false);
			Main.deleteColumns();
			Main.createColumnsClone();
			Main.table.setRedraw(true);
		} else {
			new ErrorMessage("Function not implemented for files with name: "+ path);
		}
		Main.statusLabel.setText("");
		return;
	}

	/** 
	 * reads the file and loads the data into the memory
	 * detects Cable Channel-list based on Frequency different zero
	 * 
	 * @param path
	 * @param channelList
	 */
	public static void parseAirCable(String path, TreeMap<Integer, Channel> channelList) {
		/* read rawData
		 * attention, byte data type is not unsigned, conversion must
		 * be applied to negative values */
		byte[] rawData;
		try {
			rawData = getFileContentsAsBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		int row = 0;
		AirCableChannel chan = null;
		do 
		{
			switch (Main.scmVersion) { 
			case 'C': { 
				chan = new AirCableChannelC();
				break;
			}
			case 'D': {
				chan = new AirCableChannelD(); 					
				break;
			}
			default: // Error
				new ErrorMessage("Function not implemented for "+ Main.scmVersion +"-Series TV");
				return;
			}	
			if(chan.parse(row++, rawData) <= 0) break;
			/* else store channel in TreeMap */
			channelList.put(chan.num, chan);
		} while (row >0);
	}
	
	private void parseSat(String path, TreeMap<Integer, Channel> channelList) {
		byte[] rawData;
		try {
			rawData = getFileContentsAsBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		int row = 0;
		SatChannel chan = null;
		do 
		{
			switch (Main.scmVersion) { 
			case 'C': { 
				chan = new SatChannelC();
				break;
			}
			case 'D': {
				chan = new SatChannelD(); 					
				break;
			}
			default: // Error
				new ErrorMessage("Function not implemented for "+ Main.scmVersion +"-Series TV");
				return;
			}	
			if(chan.parse(row++, rawData) <= 0) break;
			/* else store channel in TreeMap */
			channelList.put(chan.num, chan);
		} while (row >0);
	}
	
	private void parseClone(String path, TreeMap<Integer, Channel> channelList) {
		byte[] rawData;
		try {
			rawData = getFileContentsAsBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		Main.mapType = Channel.TYPE_CLONE;
		Main.rawData = rawData;
		/* only read as many lines, as tv says are valid */
		int size = convertEndianess(rawData[0x169f2], rawData[0x169f1]);

		for(int i = 0; i < size; i++) {
			int offset = 0x1342+i*81;
			/* empty line or inactive, skip to next */
			System.out.println(rawData[offset+73]);
			if((rawData[offset+73] & CloneChannel.FLAG_INACTIVE) == CloneChannel.FLAG_INACTIVE) continue;
			
			CloneChannel chan = new CloneChannel();
			for (int j = 0; j < 81; j++) chan.rawData[j] = rawData[offset+j];

			/* read channel name (max. 50 chars) 
			 * 
			 * only reads a byte, has to be rewritten if
			 * the channel name is actually unicode utf8
			 */
			for(int j = 0; j<50; j++) {
				int c = rawData[offset+j];
				if(c==0x00) break;
				if(c < 0) c+=256;
				chan.name += (char)c;
			}
			
			chan.num	= convertEndianess(rawData[offset+51], rawData[offset+50]);
			chan.vpid	= convertEndianess(rawData[offset+53], rawData[offset+52]);
			chan.mpid	= convertEndianess(rawData[offset+55], rawData[offset+54]);
			chan.freq	= rawData[offset+56];
			chan.fav	= rawData[offset+57];
			chan.nid	= convertEndianess(rawData[offset+60], rawData[offset+59]);
			chan.tsid	= convertEndianess(rawData[offset+62], rawData[offset+61]);
			chan.onid	= convertEndianess(rawData[offset+64], rawData[offset+63]);
			chan.sid	= convertEndianess(rawData[offset+66], rawData[offset+65]);
			chan.stype	= rawData[offset+71];
			chan.enc	= rawData[offset+73];
			
			/* store channel in TreeMap */
			channelList.put(chan.num, chan);
		}
	}
	
	public static void write(String file, TreeMap<Integer, Channel> channelList) {
		switch(Main.mapType) {
			case Channel.TYPE_AIR:
			case Channel.TYPE_CABLE:
				writeAirCable(file, channelList);
				break;
			case Channel.TYPE_SAT:
				writeSat(file, channelList);
				break;
			case Channel.TYPE_CLONE:
				writeClone(file, channelList);
				break;
		}
	}

	public static void writeClone(String file, TreeMap<Integer, Channel> channelList) {
		Iterator<Channel> it = channelList.values().iterator();
		File f = new File(file);
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			new ErrorMessage("Cannot write to file:\n"+e.getMessage());
			Main.statusLabel.setText("");
			return;
		}
		
		/* write bytes 0 - 0x1341 out, nothing has changed there */
		try {
			outStream.write(Main.rawData, 0, 0x1342);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		/* build byte array to write out, stop at 999 channels */
		int entries = 0;
		while(it.hasNext() && entries < 999) {
			CloneChannel chan = (CloneChannel)it.next();
			byte[] rawData = chan.rawData;
			
			char[] name = chan.name.toCharArray();
			int n = 0;
			for(; n<name.length && n<50;n++) {
				rawData[n] = (byte)name[n];
			}
			rawData[75] = (byte)n;
			for(; n<50;n++) {
				rawData[n] = (byte)0x00;
			}
			
			revertClone(rawData, 50, chan.num);
			revertClone(rawData, 52, chan.vpid);
			revertClone(rawData, 54, chan.mpid);
			rawData[56] = (byte) chan.freq;
			rawData[57] = chan.fav;
			revertClone(rawData, 59, chan.nid);
			revertClone(rawData, 61, chan.tsid);
			revertClone(rawData, 63, chan.onid);
			revertClone(rawData, 65, chan.sid);
			rawData[71] = chan.stype;
			rawData[73] = chan.enc;
			
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
		System.out.println(entries);
		revertClone(Main.rawData, 0x169ee, entries);
		revertClone(Main.rawData, 0x169f1, entries);
		
		/* fill with 0xFF until we reach 999 channels */
		byte[] rawData = new byte[81];
		for (int i = 0; i < 81; i++) rawData[i] = (byte)0xFF;
		while(entries < 999) {
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}

		/* write bytes 0x14F59 - 0x1C390 out, nothing has changed there */
		try {
			outStream.write(Main.rawData, 0x14F59, 0x74A7);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}	
		
		/* write the file out */
		Main.statusLabel.setText("Channel list written to file: "+file);
		return;
	}
	
	public static void writeAirCable(String file, TreeMap<Integer, Channel> channelList) {
		Iterator<Channel> it = channelList.values().iterator();
		File f = new File(file);
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			new ErrorMessage("Cannot write to file:\n"+e.getMessage());
			Main.statusLabel.setText("");
			return;
		}
		
		/* build byte array to write out */
		int entries = 0;
		int recordLen = 0;
		switch (Main.scmVersion) { 
		case 'C': { 
			recordLen = AirCableChannelC.lChan;
			break;
		}
		case 'D': {
			recordLen = AirCableChannelD.lChan;
			break;
		}
		default: // Error
			new ErrorMessage("Function not implemented for "+ Main.scmVersion +"-Series TV");
			return;
		}	
		AirCableChannel chan;
		while(it.hasNext()) {
			chan = (AirCableChannel)it.next();
			byte[] rawData = chan.writeData();
			
			try {
				outStream.write(rawData, 0, recordLen); // write data into the file
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
/*TODO test if fillup to 1000 records is still neded */		
		/* fill with null bytes until we reach a multiple of 1000 entries */
		byte[] rawData = new byte[recordLen];
		while(entries % 1000 != 0) {
 			try {
 				outStream.write(rawData, 0, recordLen); // write data into the file
 			} catch (IOException e) {
 				e.printStackTrace();
 				return;
 			}
 			entries++;
 		}
		Main.statusLabel.setText("Channel list written to file: "+file);
		return;
	}


	public static void writeSat(String file, TreeMap<Integer, Channel> channelList) {
		Iterator<Channel> it = channelList.values().iterator();
		File f = new File(file);
		OutputStream outStream;
		try {
			outStream = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			new ErrorMessage("Cannot write to file:\n"+e.getMessage());
			Main.statusLabel.setText("");
			return;
		}
		
		/* build byte array to write out */
		int entries = 0;
		int recordLen = 0;
		SatChannel chan;
		switch (Main.scmVersion) { 
		case 'C': { 
			recordLen = SatChannelC.lChan;
			break;
		}
		case 'D': {
			recordLen = SatChannelD.lChan;
			break;
		}
		default: // Error
			new ErrorMessage("Function not implemented for "+ Main.scmVersion +"-Series TV");
			return;
		}	
		while(it.hasNext()) {
			chan = (SatChannel)it.next();

			byte[] rawData = chan.writeData();
			
			try {
				outStream.write(rawData, 0, recordLen);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
		
		/* fill with null bytes until we reach a multiple of 1000 entries */
/* it is not necessary anymore to fill up to 1000 entries!
 *  
 *		while(entries % 1000 != 0) {
 *			chan = new SatChannelC();
 *			byte[] rawData = new byte[SatChannel.initLen];
 *			try {
 * //				outStream.write(rawData);
 *				outStream.write(rawData, 0, chan.recordLen);
 *			} catch (IOException e) {
 *				e.printStackTrace();
 *				return;
 *			}
 *			entries++;
 *		}
 *		
 */
		/* write the file out */
		Main.statusLabel.setText("Channel list written to file: "+file);
		return;
	}

	/* Endianess must be converted as Samsung and Java VM don't share the same
	 * endianess */
	private static int convertEndianess(byte b, byte c) {
		int lower = b;
		int upper = c;
		if(b<0) lower += 256;
		if(c<0) upper += 256;
		return lower+(upper<<8);
	}
	
	private static void revertClone(byte[] b, int offset, int data) {
		b[offset+1] = (byte) data;
		b[offset]   = (byte)(data>>8);
		return;
	}

	/* read bytes, so we are binary safe */
	public static byte[] getFileContentsAsBytes(String file) throws IOException {
		File f = new File(file);
		byte[] data = new byte[(int)f.length()];
		InputStream inStream = new FileInputStream(f);
		inStream.read(data);
		inStream.close();
		return data;
	}
}
