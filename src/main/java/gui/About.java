/**
 * @author polskafan <polska at polskafan.de>
 * @version 0.42
  
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class About {
	private static String message = "SamyGO Channel Editor\n" +
	"a Java based Samsung Channel Editor\n" +
	"Version: "+Main.version+"\n\n" +

	"Written and developed by polskafan <polska@polskafan.de> and\n" +
	"upgraded for Samsung "+Main.series+" TV models by rayzyt <rayzyt@mail-buero.de>\n" +
	"For more information see: <a>http://www.ullrich.es/job/sendersortierung/samsung-samygo/</a>\n"+
	"Source Code and the latest version: <a>https://sourceforge.net/projects/samygochanedit/</a>\n\n"+
	"This program is free software: you can redistribute it and/or modify\n" +
	"it under the terms of the GNU General Public License as published by\n" +
	"the Free Software Foundation, either version 3 of the License, or\n" +
	"(at your option) any later version.\n\n" +
	
	"This program is distributed in the hope that it will be useful,\n" +
	"but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
	"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
	"GNU General Public License for more details.\n\n" +
	"You should have received a copy of the GNU General Public License\n" +
	"along with this program.  If not, see <a>http://www.gnu.org/licenses/</a>.";
	
	public About() {
		Shell dialog = new Shell(Main.shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 1;
		dialog.setLayout(layout);
 		
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		Link link = new Link(dialog, SWT.NONE);
		link.setText(message);
		link.setLayoutData(g);
		link.pack();
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI(event.text));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				Link widget = (Link)event.widget;
				Shell dialog = widget.getShell();
				dialog.getChildren()[1].setFocus();
			}
		});
	
		g = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Button b = new Button(dialog, SWT.CENTER);
		b.setText("&OK");
		b.addSelectionListener(new Exit(dialog));
		b.setLayoutData(g);
		b.setFocus();
		
		dialog.pack();
		dialog.setText("About SamyGO ChanEdit");
		
	    Monitor primary = Main.display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = dialog.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    dialog.setLocation(x, y);
	    
	    dialog.open();
	}
}
