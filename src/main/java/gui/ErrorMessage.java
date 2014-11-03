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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class ErrorMessage {
	public ErrorMessage(String error) {
		createGUI(Main.shell, error);
	}

	public ErrorMessage(Shell shell, String error) {
		createGUI(shell, error);
	}
	
	private void createGUI(Shell shell, String error) {
		Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 1;
		dialog.setLayout(layout);
 		
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		Label l = new Label(dialog, SWT.CENTER);
		l.setText(error);
		l.pack();
		l.setLayoutData(g);
		
		g = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Button b = new Button(dialog, SWT.CENTER);
		b.setText("&OK");
		b.addSelectionListener(new Exit(dialog));
		b.setLayoutData(g);
		
		dialog.pack();
		dialog.setText("Error");
		
	    Monitor primary = Main.display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = dialog.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    dialog.setLocation(x, y);
	    
	    dialog.open();
	}
}
