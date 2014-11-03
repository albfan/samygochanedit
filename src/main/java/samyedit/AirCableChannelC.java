/**
 * @author rayzyt
 * @version 0.49cd
 */

package samyedit;

public class AirCableChannelC extends AirCableChannel {
	public static final int lChan	= 292; // RB new length 292/320 byte in C/D-Series
	{
		recordLen	= lChan;
		iChanCRC	= recordLen - 1;
 	}
}

