/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.2
  
	Copyright 2009 by Timo Dobbrick
	For more information see http://www.polskafan.de/samsung
 
    This file is part of SamyGO ChanEdit.

    Foobar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.

 */

package samyedit;

/* new Channel object, make it cloneable if we need to copy a channel */
public class Channel implements Cloneable {
	public static final byte QAM64 = 0x41;
	public static final byte QAM256 = 0x42;
	
	public static final byte ENC_FTA = 0x1f;
	public static final byte ENC_SCRAMBLED = 0x3f;
	
	public static final byte STYPE_TV = 0x01;
	public static final byte STYPE_RADIO = 0x02;
	public static final byte STYPE_DATA = 0x0C;
	
	public String name = "";
	public int num = -1;
	public int sid = -1;
	public int vpid = -1;
	public int mpid = -1;
	
	public int bouqet = -1;
	public int onid = -1;
	public int tsid = -1;
	public int freq = -1;
	public int symbr = -1;	
	public byte qam = -1;
	public byte stype = -1;
	public byte enc = -1;

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
			default:			ret += "unknown"; break;
		}
		
		ret += " encryption: ";
		switch(this.enc) {
			case ENC_FTA:		ret += "FTA"; break;
			case ENC_SCRAMBLED:	ret += "CSA"; break;
			default:			ret += "unknown"; break;
		}
		
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