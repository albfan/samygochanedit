package samyedit;

public class AirCableChannelD extends AirCableChannel {
	public static final int lChan	= 320; // RB new length 292/320 byte in C/D-Series
	{
		recordLen	= lChan;
		iChanCRC	= recordLen - 1;
 	}
}