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

package gui;

import java.util.NoSuchElementException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import samyedit.AirCableChannel;
import samyedit.Channel;
import samyedit.SatChannel;

public class Edit {
	public Channel channel;
	public Shell dialog;
	
	public Text[]   t = new Text[6];
	public Button[] r_stype = new Button[4];
	public Button[] r_misc  = new Button[3];
	public Button[] r_fav79 = new Button[4];
	public Text[]	t_add = new Text[4];
	public Button[]	r_add = new Button[5];
	
	public Edit(Channel channel) {
		this.channel = channel;
		createGUI();
	}

	private void createGUI() {
		dialog = new Shell(Main.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		if (channel.num == -1)
			dialog.setText("Add Channel");
		else
			dialog.setText("Edit Channel");
		
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 2;
		dialog.setLayout(layout);
 		
		GridData gridData;
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		
		Label l = new Label(dialog, SWT.CENTER);
		l.setText("Name:");
		t[0] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[0].setText(channel.name);
		t[0].setLayoutData(gridData);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("ONID:");
		t[1] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[1].setText(channel.onid+"");
		t[1].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("TSID:");
		t[2] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[2].setText(channel.tsid+"");
		t[2].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("SID:");
		t[3] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[3].setText(channel.sid+"");
		t[3].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("PID:");
		t[4] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[4].setText(channel.mpid+"");
		t[4].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("VPID:");
		t[5] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[5].setText(channel.vpid+"");
		t[5].setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		Group g = new Group(dialog, SWT.CENTER);
		g.setText("Service Type");
		g.setLayout(new RowLayout());
		r_stype[0] = new Button(g, SWT.RADIO);
		r_stype[0].setText("TV");
		if(channel.stype == Channel.STYPE_TV)    r_stype[0].setSelection(true);
		r_stype[1] = new Button(g, SWT.RADIO);
		r_stype[1].setText("Radio");
		if(channel.stype == Channel.STYPE_RADIO) r_stype[1].setSelection(true);
		r_stype[2] = new Button(g, SWT.RADIO);
		r_stype[2].setText("Data");
		if(channel.stype == Channel.STYPE_DATA)  r_stype[2].setSelection(true);
		r_stype[3] = new Button(g, SWT.RADIO);
		r_stype[3].setText("HD");
		if(channel.stype == Channel.STYPE_HD)  r_stype[3].setSelection(true);
		g.setLayoutData(gridData);
		g.pack();
		
		g = new Group(dialog, SWT.CENTER);
		g.setText("Misc");
		g.setLayout(new RowLayout());
		r_misc[0] = new Button(g, SWT.CHECK);
		r_misc[0].setText("Encrypted");
		if((channel.enc & Channel.FLAG_SCRAMBLED)!=0) r_misc[0].setSelection(true);
		r_misc[1] = new Button(g, SWT.CHECK);
		r_misc[1].setText("Locked");
		if((channel.lock & Channel.FLAG_LOCK)!=0) r_misc[1].setSelection(true);
		r_misc[2] = new Button(g, SWT.CHECK);
		r_misc[2].setText("Favourite");
		if((channel.fav & Channel.FLAG_FAV_1)!=0) r_misc[2].setSelection(true);

		g.setLayoutData(gridData);
		g.pack();
		
		g = new Group(dialog, SWT.CENTER);
		g.setText("Favourites (x79)");
		g.setLayout(new RowLayout());
		r_fav79[0] = new Button(g, SWT.CHECK);
		r_fav79[0].setText("Fav1");
		if((channel.fav79 & Channel.FLAG_FAV_1)!=0) r_fav79[0].setSelection(true);
		r_fav79[1] = new Button(g, SWT.CHECK);
		r_fav79[1].setText("Fav2");
		if((channel.fav79 & Channel.FLAG_FAV_2)!=0) r_fav79[1].setSelection(true);
		r_fav79[2] = new Button(g, SWT.CHECK);
		r_fav79[2].setText("Fav3");
		if((channel.fav79 & Channel.FLAG_FAV_3)!=0) r_fav79[2].setSelection(true);
		r_fav79[3] = new Button(g, SWT.CHECK);
		r_fav79[3].setText("Fav4");
		if((channel.fav79 & Channel.FLAG_FAV_4)!=0) r_fav79[3].setSelection(true);		
		g.setLayoutData(gridData);
		g.pack();
		
		switch(Main.mapType) {
			case Channel.TYPE_AIR:
				editAir();
				break;
			case Channel.TYPE_CABLE:
				editCable();
				break;
			case Channel.TYPE_SAT:
				editSat();
				break;
		}
		
		/* Add/Edit/Cancel Buttons */
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;

		Composite buttons = new Composite(dialog, SWT.RIGHT);
		GridLayout buttonLayout = new GridLayout(SWT.FILL, true);
		buttonLayout.numColumns = 2;
		buttons.setLayout(buttonLayout);
		buttons.setLayoutData(gridData);
		
		Button b = new Button(buttons, SWT.CENTER);
		if (channel.num == -1)
			b.setText("Add");
		else
			b.setText("Change");
		b.addSelectionListener(new doEdit(dialog, this));
		
		b = new Button(buttons, SWT.CENTER);
		b.setText("&Abort");
		b.addSelectionListener(new Exit(dialog));
		
		dialog.pack();
		
	    Monitor primary = Main.display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = dialog.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    dialog.setLocation(x, y);
	    
	    dialog.setSize(250, rect.height);
	    dialog.open();
	}
	
	void editCable() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		AirCableChannel c = (AirCableChannel) channel;
		Label l = new Label(dialog, SWT.CENTER);
		l.setText("NID:");
		t_add[0] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[0].setText(c.nid+"");
		t_add[0].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("Frequency:");
		t_add[1] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[1].setText(c.freq+"");
		t_add[1].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("SymbR (ksymb/s):");
		t_add[2] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[2].setText(c.symbr+"");
		t_add[2].setLayoutData(gridData);
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		Group g = new Group(dialog, SWT.CENTER);
		g.setText("QAM");
		g.setLayout(new RowLayout());
		r_add[0] = new Button(g, SWT.RADIO);
		r_add[0].setText("QAM64");
		if(c.qam == AirCableChannel.QAM64)	r_add[0].setSelection(true);
		r_add[1] = new Button(g, SWT.RADIO);
		r_add[1].setText("QAM256");
		if(c.qam == AirCableChannel.QAM256)	r_add[1].setSelection(true);
		g.setLayoutData(gridData);
		g.pack();
	}
	
