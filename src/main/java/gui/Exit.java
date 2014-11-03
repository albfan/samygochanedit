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

package gui;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

public class Exit implements SelectionListener {
	Shell shell;
	public Exit(Shell shell) {
		this.shell = shell;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
		/* close current window, if it is the main window
		 * return to system
		 */
		if(shell == Main.shell) 
			System.exit(0);

		shell.dispose();
	}

	public void widgetSelected(SelectionEvent arg0) {
		/* close current window, if it is the main window
		 * return to system
		 */
		if(shell == Main.shell) 
			System.exit(0);

		shell.dispose();
	}
}
