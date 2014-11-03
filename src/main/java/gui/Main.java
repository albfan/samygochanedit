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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import samyedit.Channel;
import samyedit.MapParser;
import samyedit.SkyFeedChannels;

public class Main {
	
	static Display display;
	static Shell shell;
	static Table table;
	static Clipboard cb;
	static TreeMap<Integer, Channel> channelList = new TreeMap<Integer, Channel>();
	static String filepath;
	
	public static void main(String[] args) {
		display = new Display();
		cb = new Clipboard(display);
		shell = new Shell(display,  SWT.SHELL_TRIM);
		shell.setText("SamyGO Channel Editor v0.2");
				
		createMenuBar();
		createTable();
		refresh();
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
	
	static void createMenuBar() {
		/* build the menu bar */
		Menu mbar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mbar);
		
		MyListener myListener = new MyListener(shell);
		MenuItem MenuBar = new MenuItem(mbar, SWT.CASCADE);
		MenuBar.setText("&File");
		
		Menu Menu = new Menu(MenuBar);
		MenuBar.setMenu(Menu);
		
		MenuItem Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("&Open...\tCtrl+O");
		Item.setData("action", MyListener.ACTION_OPEN);
		Item.setAccelerator(SWT.CTRL + 'O');
		Item.addSelectionListener(myListener);

		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("&Save\tCtrl+S");
		Item.setData("action", MyListener.ACTION_SAVE);
		Item.setAccelerator(SWT.CTRL + 'S');
		Item.addSelectionListener(myListener);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Save As...");
		Item.setData("action", MyListener.ACTION_SAVEAS);
		Item.addSelectionListener(myListener);
		
		Item = new MenuItem(Menu, SWT.SEPARATOR);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("&Quit");
		Item.addSelectionListener(new Exit(shell));
		
		MenuBar = new MenuItem(mbar, SWT.CASCADE);
		MenuBar.setText("&Edit");
		
		Menu = new Menu(MenuBar);
		MenuBar.setMenu(Menu);

		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Add new Channel...");
		Item.setData("action", MyListener.ACTION_ADDCHAN);
		Item.addSelectionListener(myListener);

		Item = new MenuItem(Menu, SWT.SEPARATOR);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Edit Channel...");
		Item.setData("action", MyListener.ACTION_EDITCHAN);
		Item.addSelectionListener(myListener);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Move Channel(s)...");
		Item.setData("action", MyListener.ACTION_MOVECHAN);
		Item.addSelectionListener(myListener);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Delete Channel(s)");
		Item.setData("action", MyListener.ACTION_DELETE);
		Item.addSelectionListener(myListener);
		
		Item = new MenuItem(Menu, SWT.SEPARATOR);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Find Channel...\tCtrl+F");
		Item.setData("action", MyListener.ACTION_FINDCHAN);
		Item.setAccelerator(SWT.CTRL + 'F');
		Item.addSelectionListener(myListener);
		
		Item = new MenuItem(Menu, SWT.SEPARATOR);
		
		Item = new MenuItem(Menu, SWT.NONE);
		Item.setText("Add Sky.de Feed Channels");
		Item.setData("action", MyListener.ACTION_SKY);
		Item.addSelectionListener(myListener);
	}

	private static void createTable() {
		shell.setLayout(new FillLayout());
		final Composite comp = new Composite(shell, SWT.NONE);
		table = new Table(comp, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

		/* resize table, when program window gets resized */
		comp.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = comp.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2*table.getBorderWidth();
				if (preferredSize.y > area.height + table.getHeaderHeight()) {
					/* Subtract the scrollbar width from the total column width
					 * if a vertical scrollbar will be required */
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				
				/* we don't care about column width, just resize */
				table.setSize(area.width, area.height);
			}
		});
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		/* build main table */
		TableColumn[] col = new TableColumn[11];
		String[] colNames = {"No.", "Name", "Frq", "SR", "Onid", "Tsid", "Sid", "Pid", "Vpid", "Typ", "Enc", "Key"};
		int[] colWidth = {40, 200, 40, 45, 45, 45, 45, 45, 45, 45, 45};
		int[] colAlign = {SWT.RIGHT, SWT.LEFT, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER,SWT.CENTER,SWT.CENTER,SWT.CENTER};
		for(int c = 0; c < col.length; c++) {
			col[c] = new TableColumn(table, SWT.CENTER);
			col[c].setText(colNames[c]);
			col[c].setWidth(colWidth[c]);
			col[c].setAlignment(colAlign[c]);
			col[c].setResizable(true);
			col[c].addListener(SWT.Selection, SortListenerFactory.getListener());
		}
		/* set a starting size */
		table.setSize(780, 500);
		
