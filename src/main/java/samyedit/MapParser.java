/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.31
  
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
		
		/* map-SateD might be bigger, perhaps file should be checked using
		 * line cheksum
		 */
		
		if((int)file.length() != 248000) {
			new ErrorMessage("File length does not match 248.000 bytes!");
			Main.statusLabel.setText("");
			return;
		}
		
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
		
		for(int i = 0; i < 1000; i++) {
			/* empty line, skip to next */
			if(rawData[i*248] == (byte)00) continue;
			
			int offset = i*248;
			Channel chan = new Channel();
			
			chan.num	= convertEndianess(rawData[offset]   , rawData[offset+ 1]);
			chan.vpid	= convertEndianess(rawData[offset+ 2], rawData[offset+ 3]);
			chan.mpid	= convertEndianess(rawData[offset+ 4], rawData[offset+ 5]);
			chan.fav	= rawData[offset+ 6];
			chan.qam	= rawData[offset+ 7];
			chan.stype	= rawData[offset+ 9];
			chan.sid	= convertEndianess(rawData[offset+10], rawData[offset+11]);
			chan.onid	= convertEndianess(rawData[offset+12], rawData[offset+13]);
			chan.nid	= convertEndianess(rawData[offset+14], rawData[offset+15]);
			chan.enc	= rawData[offset+23];
			chan.freq	= convertEndianess(rawData[offset+26], rawData[offset+27]);
			chan.symbr	= convertEndianess(rawData[offset+32], rawData[offset+33]);
			chan.bouqet	= convertEndianess(rawData[offset+34], rawData[offset+35]);
			chan.tsid	= convertEndianess(rawData[offset+36], rawData[offset+37]);
			chan.lock	= rawData[offset+245];
			
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
	
	public static void write(String file, TreeMap<Integer, Channel> channelList) {
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
			byte[] rawData = new byte[248];
			Channel chan = it.next();
			revertEndianess(rawData,  0, chan.num);
			revertEndianess(rawData,  2, chan.vpid);
			revertEndianess(rawData,  4, chan.mpid);
			rawData[ 6] = chan.fav;
			rawData[ 7] = chan.qam;
			rawData[ 8] = (byte)0xE8;
			rawData[ 9] = chan.stype;
			revertEndianess(rawData, 10, chan.sid);
			revertEndianess(rawData, 12, chan.onid);
			revertEndianess(rawData, 14, chan.nid);
			rawData[20] = (byte)0xFF;
			rawData[21] = (byte)0xFF;
			rawData[22] = (byte)0xFF;
			rawData[23] = chan.enc;
			rawData[24] = (byte)0xFF;
			rawData[25] = (byte)0x01;
			revertEndianess(rawData, 26, chan.freq);
			rawData[28] = (byte)0xFF;
			rawData[29] = (byte)0xFF;
			rawData[30] = (byte)0xFF;
			rawData[31] = (byte)0xFF;
			revertEndianess(rawData, 32, chan.symbr);
			revertEndianess(rawData, 34, chan.bouqet);
			revertEndianess(rawData, 36, chan.tsid);
			
			char[] name = chan.name.toCharArray();
			for(int i = 0; i<name.length;i++) {
				rawData[45+2*i] = (byte)name[i];
			}
			
			rawData[245] = chan.lock;
			
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
