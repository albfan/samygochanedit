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

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import samyedit.Channel;

public class Find {
	Iterator<Channel> it;
	
	public Find() {
		createGUI();
	}

	private void createGUI() {
		Shell dialog = new Shell(Main.shell, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText("Find channel");
		
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 2;
		dialog.setLayout(layout);
 		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;	
		
		Label l = new Label(dialog, SWT.CENTER);
		l.setText("Name:");
		Text t = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		t.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		
		Composite buttons = new Composite(dialog, SWT.RIGHT);
		GridLayout buttonLayout = new GridLayout(SWT.FILL, true);
		buttonLayout.numColumns = 2;
		buttons.setLayout(buttonLayout);
		buttons.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		
		Button b = new Button(buttons, SWT.CENTER);
		b.setText("Find Next");
		b.addSelectionListener(new DoFind(dialog, t));
		
		b = new Button(buttons, SWT.CENTER);
		b.setText("Close");
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

class DoFind implements SelectionListener {
	Shell shell;
	Text text;
	Iterator<Channel> it;
	
	public DoFind(Shell dialog, Text t) {
		this.shell = dialog;
		this.text = t;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		shell.dispose();
	}

	public void widgetSelected(SelectionEvent arg0) {
		if(it == null) it = Main.channelList.values().iterator();
		
		Channel c = null;
		while(it.hasNext()) {
			c = it.next();
			if(c.name.toLowerCase().contains(text.getText())) break;
		}

		if(!it.hasNext()) {
			it = null;
			new ErrorMessage(shell, "Reached end!");
		}
		
		if(c != null) {
			/* found something, select this channel */
			int j = 0;
			Iterator<Integer> keyIt = Main.channelList.keySet().iterator();
			while(keyIt.hasNext()) {
				if(c.num == keyIt.next()) break;
				j++;
			}
			Main.table.setSelection(j);
		}
	}
}
