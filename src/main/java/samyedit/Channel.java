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

/* new Channel object, make it cloneable if we need to copy a channel */
public class Channel implements Cloneable {
	public static final byte STYPE_TV		= 0x01;
	public static final byte STYPE_RADIO	= 0x02;
	public static final byte STYPE_DATA		= 0x0C;
	public static final byte STYPE_HD		= 0x19;
	
	public static final byte FLAG_SCRAMBLED = 0x20;
	
	public static final byte QAM64	= 0x41;
	public static final byte QAM256	= 0x42;
	
	public static final byte FAV_N = 0x46;
	public static final byte FAV_Y = 0x47;

	public static final byte LOCK_N = 0x00;
	public static final byte LOCK_Y = 0x01;
	
	public String name = "";
	public int num	= -1;
	public int sid	= -1;
	public int vpid	= -1;
	public int mpid	= -1;
	
	public int bouqet	= -1;
	public int nid		= -1;
	public int onid		= -1;
	public int tsid		= -1;
	public int freq		= -1;
	public int symbr	= -1;
	
	public byte qam		= QAM64;
	public byte stype	= STYPE_TV;
	public byte enc		= 0x00;
	public byte fav		= FAV_N;
	public byte lock	= 0; 

	/* make the channel printable */
	public String toString() {
		String ret = "cnum: "+this.num + " name: "+this.name + " sid: "+this.sid
		+ " mpid: "+this.mpid + " vpid: "+this.vpid	+ " bouqet: "+this.bouqet
		+ " onid: "+this.onid + " tsid: "+this.tsid	+ " freq: "+this.freq
		+ " sr: "+this.symbr+"ksym/s";
		
		ret += " qam: ";
		switch(this.qam) {
			case QAM64:		ret += "QAM64"; break;
			case QAM256:	ret += "QAM256"; break;
			default:		ret += "unknown("+this.qam+")"; break;
		}
		
		ret += " type: ";
		switch(this.stype) {
			case STYPE_TV:		ret += "TV"; break;
			case STYPE_RADIO:	ret += "RADIO"; break;
			case STYPE_DATA:	ret += "DATA"; break;
			case STYPE_HD:		ret += "HD"; break;
			default:			ret += "unknown"; break;
		}
		
		ret += " encryption: ";
		if((this.enc & FLAG_SCRAMBLED)!=0)
			ret += "CSA";
		else
			ret += "FTA";
	
		
		return ret;
	}
	
	/* clone function, if a channel needs to be copied
	 * just invoke clone() from Object class */
    public Channel clone() {
    	Channel theClone = null;
        try {
          theClone = (Channel) super.clone();
        }
        catch(CloneNotSupportedException e) {
        }
        return theClone;
	}
}