		/* enable dragging... */
	    DragSource ds = new DragSource(table, DND.DROP_DEFAULT | DND.DROP_MOVE);
	    ds.setTransfer(new Transfer[] {TextTransfer.getInstance()});
	    ds.addDragListener(new DragSourceAdapter() {
	    	public void dragSetData(DragSourceEvent event) {
				/* get the selected items in the drag source and put together a
				 * newline seperated string containing the selected channel numbers
				 */
				DragSource ds = (DragSource) event.widget;
				Table table = (Table) ds.getControl();
				TableItem[] selection = table.getSelection();
				
				StringBuffer buff = new StringBuffer("channel\n");
				for (int i = 0, n = selection.length; i < n; i++) {
					buff.append(selection[i].getText()+"\n");
				}
				
				event.data = buff.toString();
	    	}
	    });
	    
	    /* ... and dropping */
	    DropTarget dt = new DropTarget(table, DND.DROP_DEFAULT | DND.DROP_MOVE );
	    /* accept a file that is then loaded as a channel list or a text that might be
	     * a list of channel numbers to reorder
	     */
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance(), TextTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}
			
			public void drop(DropTargetEvent event) {
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
					/* got file, try to open */
					String fileList[] = (String[])event.data;
					table.removeAll();
					table.clearAll();
					Main.channelList.clear();
					new MapParser(fileList[0], channelList);
					refresh();
				}
				
				if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
					/* got text, maybe move channel */
					String move = (String) event.data;
					String[] channels = move.split("\n");
					Channel[] selected = new Channel[channels.length-1];
					
					/* don't allow dropping on the table head */
					if(!(event.item instanceof TableItem)) return;
					
					if(channels[0].equals("channel")) 
						for(int i = 1; i<channels.length; i++)
							selected[i-1] = Main.channelList.get(new Integer(channels[i]));
					else
						return;
					
					/* okay we have channels to move, lets do this */
					TableItem item = (TableItem) event.item;
					Channel targetChan = (Channel) item.getData();
					
					moveChannels(selected, targetChan);
				}
			}
		});
	}

	public static void refresh() {
		/* save the view to restore it */
		int topIndex = table.getTopIndex();
		table.setRedraw(false);
		
		/* remove everything to rebuild from TreeMap */
		table.clearAll();
		table.removeAll();

		/* print out our channels */
		Iterator<Channel> it = channelList.values().iterator();
		while(it.hasNext()) {
			Channel c = it.next();
			/* new item */
			TableItem t = new TableItem(table, SWT.LEFT);
			/* build text and asign it */
			String[] col = new String[] { c.num+"", c.name, c.freq+"",
				c.symbr+"", c.onid+"", c.tsid+"",  c.sid+"",
				c.mpid+"",  c.vpid+"", c.stype+"", c.enc+""
			};
			t.setText(col);
			
			/* assign a reference to the Channel object, so we can do  drag n drop */
			t.setData(c);
		}
		
		table.setRedraw(true);
		/* restore view, so we dont't end at top of the table */
		table.setTopIndex(topIndex);
	}
	
	
	public static void moveChannels(Channel[] selected, Channel targetChan) {
		int cIndex = targetChan.num;
		
		/* first delete all the channels to be moved from list */
		deleteChannels(selected);
		
		/* then delete all channels after targetChan.num from list */
		TreeMap<Integer, Channel> tailMap = new TreeMap<Integer, Channel>(channelList.tailMap(cIndex));
		Iterator<Channel> it = tailMap.values().iterator();
		while(it.hasNext()) {
			channelList.remove(it.next().num);
		}
		
		/* now readd them at targetChannel.num, targetChannel might have moved up */
		/* readd everything with new channel number */
		for(int i = 0; i<selected.length;i++) {
			selected[i].num = cIndex;
			channelList.put(selected[i].num, selected[i]);
			cIndex++;
		}
		
		/* after that all other channels, only renumberering if we have to
		 * (mind the gap!) */
		it = tailMap.values().iterator();
		while(it.hasNext()) {
			Channel c = it.next();
			if(channelList.containsKey(c.num)) {
				/* channel number already used, give it a new one */
				c.num = cIndex;
				channelList.put(c.num, c);
				cIndex++;
			} else {
				/* we hit a gap, just keep the numbers from now on */
				channelList.put(c.num, c);
			}
		}
		
		refresh();
	}
	
	static Channel[] getSelected() {
		TableItem[] items = table.getSelection();
		Channel[] selected = new Channel[items.length];
		for(int i = 0; i<items.length; i++) {
			selected[i] = channelList.get(new Integer(items[i].getText()));
		}
		return selected;
	}
	
	static void deleteChannels(Channel[] selected) {
		/* delete all selected channels from TreeMap and refresh view */
		for(int i = 0; i<selected.length; i++) {
			channelList.remove(selected[i].num);
			int j = selected[i].num+1;
			Channel c;
			/* also move channels behind up, until the next gap */
			while((c = channelList.get(j))!=null) {
				channelList.remove(j);
				c.num -= 1;
				channelList.put(c.num, c);
				j++;
			}
		}
		Main.refresh();
	}
}

