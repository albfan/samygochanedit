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

import java.util.Iterator;
import java.util.TreeMap;

public class SkyFeedChannels {
	public static void add(TreeMap<Integer, Channel> channelList) {
		/* get reference channels, so we now their parameters to copy them */
		Channel[] ref = new Channel[5];
		Iterator <Channel> it = channelList.values().iterator();
		while(it.hasNext()) {
			Channel curr = it.next();
			/* find by tsid and onid, perhaps add additional checks for sat compatibilty */
			if(curr.onid != 133) continue;
			switch(curr.tsid) {
				case 0x01:
					ref[0] = curr;
					break;
				case 0x02:
					ref[1] = curr;
					break;
				case 0x03:
					ref[2] = curr;
					break;
				case 0x11:
					ref[3] = curr;
					break;
				case 0x04:
					ref[4] = curr;
					break;
			}
		}
		
		if(ref[0] == null || ref[1] == null || ref[2] == null || ref[3] == null || ref[4] == null) {
			new ErrorMessage("Could not find enough sky reference channels for frequency extraction.\nPlease load a channel list containing sky channels,\nbefore using this function.");
			return;
		}
		
		addChannel(channelList, ref[4], 9700, "Sky Select",      18);
		addChannel(channelList, ref[1], 9701, "Sky Select 1",   251);
		addChannel(channelList, ref[1], 9702, "Sky Select 2",   261);
		addChannel(channelList, ref[2], 9703, "Sky Select 3",   271);
		addChannel(channelList, ref[2], 9704, "Sky Select 4",   281);
		addChannel(channelList, ref[2], 9705, "Sky Select 5",   291);
		addChannel(channelList, ref[2], 9706, "Sky Select 6",   301);
		addChannel(channelList, ref[3], 9707, "Sky Select 7",   311);
		addChannel(channelList, ref[3], 9708, "Sky Select 8",   321);
		addChannel(channelList, ref[3], 9709, "Sky Select 9",   331);
		addChannel(channelList, ref[1], 9710, "Sky Event A",    254);
		addChannel(channelList, ref[1], 9711, "Sky Event B",    264);		
		
		addChannel(channelList, ref[2], 9800, "Sky Sport Info",  17);
		addChannel(channelList, ref[4], 9801, "Sky Sport 1",    221);
		addChannel(channelList, ref[4], 9802, "Sky Sport 2",    222);
		addChannel(channelList, ref[1], 9803, "Sky Sport 3",    253);
		addChannel(channelList, ref[3], 9804, "Sky Sport 4",    333);
		addChannel(channelList, ref[3], 9805, "Sky Sport 5",    323);
		addChannel(channelList, ref[3], 9806, "Sky Sport 6",    313);
		addChannel(channelList, ref[2], 9807, "Sky Sport 7",    303);
		addChannel(channelList, ref[2], 9808, "Sky Sport 8",    293);
		addChannel(channelList, ref[2], 9809, "Sky Sport 9",    283);
		addChannel(channelList, ref[1], 9810, "Sky Sport 10",   263);
		addChannel(channelList, ref[2], 9811, "Sky Sport 11",   273);
		addChannel(channelList, ref[0], 9812, "Sky Sport 12",   363);
		addChannel(channelList, ref[0], 9813, "Sky Sport 13",   373);
		
		addChannel(channelList, ref[4], 9900, "Sky Bundesliga", 223);
		addChannel(channelList, ref[1], 9901, "Sky Bundesl. 1", 262);
		addChannel(channelList, ref[2], 9902, "Sky Bundesl. 2", 272);
		addChannel(channelList, ref[2], 9903, "Sky Bundesl. 3", 282);
		addChannel(channelList, ref[2], 9904, "Sky Bundesl. 4", 292);
		addChannel(channelList, ref[2], 9905, "Sky Bundesl. 5", 302);
		addChannel(channelList, ref[3], 9906, "Sky Bundesl. 6", 312);
		addChannel(channelList, ref[3], 9907, "Sky Bundesl. 7", 322);
		addChannel(channelList, ref[3], 9908, "Sky Bundesl. 8", 332);
		addChannel(channelList, ref[0], 9909, "Sky Bundesl. 9", 342);
		addChannel(channelList, ref[0], 9910, "Sky Bundesl. 10",352);
		addChannel(channelList, ref[1], 9911, "Sky Bundesl. 11",252);
		
		addChannel(channelList, ref[4], 9600, "Blue Movie",   513);
		addChannel(channelList, ref[0], 9601, "Blue Movie 1", 345);
		addChannel(channelList, ref[0], 9602, "Blue Movie 2", 355);
		addChannel(channelList, ref[0], 9603, "Blue Movie 3", 365);
	}

	private static void addChannel(TreeMap<Integer, Channel> channelList, Channel ref, int num, String name, int sid) {
		Channel c = ref.clone();
		c.num    = num;
		c.name   = name;
		c.sid    = sid;
		c.mpid   = 0xFFFF;
		c.vpid   = 0xFFFF;
		c.stype  = Channel.STYPE_TV;
		c.enc |= Channel.FLAG_SCRAMBLED;
		channelList.put(c.num, c);
	}
}