	void editAir() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		AirCableChannel c = (AirCableChannel) channel;
		Label l = new Label(dialog, SWT.CENTER);
		l.setText("NID:");
		t_add[0] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[0].setText(c.nid+"");
		t_add[0].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("Frequency:");
		t_add[1] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[1].setText(c.freq+"");
		t_add[1].setLayoutData(gridData);
	}
	
	void editSat() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		SatChannel c = (SatChannel) channel;
		Label l = new Label(dialog, SWT.CENTER);
		l.setText("Sat:");
		t_add[0] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[0].setText(c.sat+"");
		t_add[0].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("TP:");
		t_add[1] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t_add[1].setText(c.tpid+"");
		t_add[1].setLayoutData(gridData);
	}
}

class doEdit implements SelectionListener {
	Shell	shell;
	Edit	edit;
	
	public doEdit(Shell dialog, Edit edit) {
		this.shell   = dialog;
		this.edit    = edit;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent arg0) {
		Channel channel = edit.channel;
		
		channel.name	= edit.t[0].getText();
		try {
			channel.onid	= new Integer(edit.t[1].getText());
			channel.tsid	= new Integer(edit.t[2].getText());
			channel.sid		= new Integer(edit.t[3].getText());
			channel.mpid	= new Integer(edit.t[4].getText());
			channel.vpid	= new Integer(edit.t[5].getText());
		} catch(NumberFormatException e) {
			new ErrorMessage("Cannot get number representation "+e.getMessage());
			return;
		}
		
		if(edit.r_stype[0].getSelection()) channel.stype = Channel.STYPE_TV;
		if(edit.r_stype[1].getSelection()) channel.stype = Channel.STYPE_RADIO;
		if(edit.r_stype[2].getSelection()) channel.stype = Channel.STYPE_DATA;
		if(edit.r_stype[3].getSelection()) channel.stype = Channel.STYPE_HD;
		
		if(edit.r_misc[0].getSelection())
			channel.enc |= Channel.FLAG_SCRAMBLED;
		else
			channel.enc &= ~Channel.FLAG_SCRAMBLED;
		
		if(edit.r_misc[1].getSelection())
			channel.lock |= Channel.FLAG_LOCK;
		else
			channel.lock &= ~Channel.FLAG_LOCK;
		
		if(edit.r_misc[2].getSelection())
			channel.fav |= Channel.FLAG_FAV_1;
		else
			channel.fav &= ~Channel.FLAG_FAV_1;
		
		if(edit.r_fav79[0].getSelection())
			channel.fav79 |= Channel.FLAG_FAV_1;
		else
			channel.fav79 &= ~Channel.FLAG_FAV_1;
		
		if(edit.r_fav79[1].getSelection())
			channel.fav79 |= Channel.FLAG_FAV_2;
		else
			channel.fav79 &= ~Channel.FLAG_FAV_2;
		
		if(edit.r_fav79[2].getSelection())
			channel.fav79 |= Channel.FLAG_FAV_3;
		else
			channel.fav79 &= ~Channel.FLAG_FAV_3;
		
		if(edit.r_fav79[3].getSelection())
			channel.fav79 |= Channel.FLAG_FAV_4;
		else
			channel.fav79 &= ~Channel.FLAG_FAV_4;
		
		switch(Main.mapType) {
			case Channel.TYPE_CABLE:
				AirCableChannel cable = (AirCableChannel) channel;
				try {
					cable.nid	= new Integer(edit.t_add[0].getText());
					cable.freq	= new Integer(edit.t_add[1].getText());
					cable.symbr	= new Integer(edit.t_add[2].getText());
				} catch(NumberFormatException e) {
					new ErrorMessage("Cannot get number representation "+e.getMessage());
					return;
				}
				if(edit.r_misc[0].getSelection())
					cable.qam = AirCableChannel.QAM64;
				if(edit.r_add[1].getSelection())
					cable.qam = AirCableChannel.QAM256;
				break;
			case Channel.TYPE_AIR:
				AirCableChannel air = (AirCableChannel) channel;
				try {
					air.nid		= new Integer(edit.t_add[0].getText());
					air.freq	= new Integer(edit.t_add[1].getText());
				} catch(NumberFormatException e) {
					new ErrorMessage("Cannot get number representation "+e.getMessage());
					return;
				}
				break;
			case Channel.TYPE_SAT:
				SatChannel sat = (SatChannel) channel;
				try {
					sat.sat		= new Integer(edit.t_add[0].getText());
					sat.tpid	= new Integer(edit.t_add[1].getText());
				} catch(NumberFormatException e) {
					new ErrorMessage("Cannot get number representation "+e.getMessage());
					return;
				}
				break;
		}
		
		if(channel.num == -1) {
			try {
				channel.num = Main.channelList.lastKey()+1;
			} catch(NoSuchElementException e) {
				channel.num = 1;
			}
			
			Main.channelList.put(channel.num, channel);
			Main.refresh();
			Main.table.setTopIndex(Main.channelList.size());
		} else {
			Main.refresh();
		}
		
		shell.dispose();
	}
}
