/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.42
  
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

import gui.Main;

public class AirCableChannel extends Channel {
	
	public static final byte QAM64	= 0x1; //RB changed from 0x0 to 0x1
	public static final byte QAM256	= 0x2; //RB changed from 0x1 to 0x2
	public static final byte QAMauto= 0x8; //DVB-T
	
	public int recordLen				= 292; // C-Series: 292; D-Series: 320

	byte[]     rawData		= new byte[320]; //initialize with bigest value of various versions

	public int iChanNo  	=   0; //displayed Channel Number
	public int iChanVpid	=   2; //video Stream PID (or -1)
	public int iChanMpid	=   4; //Program Clock Recovery PID
	public int iChanSid		=   6; //SVB Service Identifier
	public int iChanStatus	=   8; 
	public int iChanQam		=  12; //modulation type (QAM64 | QAM256 | QAM_Auto)
	public int iChanSType	=  15; //Service Type (0x01 = TV; 0x02 = Radio; 0x0c = Data; 0x19 = HD) 
	public int iChanFav		=  16; //video Codec: 0=MPEG2, 1=MPEG4
	public int iChanEnc		=  24; //Scrambled service: 0=FTA, 1=CSA 
//	public int iChan???		=  25; //Frame Rate
	public int iChanSymbR	=  28; //Symbol Rate
	public int iChanLock	=  31; //locked 0=open, 1=locked
	public int iChanONid	=  32; //original DVB Network ID 
	public int iChanNid		=  34; //DVB Network ID displayed  ???
	public int iChanBouqet	=  36; //??? always 0
	public int iChanProvId	=  38; //Service Provider ID (or -1)
	public int iChanFreq	=  42; //cable channel
												//	     if (Channel <   7) { Band = 'S'; Channel +=   4; }
												//	else if (Channel <  15) { Band = 'K'; Channel -=   2; }
												//	else if (Channel <  46) { Band = 'S'; Channel -=   4; }
												//	else if (Channel <  95) { Band = 'K'; Channel -=  25; }
												//	else if (Channel < 194) { ??? }
												//	else if (Channel < 196) { Band = 'S'; Channel -= 192; };
	public int iChanLcn		=  44; //Logical Channel Number or -1 ???  
 	public int iChanTSid	=  48; //Transport Stream Identifier  
	public int iChanName	=  64; //big-endian Unicode characters 
	public int lChanName	= 100; 
	public int iChanSName	= 164; //big-endian Unicode characters
	public int lChanSName	=   9; 
	public int iChanVFmt	= 182; // video format: 5=1080i25, 7=720p50, 12=576i25, 13=576i25w, 20=custom
	public int iChanFav79	= 290; //bit-field: 0x1=Fav1, 0x2=Fav2, 0x4=Fav3, 0x8=Fav4
	public int iChanCRC	= 291; //simple char sum of all previous bytes

	public byte qam		= QAM64;
	
	public int nid		= -1;
	public int freq		= -1;
	public int symbr	= -1;

	public int lcn		= 0;

	/** 
	 * reads the record number "row" out of "inData"
	 * 
	 * @param row - number of the channel record
	 * @param inData - raw / binary date to parse
	 */
	public int parse(int row, byte[] inData) {
		/* read inData into the chan.rawData
		 * attention, byte data type is not unsigned, conversion must
		 * be applied to negative values */

		int size  = inData.length/recordLen;
		if (row > size) return 0;
		for(int i = row; i < size; i++) /* Search next valid line and return the values */ 
		{
			/* empty line or inactive channel, skip to next */
			int offset = i*recordLen;

			if(inData[offset] == (byte)00) continue; //RB || (inData[offset+8]&Channel.FLAG_ACTIVE)==0) continue;
			//RB looks like iChanStatus must be checked on both bytes!
			//				if((inData[offset] == (byte)00 && inData[offset+1] == (byte)00) || (inData[offset+iChanStatus] & Channel.FLAG_ACTIVE)==0) continue;

			byte chsum = 0;
			for (int j = 0; j < recordLen; j++) {
				rawData[j] = inData[offset+j];
				chsum += inData[offset+j];
			}
			num		= convertEndianess(inData[offset+iChanNo  ], inData[offset+iChanNo  + 1]); //displayed Channel Number
			vpid	= convertEndianess(inData[offset+iChanVpid], inData[offset+iChanVpid+ 1]); //video Stream PID (or -1)
			mpid	= convertEndianess(inData[offset+iChanMpid], inData[offset+iChanMpid+ 1]); //Program Clock Recovery PID
			sid		= convertEndianess(inData[offset+iChanSid ], inData[offset+iChanSid + 1]); //SVB Service Identifier
			fav		= inData[offset+iChanFav];
			status  = inData[offset+iChanStatus];
			stype	= inData[offset+iChanSType];
			qam		= inData[offset+iChanQam]; //modulation
			enc		= inData[offset+iChanEnc];
			freq	= convertEndianess(inData[offset+iChanFreq], inData[offset+iChanFreq  + 1]); //Frame Rate
			symbr	= convertEndianess(inData[offset+iChanSymbR], inData[offset+iChanSymbR+ 1]); //Symbol Rate
			lock	= inData[offset+iChanLock];										//RB locked 0|1
			onid	= convertEndianess(inData[offset+iChanONid], inData[offset+iChanONid  + 1]); //original DVB NID
			bouqet	= convertEndianess(inData[offset+iChanBouqet], inData[offset+iChanBouqet+ 1]);
			nid		= convertEndianess(inData[offset+iChanNid], inData[offset+iChanNid+ 1]); //DVB NID displayed
			lcn		= convertEndianess(inData[offset+iChanLcn], inData[offset+iChanLcn+ 1]); //Logical Channel Number or -1
			tsid	= convertEndianess(inData[offset+iChanTSid], inData[offset+iChanTSid+ 1]); //Transport Stream Identifier
			fav79	= inData[offset+iChanFav79]; //Test same as fav

			if(i == 0) {
				/* first line, try to detect channel type */
				if(symbr != 0) Main.mapType = Channel.TYPE_CABLE;
				else Main.mapType = Channel.TYPE_AIR;
			}

			/* read channel name (max. 100 chars) 
			 * 
			 * only reads a byte, has to be rewritten if
			 * the channel name is actually unicode utf8
			 */
			for(int j = 0; j<lChanName; j++) {
				int c = inData[offset+iChanName+1+j*2];
				if(c==0x00) break;
				if(c < 0) c+=256;
				name += (char)c;
			}
			return num;
		}
		return 0;
	}

