/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.40
  
	Copyright 2009 by Timo Dobbrick
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
	public MapParser(String path, TreeMap<Integer, Channel> channelList) {
		File file = new File(path);
		
		/* check for filetype */
		if((int)file.length() % 248000 == 0) {
			/* map-CableD, map-AirD */
			Main.mapType = Channel.TYPE_CABLE;
			parseAirCable(path, channelList);
			Main.table.setRedraw(false);
			Main.deleteColumns();
			if(Main.mapType == Channel.TYPE_AIR) {
				Main.createColumnsAir();
			} else {
				Main.createColumnsCable();
			}
			Main.table.setRedraw(true);
		} else if((int)file.length() % 144000 == 0) {
			/* map-SateD */
			Main.mapType = Channel.TYPE_SAT;
			parseSat(path, channelList);
			Main.table.setRedraw(false);
			Main.deleteColumns();
			Main.createColumnsSat();
			Main.table.setRedraw(true);
		} else {
			new ErrorMessage("File length does not match map-AirD, map-CableD or map-SateD.");
			Main.statusLabel.setText("");
			return;
		}
	}
	
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
		
		int size = rawData.length/248;
		for(int i = 0; i < size; i++) {
			/* empty line or inactive channel, skip to next */
			int offset = i*248;

			if(rawData[offset] == (byte)00 || (rawData[offset+8]&Channel.FLAG_ACTIVE)==0) continue;
			
			AirCableChannel chan = new AirCableChannel();
			for (int j = 0; j < 248; j++) chan.rawData[j] = rawData[offset+j];
			chan.num	= convertEndianess(rawData[offset]   , rawData[offset+ 1]);
			chan.vpid	= convertEndianess(rawData[offset+ 2], rawData[offset+ 3]);
			chan.mpid	= convertEndianess(rawData[offset+ 4], rawData[offset+ 5]);
			chan.fav	= rawData[offset+ 6];
			chan.qam	= rawData[offset+ 7];
			chan.status = rawData[offset+ 8];
			chan.stype	= rawData[offset+ 9];
			chan.sid	= convertEndianess(rawData[offset+10], rawData[offset+11]);
			chan.onid	= convertEndianess(rawData[offset+12], rawData[offset+13]);
			chan.nid	= convertEndianess(rawData[offset+14], rawData[offset+15]);
			chan.enc	= rawData[offset+23];
			chan.freq	= convertEndianess(rawData[offset+26], rawData[offset+27]);
			chan.lcn	= convertEndianess(rawData[offset+28], rawData[offset+29]);
			chan.symbr	= convertEndianess(rawData[offset+32], rawData[offset+33]);
			chan.bouqet	= convertEndianess(rawData[offset+34], rawData[offset+35]);
			chan.tsid	= convertEndianess(rawData[offset+36], rawData[offset+37]);
			chan.lock	= rawData[offset+245];
			chan.fav79	= rawData[offset+246];

			if(i == 0) {
				/* first line, try to detect channel type */
				if(chan.symbr != 0) Main.mapType = Channel.TYPE_CABLE;
				else Main.mapType = Channel.TYPE_AIR;
			}
			
			/* read channel name (max. 100 chars) 
			 * 
			 * only reads a byte, has to be rewritten if
			 * the channel name is actually unicode utf8
			 */
			for(int j = 0; j<100; j++) {
				int c = (int)rawData[offset+45+j*2];
				if(c==0x00) break;
				if(c < 0) c+=256;
				chan.name += (char)c;
			}
			
			/* store channel in TreeMap */
			channelList.put(chan.num, chan);
		}
	}

	private void parseSat(String path, TreeMap<Integer, Channel> channelList) {
		byte[] rawData;
		try {
			rawData = getFileContentsAsBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		Main.mapType = Channel.TYPE_SAT;
		
		int size = rawData.length/144;
		for(int i = 0; i < size; i++) {
			/* empty line, skip to next */
			if(rawData[i*144] == (byte)00) continue;
			
			int offset = i*144;
			SatChannel chan = new SatChannel();
			for (int j = 0; j < 144; j++) chan.rawData[j] = rawData[offset+j];
			
			chan.num	= convertEndianess(rawData[offset]   , rawData[offset+ 1]);
			chan.vpid	= convertEndianess(rawData[offset+ 2], rawData[offset+ 3]);
			chan.mpid	= convertEndianess(rawData[offset+ 4], rawData[offset+ 5]);
			chan.vtype  = rawData[offset+ 6];
			chan.stype	= rawData[offset+ 14];
			chan.sid	= convertEndianess(rawData[offset+16], rawData[offset+17]);
			chan.tpid	= convertEndianess(rawData[offset+18], rawData[offset+19]);
			chan.sat	= convertEndianess(rawData[offset+20], rawData[offset+21]);
			chan.tsid	= convertEndianess(rawData[offset+24], rawData[offset+25]);
			chan.onid	= convertEndianess(rawData[offset+28], rawData[offset+29]);
			chan.bouqet	= convertEndianess(rawData[offset+138], rawData[offset+139]);
			chan.lock	= rawData[offset+141];
			chan.fav79	= rawData[offset+142];

			/* read channel name (max. 50 chars) 
			 * 
			 * only reads a byte, has to be rewritten if
			 * the channel name is actually unicode utf8
			 */
			for(int j = 0; j<50; j++) {
				int c = (int)rawData[offset+37+j*2];
				if(c==0x00) break;
				if(c < 0) c+=256;
				chan.name += (char)c;
			}
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
		}
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
		while(it.hasNext()) {
			AirCableChannel chan = (AirCableChannel)it.next();
			byte[] rawData = chan.rawData;
			revertEndianess(rawData,  0, chan.num);
			revertEndianess(rawData,  2, chan.vpid);
			revertEndianess(rawData,  4, chan.mpid);
			rawData[ 6] = chan.fav;
			rawData[ 7] = chan.qam;
			rawData[ 8] = chan.status;
			rawData[ 9] = chan.stype;
			revertEndianess(rawData, 10, chan.sid);
			revertEndianess(rawData, 12, chan.onid);
			revertEndianess(rawData, 14, chan.nid);
			rawData[23] = chan.enc;
			revertEndianess(rawData, 26, chan.freq);
			revertEndianess(rawData, 32, chan.symbr);
			revertEndianess(rawData, 34, chan.bouqet);
			revertEndianess(rawData, 36, chan.tsid);
			
			char[] name = chan.name.toCharArray();
			int n = 0;
			for(; n<name.length;n++) {
				rawData[45+2*n] = (byte)name[n];
			}
			for(; n<100;n++) {
				rawData[45+2*n] = (byte)0x00;
			}
			
			rawData[245] = chan.lock;
			rawData[246] = chan.fav79;
			rawData[247] = 0;
			for(int i = 0; i<247; i++) {
				rawData[247] += rawData[i];
			}
			
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
		
		/* fill with null bytes until we reach a multiple of 1000 channels */
		while(entries % 1000 != 0) {
			byte[] rawData = new byte[248];
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
		
		/* write the file out */
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
		while(it.hasNext()) {
			SatChannel chan = (SatChannel)it.next();
			/* use raw pattern of original file as example */
			byte[] rawData = chan.rawData;
	
			revertEndianess(rawData,  0, chan.num);
			revertEndianess(rawData,  2, chan.vpid);
			revertEndianess(rawData,  4, chan.mpid);
			rawData[ 6] = chan.vtype;
			rawData[14] = chan.stype;
			revertEndianess(rawData, 16, chan.sid);
			revertEndianess(rawData, 18, chan.tpid);
			revertEndianess(rawData, 20, chan.sat);
			revertEndianess(rawData, 24, chan.tsid);
			revertEndianess(rawData, 28, chan.onid);
			
			char[] name = chan.name.toCharArray();
			int n = 0;
			for(; n<name.length;n++) {
				rawData[37+2*n] = (byte)name[n];
			}
			for(; n<50;n++) {
				rawData[37+2*n] = 0x00;
			}
			
			
			revertEndianess(rawData, 138, chan.bouqet);
			rawData[141] = chan.lock;
			rawData[142] = chan.fav79;
			
			rawData[143] = 0;
			/* calculate checksum */
			for(int i = 0; i<143; i++) {
				rawData[143] += rawData[i];
			}
			
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
		
		/* fill with null bytes until we reach a multiple of 1000 entries */
		while(entries % 1000 != 0) {
			byte[] rawData = new byte[144];
			try {
				outStream.write(rawData);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			entries++;
		}
		
		/* write the file out */
		Main.statusLabel.setText("Channel list written to file: "+file);
		return;
	}

	/* Endianess must be converted as Samsung and Java VM don't share the same
	 * endianess */
	private static int convertEndianess(byte b, byte c) {
		int lower = (int) b;
		int upper = (int) c;
		if(b<0) lower += 256;
		if(c<0) upper += 256;
		return lower+(upper<<8);
	}
	
	private static void revertEndianess(byte[] b, int offset, int data) {
		b[offset] = (byte)data;
		b[offset+1]   = (byte)(data>>8);
		return;
	}

	/* read bytes, so we are binary safe */
	private static byte[] getFileContentsAsBytes(String file) throws IOException {
		File f = new File(file);
		byte[] data = new byte[(int)f.length()];
		InputStream inStream = new FileInputStream(f);
		inStream.read(data);
		inStream.close();
		return data;
	}
}