/* assign actions to our menubar */
class MyListener implements SelectionListener {
	public static final int ACTION_OPEN     = 0;
	public static final int ACTION_SAVE     = 1;
	public static final int ACTION_SAVEAS   = 2;
	public static final int ACTION_ADDCHAN  = 3;
	public static final int ACTION_EDITCHAN = 4;
	public static final int ACTION_MOVECHAN = 5;
	public static final int ACTION_DELETE   = 6;
	public static final int ACTION_FINDCHAN = 7;
	public static final int ACTION_SKY      = 8;
	
	private Shell shell;
	MyListener(Shell shell) {
		super();
		this.shell = shell;
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		if(e.widget instanceof MenuItem) {
			/* a menu item was clicked, check what to do now */
			int action = (Integer) e.widget.getData("action");
			
			switch(action) {
				case ACTION_OPEN:
					/* show open dialog to select a file */
					FileDialog fd = new FileDialog(shell, SWT.OPEN);
					fd.setText("Open");
					String[] filterExt = {"*.*"};
					fd.setFilterExtensions(filterExt);
					String path = fd.open();
					if(path == null) return;
					
					/* remove all channels added and kick them from ram */
					Main.channelList.clear();
					System.gc();
					
					/* parse the file and refresh the gui */
					new MapParser(path, Main.channelList);
					Main.refresh();
					
					/* save filepath for later */
					Main.filepath = path;
					return;
				case ACTION_SAVE:
					if (Main.filepath == null) return;
					/* write the file out */
					MapParser.write(Main.filepath, Main.channelList);
					return;
				case ACTION_SAVEAS:
					/* show save dialog to select a filename to save to */
					FileDialog fsd = new FileDialog(shell, SWT.SAVE);
					fsd.setText("Save");
					String[] filtersExt = {"*.*"};
					fsd.setFilterExtensions(filtersExt);
					String spath = fsd.open();
					if(spath == null) return;
					
					/* write the file out */
					MapParser.write(spath, Main.channelList);
					return;
				case ACTION_ADDCHAN:
					new Edit(new Channel());
					return;
				case ACTION_EDITCHAN:
					TableItem[] item = Main.table.getSelection();
					if(item.length > 0) {
						new Edit((Channel) item[0].getData());
					}
					return;
				case ACTION_MOVECHAN:
					new Move();
					return;
				case ACTION_DELETE:
					/* delete marked channels */
					Main.deleteChannels(Main.getSelected());
					return;
				case ACTION_FINDCHAN:
					new Find();
					return;
				case ACTION_SKY:
					SkyFeedChannels.add(Main.channelList);
					Main.refresh();
					return;
				default:
					return;
			}
		}
	}
}

/* user wants to sort the table, do him the favour */
class SortListenerFactory implements Listener {
    private Comparator<TableItem> currentComparator = null;
    
    private SortListenerFactory() {
    	currentComparator = Comparator;
    }
    
	public static Listener getListener() {
        return new SortListenerFactory();
    }
    
    private int colIndex = 0;
    private int updown   = -1;
          
    // do a comparator for array sort
    private Comparator<TableItem> Comparator = new Comparator<TableItem>() {
        public int compare(TableItem t1, TableItem t2) {
        	/* order by the text in the column, ignore the case of the chars */
            String v1 = t1.getText(colIndex).toLowerCase();
            String v2 = t2.getText(colIndex).toLowerCase();

            switch(colIndex) {
            	/* we are in column one, treat as text */
            	case 1:
            		return v1.compareTo(v2)*updown;
            	/* we are in another column, parse to integer and compare then */
            	default:
            		return new Integer(t1.getText(colIndex)).compareTo(Integer.parseInt(t2.getText(colIndex)))*updown;
            }
        }    
    };
          
    public void handleEvent(Event e) {
    	/* reverse ordering when clicking twice */
        updown = (updown == 1 ? -1 : 1);
        
        /* get selected column and the table it belongs to */
        TableColumn currentColumn = (TableColumn)e.widget;
        Table table = currentColumn.getParent();

        /* set columnIndex, so we can decide what comparator should to */
        colIndex = searchColumnIndex(currentColumn);
        
        /* disable redrawing until everything is done */
        table.setRedraw(false);

        /* get items and sort them */
        TableItem[] items = table.getItems();
        Arrays.sort(items, currentComparator);
  
        /* then update the table using sorted array */
        table.setItemCount(items.length);
        for (int i = 0;i<items.length;i++) {   
            TableItem item = new TableItem(table,SWT.NONE,i);
            /* do text */
            item.setText(getData(items[i]));
            /* connect channel reference */
            item.setData(items[i].getData());
            /* kill the old item */
            items[i].dispose();
        }

        /* finished, redraw */
        table.setRedraw(true);     
    }
    
    private String[] getData(TableItem t) {
        Table table = t.getParent();
        
        int colCount = table.getColumnCount();
        String[] s = new String[colCount];
        
        for (int i = 0;i<colCount;i++) s[i] = t.getText(i);
                
        return s;
    }
    
    private int searchColumnIndex(TableColumn currentColumn) {
        Table table = currentColumn.getParent();
        
        int in = 0;
        for (int i = 0;i<table.getColumnCount();i++)
            if (table.getColumn(i) == currentColumn)
                in = i;
        
        return in;
    }
}