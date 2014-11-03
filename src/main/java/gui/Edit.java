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

import samyedit.Channel;

public class Edit {
	public Channel channel;
	
	public Text[]   t = new Text[9];
	public Button[] r_stype = new Button[4];
	public Button[] r_qam   = new Button[2];
	public Button[] r_misc  = new Button[3];
	
	public Edit(Channel channel) {
		this.channel = channel;
		createGUI();
	}

	private void createGUI() {
		Shell dialog = new Shell(Main.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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
		l.setText("Frequency:");
		t[1] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[1].setText(channel.freq+"");
		t[1].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("Symbolrate (ksymb/s):");
		t[2] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[2].setText(channel.symbr+"");
		t[2].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("NID:");
		t[8] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[8].setText(channel.nid+"");
		t[8].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("ONID:");
		t[3] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[3].setText(channel.onid+"");
		t[3].setLayoutData(gridData);
		
		l = new Label(dialog, SWT.CENTER);
		l.setText("TSID:");
		t[4] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[4].setText(channel.tsid+"");
		t[4].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("SID:");
		t[5] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[5].setText(channel.sid+"");
		t[5].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("PID:");
		t[6] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[6].setText(channel.mpid+"");
		t[6].setLayoutData(gridData);

		l = new Label(dialog, SWT.CENTER);
		l.setText("VPID:");
		t[7] = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t[7].setText(channel.vpid+"");
		t[7].setLayoutData(gridData);

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
		g.setText("Modulation");
		g.setLayout(new RowLayout());
		r_qam[0] = new Button(g, SWT.RADIO);
		r_qam[0].setText("QAM64");
		if(channel.qam == Channel.QAM64)  r_qam[0].setSelection(true);
		r_qam[1] = new Button(g, SWT.RADIO);
		r_qam[1].setText("QAM256");
		if(channel.qam == Channel.QAM256) r_qam[1].setSelection(true);
		g.setLayoutData(gridData);
		g.pack();

		g = new Group(dialog, SWT.CENTER);
		g.setText("Misc");
		g.setLayout(new RowLayout());
		r_misc [0] = new Button(g, SWT.CHECK);
		r_misc[0].setText("Favourite");
		if(channel.fav == Channel.FAV_Y)  r_misc[0].setSelection(true);
		r_misc[1] = new Button(g, SWT.CHECK);
		r_misc[1].setText("Encrypted");
		if((channel.enc & Channel.FLAG_SCRAMBLED)!=0) r_misc[1].setSelection(true);
		r_misc[2] = new Button(g, SWT.CHECK);
		r_misc[2].setText("Locked");
		if(channel.lock == Channel.LOCK_Y) r_misc[2].setSelection(true);
		
		g.setLayoutData(gridData);
		g.pack();
		
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
			channel.freq	= new Integer(edit.t[1].getText());
			channel.symbr	= new Integer(edit.t[2].getText());
			channel.onid	= new Integer(edit.t[3].getText());
			channel.tsid	= new Integer(edit.t[4].getText());
			channel.sid		= new Integer(edit.t[5].getText());
			channel.mpid	= new Integer(edit.t[6].getText());
			channel.vpid	= new Integer(edit.t[7].getText());
			channel.nid 	= new Integer(edit.t[8].getText());
		} catch(NumberFormatException e) {
			new ErrorMessage("Cannot get number representation "+e.getMessage());
			return;
		}
		
		if(edit.r_stype[0].getSelection()) channel.stype = Channel.STYPE_TV;
		if(edit.r_stype[1].getSelection()) channel.stype = Channel.STYPE_RADIO;
		if(edit.r_stype[2].getSelection()) channel.stype = Channel.STYPE_DATA;
		if(edit.r_stype[3].getSelection()) channel.stype = Channel.STYPE_HD;
		
		if(edit.r_qam[0].getSelection()) channel.qam = Channel.QAM64;
		if(edit.r_qam[1].getSelection()) channel.qam = Channel.QAM256;
		
		if(edit.r_misc[0].getSelection())
			channel.fav = Channel.FAV_Y;
		else
			channel.fav = Channel.FAV_N;
		
		if(edit.r_misc[1].getSelection())
			channel.enc |= Channel.FLAG_SCRAMBLED;
		else
			channel.enc &= ~Channel.FLAG_SCRAMBLED;
		
		if(edit.r_misc[2].getSelection())
			channel.lock = Channel.LOCK_Y;
		else
			channel.lock = Channel.LOCK_N;
		
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
