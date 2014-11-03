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

public class SatChannel extends Channel {
	public static final int initLen	= 200; // max bytes we need for any sub class!
/*	public */
	byte[]     rawData		= new byte[initLen];
	public int tpid			= -1;
	public int sat			= -1;
	public int recordLen	=  0; // C-Series: 144; D-Series: 172
	public int lChanName	= 50; // max length of the name string in unicode (2 byte per character) 

	public int iChanNo  	=   0; //displayed Channel Number
	public int iChanVpid	=   2; //video Stream PID (or -1)
	public int iChanMpid	=   4; //Program Clock Recovery PID
	public int iChanVType	=   6; //Virtual Service Type  
	public int iChanSType	=  14; //Service Type (0x01 = TV; 0x02 = Radio; 0x0c = Data; 0x19 = HD) 
	public int iChanSid		=  16; //SVB Service Identifier
	public int iChanTpid	=  18;
	public int iChanSat		=  20;
	public int iChanTSid	=  24; //Transport Stream Identifier  
	public int iChanONid	=  28; //original DVB Network ID 
	public int iChanName	=  36; //big-endian unicode characters 
	public int iChanBouqet	= 138; //??? always 0
	public int iChanLock	= 141; //locked 0=open, 1=locked
	public int iChanFav79	= 142; //bit-field: 0x1=Fav1, 0x2=Fav2, 0x4=Fav3, 0x8=Fav4
	public int iChanCRC		= -1; //simple char sum of all previous bytes
	

	public int parse(int row, byte[] inData) {
		/* read rawData
		 * attention, byte data type is not unsigned, conversion must
		 * be applied to negative values */
				
		int size  = inData.length/recordLen;
		if (row > size) return 0;
		for(int i = row; i < size; i++) /* Search next valid line and return the values */ 
		{
			/* empty line, skip to next // RB: skip only if both bytes of chan.num == 0 */
			int offset = i*recordLen;
			if((inData[offset]|inData[offset+1])== (byte)00) continue;
			
			byte chsum = 0;
			for (int j = 0; j < recordLen; j++) 
			{ 
				rawData[j] = inData[offset+j];
				chsum     += inData[offset+j];
			}
			num		= convertEndianess(rawData[iChanNo   ], rawData[iChanNo   + 1]); //displayed Channel Number
			vpid	= convertEndianess(rawData[iChanVpid ], rawData[iChanVpid + 1]); //video Stream PID (or -1)
			mpid	= convertEndianess(rawData[iChanMpid ], rawData[iChanMpid + 1]); //Program Clock Recovery PID
			vtype	= rawData[iChanVType];
			stype	= rawData[iChanSType];
			sid		= convertEndianess(rawData[iChanSid  ], rawData[iChanSid  + 1]); //SVB Service Identifier
			tpid	= convertEndianess(rawData[iChanTpid ], rawData[iChanTpid + 1]); //TPID
			sat		= convertEndianess(rawData[iChanSat  ], rawData[iChanSat  + 1]); //SAT ID
			tsid	= convertEndianess(rawData[iChanTSid  ], rawData[iChanTSid  + 1]); //Transport Stream Identifier
			onid	= convertEndianess(rawData[iChanONid  ], rawData[iChanONid  + 1]); //original DVB NID
			bouqet	= convertEndianess(rawData[iChanBouqet], rawData[iChanBouqet+ 1]);
			lock	= rawData[iChanLock];										//RB locked 0|1
			fav79	= rawData[iChanFav79]; //Test same as chan.fav

//			fav		= rawData[iChanFav];
//			status	= rawData[iChanStatus];
//			qam		= rawData[iChanQam]; //modulation
//			enc		= rawData[iChanEnc];
//			freq	= convertEndianess(rawData[iChanFreq  ], rawData[iChanFreq  + 1]); //Frame Rate
//			symbr	= convertEndianess(rawData[iChanSymbR ], rawData[iChanSymbR + 1]); //Symbol Rate
//			nid		= convertEndianess(rawData[iChanNid   ], rawData[iChanNid   + 1]); //DVB NID displayed
//			lcn		= convertEndianess(rawData[iChanLcn   ], rawData[iChanLcn   + 1]); //Logical Channel Number or -1

			/* read channel name 
			 * 
			 * only reads a byte, has to be rewritten if
			 * the channel name is actually unicode utf8
			 */
			for(int j = 0; j<lChanName; j++) {
				int c = rawData[iChanName+1+j*2];
				if(c==0x00) break; // 0x00 is the end delimiter
				if(c < 0) c+=256;
				name += (char)c;
			}
			return num;
		}
		return 0;
	}
	
	public byte [] writeData() 
	{
			revertEndianess(rawData, iChanNo  , num );
			revertEndianess(rawData, iChanVpid, vpid);
			revertEndianess(rawData, iChanMpid, mpid);
			rawData[iChanVType] 			  = vtype;
			rawData[iChanSType] 			  = stype;
			revertEndianess(rawData, iChanSid , sid );
			revertEndianess(rawData, iChanTpid, tpid);
			revertEndianess(rawData, iChanSat , sat );
			revertEndianess(rawData, iChanTSid, tsid);
			revertEndianess(rawData, iChanONid, onid);
			
			char[] name = this.name.toCharArray();
			int n = 0;
			for(; n<name.length;n++) {
				rawData[iChanName+1+2*n] = (byte)name[n];
			}
			for(; n<lChanName;n++) {
				rawData[iChanName+1+2*n] = 0x00;
			}
			
			revertEndianess(rawData, iChanBouqet, bouqet);
			rawData[iChanLock ] 				= lock;
			rawData[iChanFav79] 				= fav79;
			
			rawData[iChanCRC] = 0;
			/* calculate checksum */
			for(int i = 0; i<iChanCRC; i++) {
				rawData[iChanCRC] += rawData[i];
			}
			return rawData;
		}
}	
