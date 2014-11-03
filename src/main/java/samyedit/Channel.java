/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.42c
  
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

/** new Channel class, <p>made it cloneable if we need to copy a channel</p> */
public class Channel implements Cloneable {
//	public static int lChan = 292; // right for C-Series only!)
	
	public static final byte TYPE_CABLE	= (byte)0x01;
	public static final byte TYPE_AIR	= (byte)0x02;
	public static final byte TYPE_SAT	= (byte)0x04;
//	public static final byte TYPE_SAT_D	= (byte)0x05;
	public static final byte TYPE_CLONE	= (byte)0x08;
	
	public static final byte STYPE_TV		= (byte)0x01;
	public static final byte STYPE_RADIO	= (byte)0x02;
	public static final byte STYPE_DATA		= (byte)0x0C;
	public static final byte STYPE_HD		= (byte)0x19;
	
	public static final byte VTYPE_MPEG2	= (byte)0x00;
	public static final byte VTYPE_MPEG4	= (byte)0x01;
	
	public static final byte FLAG_ACTIVE	= (byte)0x80;
	public static final byte FLAG_SCRAMBLED = (byte)0x01; //RB changed from 0x20 to 0x01
	public static final byte FLAG_LOCK		= (byte)0x01;
	
	public static final byte FLAG_FAV_1		= (byte)0x01;
	public static final byte FLAG_FAV_2		= (byte)0x02;
	public static final byte FLAG_FAV_3		= (byte)0x04;
	public static final byte FLAG_FAV_4		= (byte)0x08;
	
	public String name = "";
	public int num	= -1;
	public int sid	= -1;
	public int vpid	= -1;
	public int mpid	= -1;
		
	public int bouqet	= -1;
	public int onid		= -1;
	public int tsid		= -1;
	
	public byte stype	= STYPE_TV;
	public byte vtype	= VTYPE_MPEG2;
	public byte status	= (byte)0xE8;
	public byte enc		= (byte)0x00;
	public byte fav		= (byte)0x00;
	public byte fav79	= (byte)0x00;
	public byte lock	= (byte)0x00; 

	/** prints the content of the channel into a string */
	@Override
	public String toString() {
		String ret = "cnum: "+this.num + " name: "+this.name + " sid: "+this.sid
		+ " mpid: "+this.mpid + " vpid: "+this.vpid	+ " bouqet: "+this.bouqet
		+ " onid: "+this.onid + " tsid: "+this.tsid;
		
		ret += " type: ";
		switch(this.stype) {
			case STYPE_TV:		ret += "TV-SD"; break;
			case STYPE_RADIO:	ret += "RADIO"; break;
			case STYPE_DATA:	ret += "DATA"; break;
			case STYPE_HD:		ret += "TV-HD"; break;
			default:			ret += "unknown"; break;
		}
		
		ret += " encryption: ";
		if((this.enc & FLAG_SCRAMBLED)!=0)
			ret += "CSA"; //Content Secured on Air
		else
			ret += "FTA"; //Free To Air
		return ret;
	}
	
	/** clone function, if a channel needs to be copied
	 * just invoke clone() from Object class */
    @Override
	public Channel clone() {
    	Channel theClone = null;
        try {
          theClone = (Channel) super.clone();
        }
        catch(CloneNotSupportedException e) {
        }
        return theClone;
	}

	/* Endianess must be converted as Samsung and Java VM don't share the same
	 * endianess */
	
	protected static int convertEndianess(byte b, byte c) {
		int lower = b;
		int upper = c;
		if(b<0) lower += 256;
		if(c<0) upper += 256;
		return lower+(upper<<8);
	}
	
	protected static void revertEndianess(byte[] b, int offset, int data) {
		b[offset] = (byte) (data & 0x00ff); 
		b[offset+1]   = (byte)(data>>8);
		return;
	}
	
	/* read bytes, so we are binary safe */

}