	/** 
	 * provides the Channel data a binary data for saving into a MapChan file
	 */
	public byte [] writeData() 
	{
		revertEndianess(rawData, iChanNo,     num);
		revertEndianess(rawData, iChanVpid,   vpid);
		revertEndianess(rawData, iChanMpid,   mpid);
		revertEndianess(rawData, iChanSid,    sid);
		rawData[ iChanFav]    				= fav;
		rawData[ iChanQam]    				= qam;
		rawData[ iChanStatus] 				= status;
		rawData[ iChanSType]  				= stype;
		revertEndianess(rawData, iChanONid,   onid);
		revertEndianess(rawData, iChanNid,    nid);
		rawData[iChanEnc]     				= enc;
		revertEndianess(rawData, iChanFreq,   freq);
		revertEndianess(rawData, iChanLcn,    lcn);
		revertEndianess(rawData, iChanSymbR,  symbr);
		revertEndianess(rawData, iChanBouqet, bouqet);
		revertEndianess(rawData, iChanTSid,   tsid);
		
		char[] name = this.name.toCharArray();
		int n = 0;
		for(; n<name.length;n++) {
			rawData[iChanName+1+2*n] = (byte)name[n];
		}
		for(; n<lChanName;n++) {
			rawData[iChanName+1+2*n] = (byte)0x00;
		}
		
		rawData[iChanLock]  = lock;
		rawData[iChanFav79] = fav79;
		rawData[iChanCRC]   = 0; 			//CheckSum
		for(int i = 0; i<iChanCRC; i++) {	//calculate the CheckSum
			rawData[iChanCRC] += rawData[i];
		}
		return rawData;
	}
	
	/** converts the Channel/Frequency ID into a text String
	 * 
	 * @return String filled of Channel/Frequency ID in human readable form of {K|S|!}{number}
	 * @author rayzyt
	 */
	public String getFreq() 
	{
		
		if (this.freq <   7) return "S" + new Integer(this.freq +   4).toString();
		if (this.freq <  15) return "K" + new Integer(this.freq -   2).toString();
		if (this.freq <  46) return "S" + new Integer(this.freq -   4).toString();
		if (this.freq <  95) return "K" + new Integer(this.freq -  25).toString();
		if (this.freq < 194) return "!" + new Integer(this.freq      ).toString();
		if (this.freq < 196) return "S" + new Integer(this.freq - 192).toString();
		return "?" + new Integer(this.freq      ).toString();
	}
	/** converts a String back to the channel/Frequency ID
	 * 
	 * @param  String S - ASCII Text containing the channel/Frequency ID
	 * @return integer Frequency ID 
	*/
	public int setFreq(String s) 
	{
		String fStr = s.substring(1);
		int f = Integer.parseInt(fStr);
		if ( s.startsWith("S") && f <  11) return this.freq = f -    4;
		if ( s.startsWith("K") && f <  13) return this.freq = f +    2;
		if ( s.startsWith("S") && f <  42) return this.freq = f +    4;
		if ( s.startsWith("K") && f <  70) return this.freq = f +   25;
		if ( s.startsWith("!") && f < 194) return this.freq = f +    0;
		if ( s.startsWith("S") && f <   4) return this.freq = f +  192;
		return this.freq = Integer.parseInt(s);
	}
}
