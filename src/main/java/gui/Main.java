/**
 * @author polskafan <polska at polskafan.de>
 * @author rayzyt <rayzyt at mail-buero.de>
 * 
 * @version 0.47c2
 * 
 *	Copyright 2009 by Timo Dobbrick & Rayzyt
 *	For more information see http://www.polskafan.de/samsung
 *
 *   This file is part of SamyGO ChanEdit.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
// import java.util.regex.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import samyedit.AirCableChannel;
import samyedit.AirChannel;
import samyedit.Channel;
import samyedit.CloneChannel;
import samyedit.MapParser;
import samyedit.SatChannel;
import samyedit.SkyFeedChannels;
import samyedit.Zip;

/**
 * The Main class does create the application and controls all interaction 
 */
public class Main {

   /** display is the object the contains all visible items  */
   static Display display;
   /** shell does control all interactions */
   static Shell   shell;
   /** table contains all channels */
   public static Table table;
   public static Label statusLabel;
   public static Label modeLabel;
   public static byte mapType = 0x01;
   public static byte[] rawData;

   /** message levels for system output and debugging */
   public static final int LOG_ERROR = 3;
   public static final int LOG_WARN  = 2;
   public static final int LOG_INFO  = 1;

   /** channelList is a TreeMap of all channels read from the channel file "filepath" */	
   static TreeMap<Integer, Channel> channelList = new TreeMap<Integer, Channel>();
   /** chanFile contains the name of the actual channel file read into the channelList */
   static String chanFile; 
   /** scmFile contains the name of the SCM file that has been opened and extracted into tempDir */
   static String scmFile;
   /** tempDir is the place where the scmFile has been extracted to, 
    * it's generated automatically in the users "temp directory" in a sub-directory starting with "SamyGo" 
    */
   public static String tempDir;
   /** currentDir saved the current directory */
   static String cloneFile;
   /** File Name containing TV version information including directory */
   public static char scmVersion = 'C';
   /** byte describing the smcFile Version: B-series / C-series / D-series */
   static File currentDir = new File (".");

   static String version = "v0.54cd";
   static String series  = "C and D-Series";

   public static void main(String[] args) {
      
      if (args.length > 0) {
         for (int pos = 0; args.length > pos; pos++) { 
            if (args[pos].equals("-d")) { 
               tempDir  = "" + args[++pos]; 
            } else if (args[pos].equals("-c") || args[pos].equals("--channel")) {
               chanFile = "" + args[++pos]; 
            } else if (args[pos].equals("-s")) { 
               scmFile  = "" + args[++pos]; 
            } else if (args[pos].equals("-v") || args[pos].equals("--version")) { 
               System.out.println("Version: " + version);
               System.exit(0);
            } else if (args[pos].equals("--scmversion")) {
               scmVersion = args[++pos].toCharArray()[0];
            } else if (args[pos].equals("-h") || args[pos].equals("--help")) {
               System.out.println("Edit channels from Samsung tv dump \n" +
                  "\n" +
                  " -d                        Temporary directory \n" +
                  //" -c,--channel channel-file \n" + //not implemented yet
                  " -s scm-file               \n" +
                  " -v,--version {B|C|D}      Channels version \n " +
                  " -h,--help                 Show this help \n");
               System.exit(0);
            } else {
               System.out.println("argument "+args[pos]+" unknow");
               System.exit(1);
            }
         }
      }

      display = new Display();
      shell = new Shell(display,  SWT.SHELL_TRIM);
      shell.setSize(820, 500);
      shell.setText("SamyGO Channel Editor "+version);

      GridLayout layout = new GridLayout();
      layout.verticalSpacing = 0;
      layout.horizontalSpacing = 0;
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.numColumns = 2;
      shell.setLayout(layout);

      createMenuBar();

      createTable();
      GridData twoColumn = new GridData(GridData.FILL_BOTH);
      twoColumn.horizontalSpan = 2;
      table.setLayoutData(twoColumn);

      statusLabel = new Label(shell, SWT.BORDER);
      statusLabel.setText("Ready.");
      statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      modeLabel = new Label(shell, SWT.RIGHT | SWT.BORDER);
      modeLabel.setText("Mode: map-CableD");
      modeLabel.setLayoutData(new GridData());	
      modeLabel.pack();
      refresh();
      shell.open();

      statusUpdate(Main.LOG_INFO, "Version: "+ Main.version +" started with scmVersion: " + scmVersion);

      String tmpDir = null;
      if (Main.tempDir == null) {
         tmpDir = System.getProperty("user.home") + File.separator + "SamyGoTemp" + File.separator; 
      } else {
         tmpDir = Main.tempDir + File.separator;
      }
      if (! setTempDir(tmpDir)) return;

      //		display.post(SWT.CTRL + 'O');
      if (Main.scmFile != null) {
         File d = new File(Main.scmFile);
         if (d.isFile()) { 
            if (extractScm(Main.scmFile) <= 0) {
               statusUpdate(Main.LOG_WARN, "Main: Extraction of file: "+Main.scmFile+" failed!");
            }
         } else { // no file
            statusUpdate(Main.LOG_WARN, "Main: SCM-file is no regulare file or does not exist: "+d.getAbsoluteFile());
            Main.scmFile = null;
         }
      }
      if (Main.chanFile != null) {
         File d;
         if (Main.scmFile != null) {
            d = new File(tempDir+Main.chanFile);
         } else {
            d = new File(Main.chanFile);
         }
         if (d.isFile()) { 
            /** TODO let the program do as if selected an chanFile: shell.() */
            statusUpdate(Main.LOG_WARN, "Main: opening of channel file not yet implemented!\nGot file: "+d.getAbsoluteFile());
            chanFile = null; // TODO, remove when above is implemented
         } else {
            statusUpdate(Main.LOG_WARN, "Main: Channel-file is no regulare file or does not exist: "+d.getAbsoluteFile());
            chanFile = null;
         }
      }

      while (!shell.isDisposed())
         if (!display.readAndDispatch())
            display.sleep();
   }

   static void createMenuBar() {
      /* build the menu bar */
      Menu mbar = new Menu(shell, SWT.BAR);
      shell.setMenuBar(mbar);

      MyListener myListener = new MyListener();
      MySelListener mySelListener = new MySelListener(shell);

      MenuItem MenuBar = new MenuItem(mbar, SWT.CASCADE);
      MenuBar.setText("&File");

      Menu Menu = new Menu(MenuBar);
      Menu.addListener(SWT.Show, myListener);
      MenuBar.setMenu(Menu);

      MenuItem Item = new MenuItem(Menu, SWT.CASCADE);
      Item.setText("New");

      Menu subMenu = new Menu(Item);
      Item.setMenu(subMenu);

      Item = new MenuItem(subMenu, SWT.NONE);
      Item.setText("map-AirD");
      Item.setData("action", MySelListener.ACTION_NEWAIR);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(subMenu, SWT.NONE);
      Item.setText("map-CableD");
      Item.setData("action", MySelListener.ACTION_NEWCABLE);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(subMenu, SWT.NONE);
      Item.setText("map-SateD");
      Item.setData("action", MySelListener.ACTION_NEWSAT);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("&Open...\tCtrl+O");
      Item.setData("action", MySelListener.ACTION_OPEN);
      Item.setAccelerator(SWT.CTRL + 'O');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("&Save\tCtrl+S");
      Item.setData("action", MySelListener.ACTION_SAVE);
      Item.setAccelerator(SWT.CTRL + 'S');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Save As...");
      Item.setData("action", MySelListener.ACTION_SAVEAS);
      Item.addSelectionListener(mySelListener);


      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("SCM O&pen...\tCtrl+P");
      Item.setData("action", MySelListener.ACTION_OPEN_SCM);
      Item.setAccelerator(SWT.CTRL + 'P');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("S&CM Save\tCtrl+C");
      Item.setData("action", MySelListener.ACTION_SAVE_SCM);
      Item.setAccelerator(SWT.CTRL + 'C');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("SCM Save As...");
      Item.setData("action", MySelListener.ACTION_SAVE_SCM_AS);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("&Quit");
      Item.addSelectionListener(new Exit(shell));

      MenuBar = new MenuItem(mbar, SWT.CASCADE);
      MenuBar.setText("&Edit");

      Menu = new Menu(MenuBar);
      Menu.addListener(SWT.Show, myListener);
      MenuBar.setMenu(Menu);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Add new Channel...\tCtrl+N");
      Item.setData("action", MySelListener.ACTION_ADDCHAN);
      Item.setAccelerator(SWT.CTRL + 'N');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Edit Channel...\tCtrl+E");
      Item.setData("action", MySelListener.ACTION_EDITCHAN);
      Item.setAccelerator(SWT.CTRL + 'E');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Move Channel(s)...\tCtrl+M");
      Item.setData("action", MySelListener.ACTION_MOVECHAN);
      Item.setAccelerator(SWT.CTRL + 'M');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Delete Channel(s)\tDel");
      Item.setData("action", MySelListener.ACTION_DELETE);
      Item.setAccelerator(SWT.DEL);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Find Channel...\tCtrl+F");
      Item.setData("action", MySelListener.ACTION_FINDCHAN);
      Item.setAccelerator(SWT.CTRL + 'F');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Add to favourites\tCtrl+Up");
      Item.setData("action", MySelListener.ACTION_FAVADD);
      Item.setAccelerator(SWT.CTRL + SWT.ARROW_UP);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Remove from favourites\tCtrl+Down");
      Item.setData("action", MySelListener.ACTION_FAVDEL);
      Item.setAccelerator(SWT.CTRL + SWT.ARROW_DOWN);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Add parental lock\tCtrl+Alt+Up");
      Item.setData("action", MySelListener.ACTION_LOCKADD);
      Item.setAccelerator(SWT.CTRL + SWT.ALT + SWT.ARROW_UP);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Remove parental lock\tCtrl+Alt+Down");
      Item.setData("action", MySelListener.ACTION_LOCKDEL);
      Item.setAccelerator(SWT.CTRL + SWT.ALT + SWT.ARROW_DOWN);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Add Sky.de Feed Channels");
      Item.setData("action", MySelListener.ACTION_SKY);
      Item.addSelectionListener(mySelListener);

      MenuBar = new MenuItem(mbar, SWT.CASCADE);
      MenuBar.setText("&Fav79");

      Menu = new Menu(MenuBar);
      Menu.addListener(SWT.Show, myListener);
      MenuBar.setMenu(Menu);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Toggle List 1\tCtrl+Shift+1");
      Item.setData("action", MySelListener.ACTION_FAV79_1);
      Item.setAccelerator(SWT.CTRL + SWT.SHIFT + '1');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Toggle List 2\tCtrl+Shift+2");
      Item.setData("action", MySelListener.ACTION_FAV79_2);
      Item.setAccelerator(SWT.CTRL + SWT.SHIFT + '2');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Toggle List 3\tCtrl+Shift+3");
      Item.setData("action", MySelListener.ACTION_FAV79_3);
      Item.setAccelerator(SWT.CTRL + SWT.SHIFT + '3');
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Toggle List 4\tCtrl+Shift+4");
      Item.setData("action", MySelListener.ACTION_FAV79_4);
      Item.setAccelerator(SWT.CTRL + SWT.SHIFT + '4');
      Item.addSelectionListener(mySelListener);

      MenuBar = new MenuItem(mbar, SWT.CASCADE);
      MenuBar.setText("&Help");

      Menu = new Menu(MenuBar);
      MenuBar.setMenu(Menu);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Online Help");
      Item.setData("action", MySelListener.ACTION_WWW);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("Help Foum");
      Item.setData("action", MySelListener.ACTION_FORUM);
      Item.addSelectionListener(mySelListener);

      Item = new MenuItem(Menu, SWT.SEPARATOR);

      Item = new MenuItem(Menu, SWT.NONE);
      Item.setText("About");
      Item.setData("action", MySelListener.ACTION_ABOUT);
      Item.addSelectionListener(mySelListener);
   }

   private static void createTable() {
      table = new Table(shell, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
      table.setHeaderVisible(true);
      table.setLinesVisible(true);

      createColumnsCable();

      /* enable double clicking to edit a channel */
      table.addSelectionListener(new SelectionListener() {
         public void widgetDefaultSelected(SelectionEvent arg0) {
            new Edit(Main.getSelected()[0]);
         }
         public void widgetSelected(SelectionEvent arg0) { }
      });

      /* enable dragging... */
      DragSource ds = new DragSource(table, DND.DROP_DEFAULT | DND.DROP_MOVE);
      ds.setTransfer(new Transfer[] {TextTransfer.getInstance()});
      ds.addDragListener(new DragSourceAdapter() {
         public void dragSetData(DragSourceEvent event) {
            /* get the selected items in the drag source and put together a
             * newline separated string containing the selected channel numbers
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
               channelList.clear();
               new MapParser(fileList[0], channelList);
               chanFile = fileList[0];
               Main.statusUpdate(LOG_INFO, "Finished opening file: "+Main.chanFile);
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
               refresh(false);
            }
         }
      });
   }

   public static void createColumnsCable() {
      TableColumn[] col = new TableColumn[15];
      String[] colNames = {"No.", "Name", "Freq", "SR", "Nid",
         "Onid", "Tsid", "Sid", "Pid", "Vpid",
         "Typ", "Fav", "Fav79", "Enc", "Lock"};
      int[] colWidth = {40, 175, 40, 45, 45,
         45, 45, 45, 45, 45,
         45, 40, 50, 40, 40};
      int[] colAlign = {SWT.RIGHT, SWT.LEFT, SWT.CENTER, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER};
      int text = SortListenerFactory.TYPE_TEXT;
      int integer = SortListenerFactory.TYPE_INTEGER;
      int[] colType = {integer, text, text, integer, integer,
         integer, integer, integer, integer, integer,
         text, text, text, text, text};

      for(int c = 0; c < col.length; c++) {
         col[c] = new TableColumn(table, SWT.CENTER);
         col[c].setText(colNames[c]);
         col[c].setWidth(colWidth[c]);
         col[c].setAlignment(colAlign[c]);
         col[c].setResizable(true);
         col[c].setData("type", colType[c]);
         col[c].addListener(SWT.Selection, SortListenerFactory.getListener());
      }
   }

   public static void createColumnsAir() {
      TableColumn[] col = new TableColumn[14];
      String[] colNames = {"No.", "Name", "Freq", "LCN",
         "Onid", "Tsid", "Sid", "Pid", "Vpid",
         "Typ", "Fav", "Fav79", "Enc", "Lock"};
      int[] colWidth = {40, 175, 40, 40,
         45, 45, 45, 45, 45,
         45, 40, 50, 40, 40};
      int[] colAlign = {SWT.RIGHT, SWT.LEFT, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER};
      int text = SortListenerFactory.TYPE_TEXT;
      int integer = SortListenerFactory.TYPE_INTEGER;
      int[] colType = {integer, text, integer, integer,
         integer, integer, integer, integer, integer,
         text, text, text, text, text};

      for(int c = 0; c < col.length; c++) {
         col[c] = new TableColumn(table, SWT.CENTER);
         col[c].setText(colNames[c]);
         col[c].setWidth(colWidth[c]);
         col[c].setAlignment(colAlign[c]);
         col[c].setResizable(true);
         col[c].setData("type", colType[c]);
         col[c].addListener(SWT.Selection, SortListenerFactory.getListener());
      }
   }

   public static void createColumnsSat() {
      TableColumn[] col = new TableColumn[12];
      String[] colNames = {"No.", "Name", "Sat", "TPID",
         "Onid", "Tsid", "Sid", "Pid", "Vpid",
         "Typ", "Fav79", "Lock"};
      int[] colWidth = {40, 175, 45, 45,
         45, 45, 45, 45, 45,
         45, 50, 40};
      int[] colAlign = {SWT.RIGHT, SWT.LEFT, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER};
      int text = SortListenerFactory.TYPE_TEXT;
      int integer = SortListenerFactory.TYPE_INTEGER;
      int[] colType = {integer, text, integer, integer,
         integer, integer, integer, integer, integer,
         text, text, text};

      for(int c = 0; c < col.length; c++) {
         col[c] = new TableColumn(table, SWT.CENTER);
         col[c].setText(colNames[c]);
         col[c].setWidth(colWidth[c]);
         col[c].setAlignment(colAlign[c]);
         col[c].setResizable(true);
         col[c].setData("type", colType[c]);
         col[c].addListener(SWT.Selection, SortListenerFactory.getListener());
      }
   }

   public static void createColumnsClone() {
      TableColumn[] col = new TableColumn[12];
      String[] colNames = {"No.", "Name", "Freq", "Nid",
         "Onid", "Tsid", "Sid", "Pid", "Vpid",
         "Typ", "Fav", "Enc"};
      int[] colWidth = {40, 175, 40, 45,
         45, 45, 45, 45, 45,
         45, 40, 40};
      int[] colAlign = {SWT.RIGHT, SWT.LEFT, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.CENTER,
         SWT.CENTER, SWT.CENTER, SWT.CENTER};
      int text = SortListenerFactory.TYPE_TEXT;
      int integer = SortListenerFactory.TYPE_INTEGER;
      int[] colType = {integer, text, integer, integer,
         integer, integer, integer, integer, integer,
         text, text, text};

      for(int c = 0; c < col.length; c++) {
         col[c] = new TableColumn(table, SWT.CENTER);
         col[c].setText(colNames[c]);
         col[c].setWidth(colWidth[c]);
         col[c].setAlignment(colAlign[c]);
         col[c].setResizable(true);
         col[c].setData("type", colType[c]);
         col[c].addListener(SWT.Selection, SortListenerFactory.getListener());
      }
   }

   public static void deleteColumns() {
      TableColumn[] columns = table.getColumns();
      for(int i = 0; i < columns.length; i++)
         columns[i].dispose();
   }

   public static void refresh(boolean select) {
      table.setRedraw(false);

      /* save the view to restore it */
      int[] selected = table.getSelectionIndices();
      int topIndex = table.getTopIndex();

      /* remove everything to rebuild from TreeMap */
      table.clearAll();
      table.removeAll();

      switch(mapType) {
         case Channel.TYPE_CABLE:
            fillCable();
            break;
         case Channel.TYPE_AIR:
            fillAir();
            break;
         case Channel.TYPE_SAT:
            fillSat();
            break;
         case Channel.TYPE_CLONE:
            fillClone();
            break;
      }

      /* restore view, so we dont't end at top of the table */
      if(select) table.setSelection(selected);
      table.setRedraw(true);
      table.setTopIndex(topIndex);
   }

   public static void refresh() {
      refresh(true);
   }

   public static void fillCable() {
      modeLabel.setText("Mode: map-CableD");
      /* print out our channels */
      Iterator<Channel> it = channelList.values().iterator();
      while(it.hasNext()) {
         AirCableChannel c = (AirCableChannel)it.next();

         /* new item */
         TableItem t = new TableItem(table, SWT.LEFT);

         String typ;
         switch(c.stype) {
            case Channel.STYPE_TV:		typ = "TV";		break;
            case Channel.STYPE_RADIO:	typ = "Radio";	break;
            case Channel.STYPE_DATA:	typ = "Data";	break;
            case Channel.STYPE_HD:		typ = "HD";		break;
            default:					typ = "?";		break;
         }

         String fav	= ((c.fav & Channel.FLAG_FAV_1)!=0) ? "yes" : "no";
         String enc	= ((c.enc & Channel.FLAG_SCRAMBLED)!=0) ? "yes" : "no"; 
         String lock	= ((c.lock & Channel.FLAG_LOCK)!=0) ? "yes" : "no";
         String fav79 = "";
         if((c.fav79 & Channel.FLAG_FAV_1) == Channel.FLAG_FAV_1) fav79 += "1,";
         if((c.fav79 & Channel.FLAG_FAV_2) == Channel.FLAG_FAV_2) fav79 += "2,";
         if((c.fav79 & Channel.FLAG_FAV_3) == Channel.FLAG_FAV_3) fav79 += "3,";
         if((c.fav79 & Channel.FLAG_FAV_4) == Channel.FLAG_FAV_4) fav79 += "4,";
         if(fav79.length()!=0) fav79 = fav79.substring(0, fav79.length()-1);

         /* build text and asign it */
         String[] col = new String[] { c.num+"", c.name, c.freq+"", c.symbr+"",
            c.nid+"", c.onid+"", c.tsid+"", c.sid+"",
            c.mpid+"",  c.vpid+"", typ, fav, fav79, enc, lock
         };
         //			String[] 
         col = new String[] { c.num+"", c.name, c.getFreq()+"", c.symbr+"",
            c.nid+"", c.onid+"", c.tsid+"", c.sid+"",
            c.mpid+"",  c.vpid+"", typ, fav, fav79, enc, lock
         };
         t.setText(col);

         /* assign a reference to the Channel object, so we can do  drag n drop */
         t.setData(c);
      }
   }

   public static void fillAir() {
      modeLabel.setText("Mode: map-AirD");
      /* print out our channels */
      Iterator<Channel> it = channelList.values().iterator();
      while(it.hasNext()) {
         AirCableChannel c = (AirCableChannel)it.next();

         /* new item */
         TableItem t = new TableItem(table, SWT.LEFT);

         String typ;
         switch(c.stype) {
            case Channel.STYPE_TV:		typ = "TV";		break;
            case Channel.STYPE_RADIO:	typ = "Radio";	break;
            case Channel.STYPE_DATA:	typ = "Data";	break;
            case Channel.STYPE_HD:		typ = "HD";		break;
            default:					typ = "?";		break;
         }

         String fav	= ((c.fav & Channel.FLAG_FAV_1)!=0) ? "yes" : "no";
         String enc	= ((c.enc & Channel.FLAG_SCRAMBLED)!=0) ? "yes" : "no"; 
         String lock	= ((c.lock & Channel.FLAG_LOCK)!=0) ? "yes" : "no";

         String fav79 = "";
         if((c.fav79 & Channel.FLAG_FAV_1) == Channel.FLAG_FAV_1) fav79 += "1,";
         if((c.fav79 & Channel.FLAG_FAV_2) == Channel.FLAG_FAV_2) fav79 += "2,";
         if((c.fav79 & Channel.FLAG_FAV_3) == Channel.FLAG_FAV_3) fav79 += "3,";
         if((c.fav79 & Channel.FLAG_FAV_4) == Channel.FLAG_FAV_4) fav79 += "4,";
         if(fav79.length()!=0) fav79 = fav79.substring(0, fav79.length()-1);

         /* build text and assign it */
         String[] col = new String[] { c.num+"", c.name, c.freq+"", c.lcn+"",
            c.onid+"", c.tsid+"", c.sid+"",
            c.mpid+"",  c.vpid+"", typ, fav, fav79, enc, lock
         };
         t.setText(col);

         /* assign a reference to the Channel object, so we can do  drag n drop */
         t.setData(c);
      }
   }

   public static void fillSat() {
      modeLabel.setText("Mode: map-SateD");
      /* print out our channels */
      Iterator<Channel> it = channelList.values().iterator();
      while(it.hasNext()) {
         SatChannel c = (SatChannel)it.next();

         /* new item */
         TableItem t = new TableItem(table, SWT.LEFT);

         String typ;
         switch(c.stype) {
            case Channel.STYPE_TV:		typ = "TV";		break;
            case Channel.STYPE_RADIO:	typ = "Radio";	break;
            case Channel.STYPE_DATA:	typ = "Data";	break;
            case Channel.STYPE_HD:		typ = "HD";		break;
            default:					typ = "?";		break;
         }

         String lock	= (c.lock == Channel.FLAG_LOCK) ? "yes" : "no";
         String fav79 = "";
         if((c.fav79 & Channel.FLAG_FAV_1) == Channel.FLAG_FAV_1) fav79 += "1,";
         if((c.fav79 & Channel.FLAG_FAV_2) == Channel.FLAG_FAV_2) fav79 += "2,";
         if((c.fav79 & Channel.FLAG_FAV_3) == Channel.FLAG_FAV_3) fav79 += "3,";
         if((c.fav79 & Channel.FLAG_FAV_4) == Channel.FLAG_FAV_4) fav79 += "4,";
         if(fav79.length()!=0) fav79 = fav79.substring(0, fav79.length()-1);

         /* build text and asign it */
         String[] col = new String[] { c.num+"", c.name, c.sat+"", c.tpid+"",
            c.onid+"", c.tsid+"", c.sid+"", c.mpid+"",  c.vpid+"",
            typ, fav79, lock};
         t.setText(col);

         /* assign a reference to the Channel object, so we can do drag n drop */
         t.setData(c);
      }
   }

   public static void fillClone() {
      modeLabel.setText("Mode: CLONE.BIN");
      /* print out our channels */
      Iterator<Channel> it = channelList.values().iterator();
      while(it.hasNext()) {
         CloneChannel c = (CloneChannel)it.next();

         /* new item */
         TableItem t = new TableItem(table, SWT.LEFT);

         String typ;
         switch(c.stype) {
            case Channel.STYPE_TV:		typ = "TV";		break;
            case Channel.STYPE_RADIO:	typ = "Radio";	break;
            case Channel.STYPE_DATA:	typ = "Data";	break;
            case Channel.STYPE_HD:		typ = "HD";		break;
            default:					typ = "? ("+c.stype+")";		break;
         }

         String fav	= ((c.fav & Channel.FLAG_FAV_1)!=0) ? "yes" : "no";
         String enc	= ((c.enc & CloneChannel.FLAG_SCRAMBLED)!=0) ? "yes" : "no"; 

         //System.out.println(c.flags);
         /* build text and assign it */
         String[] col = new String[] { c.num+"", c.name, c.freq+"", c.nid+"",
            c.onid+"", c.tsid+"", c.sid+"",  c.mpid+"",  c.vpid+"",
            typ, fav, enc
         };
         t.setText(col);

         /* assign a reference to the Channel object, so we can do  drag n drop */
         t.setData(c);
      }
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
   }
   /** statusUpdate writes a message to stdout and into the Status Line of the program
    * 
    * @param message - string to be printed
    */
   public static void statusUpdate(int loglevel, String message) {
      switch (loglevel) {
         case Main.LOG_ERROR:
            System.out.println("ChanEd [Error]: " + message);
            new ErrorMessage(message);
            Main.statusLabel.setText(message);
            return;
         case Main.LOG_WARN:
            System.out.println("ChanEd  [Warn]: " + message);
            Main.statusLabel.setText(message);
            return;
         case Main.LOG_INFO:
            System.out.println("ChanEd  [Info]: " + message);
            Main.statusLabel.setText(message);
            return;
      }
   }
   /** verifies a path is available directory or can be created as one
    * if yes, it will be set as new Main.tempDir  
    * 
    * @param path - path to verify
    * @return if successful
    */
   static boolean setTempDir(String path) {
      // try the directory to be available or make it
      File d = new File(path + File.separator);
      String filename = d.getPath();
      int number = 0;
      while(d.isFile()) {
         // try to Find a valid tempDir name
         d = new File(filename+number++);
      }
      if (d.isDirectory()) { 	// if the directory does exist, then empty it
         File fList[] = d.listFiles();
         for (int i = 0; i < fList.length; i++) fList[i].delete();
      } else { // create a directory
         d.mkdir();
         if (! d.isDirectory()) { 	// if the directory does NOT exist, ERROR
            Main.statusUpdate(Main.LOG_ERROR, "Cannot create temporary Directory: "+ d.getPath());
            return false;
         }
         d.deleteOnExit(); // only delete the directory on exist, if we have created it
      }
      Main.tempDir = d.getPath()+ File.separator;
      Main.statusUpdate(Main.LOG_INFO, "New Temporary Directory is: "+ Main.tempDir);
      return true;
   }


   /** extracts an SCM file into a tempDir that it selects and creates itself
    * 
    * @param path String variable that contains the path to the SCM file
    * @return number of files extracted or 
    * <dd>-1 if SCM file format error</dd>
    */
   public static int extractScm(String path) {
      if (Zip.decompress(path, Main.tempDir) <= 0) {
         // alarm file empty or no SCM file
         new ErrorMessage("File empty or not in SCM Format!");
         Main.tempDir = null;
         return -1;
      }
      // extract TV Model from file SCM filename, e.g.: channel_list_UE40D8000_1101.scm
      if (path.matches(".*[A-Za-z][A-Za-z][0-9][0-9]+B[0-9][0-9][0-9][0-9].*.scm")) { scmVersion = 'B'; }
      if (path.matches(".*[A-Za-z][A-Za-z][0-9][0-9]+C[0-9][0-9][0-9][0-9].*.scm")) { scmVersion = 'C'; }
      if (path.matches(".*[A-Za-z][A-Za-z][0-9][0-9]+D[0-9][0-9][0-9][0-9].*.scm")) { scmVersion = 'D'; }
      statusUpdate(Main.LOG_INFO, "Main: Guess TV Version is: "+ scmVersion +" based on filename " + path);
      Main.scmFile = "" + path;
      File d       = new File(Main.tempDir);
      File aList[] = d.listFiles();
      // assure that we do not leave the files laying around
      int i = 0;
      for (; i < aList.length; i++) aList[i].deleteOnExit();
      /** extract TV Model from file system file Clone.Info
       * content: UED UE65C6700 
       **/
      Main.cloneFile = Main.tempDir + "CloneInfo";
      byte data[];
      try {
         data = MapParser.getFileContentsAsBytes(Main.cloneFile);
      } catch (IOException e) {
         e.printStackTrace();
         return -1;
      }
      String line = "";
      for(int j = 0; j<data.length; j++) {
         int c = (int)data[j];
         if(c==0x00) c = 0x20; // build a long string over borders / 0x00 is the end delimiter
         if(c < 0) c+=256;
         line += (char)c;
      }
      char version = ' ';
      if (line.matches(".*[A-Za-z]+ +[A-Za-z]+[0-9][0-9]+B[0-9]+.*")) { version = 'B'; }
      if (line.matches(".*[A-Za-z]+ +[A-Za-z]+[0-9][0-9]+C[0-9]+.*")) { version = 'C'; }
      if (line.matches(".*[A-Za-z]+ +[A-Za-z]+[0-9][0-9]+D[0-9]+.*")) { version = 'D'; }
      if (version != ' ') { 
         statusUpdate(Main.LOG_INFO, "Main: Read TV Version: "+ version +" from file " + Main.cloneFile);
         scmVersion = version; 
      } else {
         statusUpdate(Main.LOG_INFO, "Main: No TV Version info found in file " + Main.cloneFile);
      }
      return i;
   }
}

/* assign actions to our menubar */
class MySelListener implements SelectionListener {
   public static final int ACTION_NEWAIR   = 10;
   public static final int ACTION_NEWCABLE = 11;
   public static final int ACTION_NEWSAT   = 12;

   public static final int ACTION_OPEN     = 20;
   public static final int ACTION_OPEN_SCM = 21;
   public static final int ACTION_SAVE     = 22;
   public static final int ACTION_SAVEAS   = 23;
   public static final int ACTION_SAVE_SCM = 24;
   public static final int ACTION_SAVE_SCM_AS = 25;

   public static final int ACTION_ADDCHAN  = 36;
   public static final int ACTION_EDITCHAN = 37;
   public static final int ACTION_MOVECHAN = 38;
   public static final int ACTION_DELETE   = 39;
   public static final int ACTION_FINDCHAN = 40;
   public static final int ACTION_FAVADD	= 41;
   public static final int ACTION_FAVDEL	= 42;
   public static final int ACTION_LOCKADD	= 43;
   public static final int ACTION_LOCKDEL	= 44;
   public static final int ACTION_SKY      = 45;

   public static final int ACTION_FAV79_1	= 64;
   public static final int ACTION_FAV79_2	= 65;
   public static final int ACTION_FAV79_3	= 66;
   public static final int ACTION_FAV79_4	= 67;

   public static final int ACTION_WWW		= 127;
   public static final int ACTION_FORUM	= 128;
   public static final int ACTION_ABOUT    = 129;

   private Shell shell;

   private String opathdir;
   private String opathscmdir;

   MySelListener(Shell shell) {
      super();
      this.shell = shell;
   }

   public void widgetDefaultSelected(SelectionEvent e) {
      widgetSelected(e);
   }

   public void widgetSelected(SelectionEvent e) {

      String[] mapOrAll = {"*map-*D", "*"};
      String[] scmOrAll = {"*.scm", "*"};
      String[] allFiles = {"*map-*D", "*.scm", "*"};

      if(e.widget instanceof MenuItem) {
         /* a menu item was clicked, check what to do now */
         int action = (Integer) e.widget.getData("action");

         switch(action) {
            case ACTION_NEWAIR:
               Main.mapType = Channel.TYPE_AIR;
               Main.channelList.clear();
               Main.refresh();
               Main.deleteColumns();
               Main.createColumnsAir();
               break;
            case ACTION_NEWCABLE:
               Main.mapType = Channel.TYPE_CABLE;
               Main.channelList.clear();
               Main.refresh();
               Main.deleteColumns();
               Main.createColumnsCable();
               break;
            case ACTION_NEWSAT:
               Main.mapType = Channel.TYPE_SAT;
               Main.channelList.clear();
               Main.refresh();
               Main.deleteColumns();
               Main.createColumnsSat();
               break;
            case ACTION_OPEN_SCM:
               String path;
               path = Main.currentDir.getAbsolutePath();
               path = selectFileToOpen("Open SCM File",scmOrAll, path);
               if(path == null) {
                  Main.statusUpdate( Main.LOG_INFO, "Open SCM: No File selected!");
                  return;
               }
               if(Main.extractScm(path) <= 0) {
                  Main.statusUpdate(Main.LOG_INFO, "Open SCM: Extraction of file: "+path+" failed!");
                  return; //unsuccessful 
               } else {
                  Main.statusUpdate(Main.LOG_INFO, "Open: Successful extraction of file: "+path);
               }
            case ACTION_OPEN:
                if (opathdir == null) {
                    if (Main.scmFile == null) {
                        opathdir = Main.currentDir.getAbsolutePath();
                    } else {
                        opathdir = Main.tempDir;
                    }
                }

               path = selectFileToOpen("Open File", allFiles, opathdir);
               if(path == null) {
                  Main.statusUpdate(Main.LOG_INFO, "Open: No File selected!");
                  return;
               }
               opathdir = new File(path).getParent();
               if(path.endsWith(".scm")) {
                  if(Main.extractScm(path) <= 0) {
                     Main.statusUpdate(Main.LOG_INFO, "Open: Extraction of file: "+path+" failed!");
                     return; //unsuccessful 
                  } else {
                     Main.statusUpdate(Main.LOG_INFO, "Open: Successful extraction of file: "+path);
                  }
                  path = selectFileToSave("Open Channel File",mapOrAll , Main.tempDir);
                  if(path == null) {
                     Main.statusUpdate(Main.LOG_INFO, "Open: No Channel-File selected!");
                     return;
                  }
               }

               /* remove all channels added and kick them from ram */
               Main.channelList.clear();
               System.gc();

               /* parse the file and refresh the gui */
               new MapParser(path, Main.channelList);
               Main.refresh();

               /* save chanFile for later */
               Main.chanFile = path;
               Main.statusUpdate(Main.LOG_INFO, "Finished opening file: "+Main.chanFile);
               return;
            case ACTION_SAVEAS:
               String spath = selectFileToSave("Save File as",allFiles, Main.tempDir);
               if (spath == null) {
                  return; //no file opened selected
               }
               MapParser.write(spath, Main.channelList);
               Main.statusUpdate(Main.LOG_INFO, "File saved as: " + spath);
               return;				
            case ACTION_SAVE:
               if (Main.chanFile == null) {
                  Main.statusUpdate(Main.LOG_ERROR, "Nothing to save ... please open a file first!");
                  return; //no file opened selected
               }
               MapParser.write(Main.chanFile, Main.channelList);
               Main.statusUpdate(Main.LOG_INFO, "File "+ Main.chanFile +" saved.");
               return;
            case ACTION_SAVE_SCM_AS:
               // no return here!
            case ACTION_SAVE_SCM:
               String scmPath = null;
               if ((Main.scmFile == null) && (Main.tempDir == null)) {
                  Main.statusUpdate(Main.LOG_ERROR, "Nothing to save ... please open a file first!");
                  return; //no file selected yet
               }
               if (Main.chanFile != null) {
                  MapParser.write(Main.chanFile, Main.channelList);
                  Main.statusUpdate(Main.LOG_INFO, "File "+ Main.chanFile +" saved.");
               }
               if (Main.scmFile != null) {
                  scmPath = Main.scmFile;
               }
               if ((scmPath == null) || (action == ACTION_SAVE_SCM_AS)) {
                  scmPath = selectFileToSave("Save SCM as",scmOrAll, scmPath);
               }
               if(scmPath == null) return; //no file defined
               Main.scmFile = scmPath; //set new scmFile name
               if (Zip.compress(scmPath, Main.tempDir)< 0) {
                  // alarm: compress failed
                  Main.statusUpdate(Main.LOG_INFO, "scmFile " + Main.scmFile + " not saved!");
                  return;
               }
               Main.statusUpdate(Main.LOG_INFO, "SCM-File saved as: " + Main.scmFile);
               return;
            case ACTION_ADDCHAN:
               switch(Main.mapType) {
                  case Channel.TYPE_CABLE:
                     // TODO fill with values of currently selected channel
                     new Edit(new AirCableChannel());
                     break;
                  case Channel.TYPE_AIR:
                     new Edit(new AirChannel());
                     break;
                  case Channel.TYPE_SAT:
                     new Edit(new SatChannel());
                     break;
               }
               return;
            case ACTION_EDITCHAN:
               TableItem[] item = Main.table.getSelection();
               if(item.length > 0) {
                  new Edit((Channel) item[0].getData());
               }
               return;
            case ACTION_MOVECHAN:
               /* nothing selected, don't do anything */
               if(Main.getSelected().length==0) return;
               new Move();
               return;
            case ACTION_DELETE:
               Main.deleteChannels(Main.getSelected());
               Main.refresh(false);
               return;
            case ACTION_FINDCHAN:
               new Find();
               return;
            case ACTION_FAVADD:
               Channel[] addfav = Main.getSelected();
               for(int i = 0; i<addfav.length; i++)
                  addfav[i].fav |= Channel.FLAG_FAV_1;
               Main.refresh();
               return;
            case ACTION_FAVDEL:
               Channel[] delfav = Main.getSelected();
               for(int i = 0; i<delfav.length; i++)
                  delfav[i].fav &= ~Channel.FLAG_FAV_1;
               Main.refresh();
               return;
            case ACTION_LOCKADD:
               Channel[] addlock = Main.getSelected();
               for(int i = 0; i<addlock.length; i++)
                  addlock[i].lock |= Channel.FLAG_LOCK;
               Main.refresh();
               return;
            case ACTION_LOCKDEL:
               Channel[] dellock = Main.getSelected();
               for(int i = 0; i<dellock.length; i++)
                  dellock[i].lock &= ~Channel.FLAG_LOCK;
               Main.refresh();
               return;
            case ACTION_SKY:
               SkyFeedChannels.add(Main.channelList);
               Main.refresh();
               return;
            case ACTION_FAV79_1:
               Channel[] fav791 = Main.getSelected();
               for(int i = 0; i<fav791.length; i++)
                  fav791[i].fav79 ^= Channel.FLAG_FAV_1;
               Main.refresh();
               return;
            case ACTION_FAV79_2:
               Channel[] fav792 = Main.getSelected();
               for(int i = 0; i<fav792.length; i++)
                  fav792[i].fav79 ^= Channel.FLAG_FAV_2;
               Main.refresh();
               return;
            case ACTION_FAV79_3:
               Channel[] fav793 = Main.getSelected();
               for(int i = 0; i<fav793.length; i++)
                  fav793[i].fav79 ^= Channel.FLAG_FAV_3;
               Main.refresh();
               return;
            case ACTION_FAV79_4:
               Channel[] fav794 = Main.getSelected();
               for(int i = 0; i<fav794.length; i++)
                  fav794[i].fav79 ^= Channel.FLAG_FAV_4;
               Main.refresh();
               return;
            case ACTION_WWW:
               try {
                  java.awt.Desktop.getDesktop().browse(new URI("http://www.ullrich.es/job/sendersortierung/samsung-samygo/"));
               } catch (IOException ex) {
                  ex.printStackTrace();
               } catch (URISyntaxException ex) {
                  ex.printStackTrace();
               }
               return;
            case ACTION_FORUM:
               try {
                  java.awt.Desktop.getDesktop().browse(new URI("http://www.hifi-forum.de/index.php?action=browseT&forum_id=151&thread=11695&postID=first#first"));
               } catch (IOException ex) {
                  ex.printStackTrace();
               } catch (URISyntaxException ex) {
                  ex.printStackTrace();
               }
               return;
            case ACTION_ABOUT:
               new About();
               return;
            default:
               return;
         }
      }
   }
   /** File Dialog to select a file for reading
    * 
    * @param message displayed for selection
    * @param filter  file selection filters
    * @param dir     directory to look into
    * @return String  - the filename selected or 
    * <dd>null - if unsuccessful</dd>
    */
   private String selectFileToOpen(String message, String[] filter, String dir) {
      FileDialog fd = new FileDialog(shell, SWT.OPEN);
      if (message!=null) {
          fd.setText(message);
      }
      if (filter!= null) {
          fd.setFilterExtensions(filter);
      }
      if (dir != null) {
          fd.setFilterPath(dir);
      }
      return fd.open();
   }


   /** File Dialog to select a file for writing
    * 
    * @param message displayed for selection
    * @param filter  file selection filters
    * @param dir     directory to look into
    * @return String  - the filename selected or 
    * <dd>null - if unsuccessful</dd>
    */
   private String selectFileToSave(String message, String[] filter, String dir) {
      String path;
      FileDialog fd = new FileDialog(shell, SWT.SAVE);
      if (message!=null) fd.setText(message);
      if (filter!= null) fd.setFilterExtensions(filter);
      if (dir   != null) fd.setFilterPath(dir);
      path = fd.open();
      return path;
   }
}
class MyListener implements Listener {
   public void handleEvent(Event event) {
      MenuItem[] menuItems = ((Menu)event.widget).getItems();
      for(int i = 0; i<menuItems.length; i++) {
         try {
            int favaction = (Integer)menuItems[i].getData("action");
            switch(favaction) {
               case MySelListener.ACTION_SAVE:
               case MySelListener.ACTION_SAVEAS:
                  if(Main.chanFile != null && Main.chanFile.length() > 0)
                     menuItems[i].setEnabled(true);
                  else
                     menuItems[i].setEnabled(false);
                  break;
               case MySelListener.ACTION_SAVE_SCM:
               case MySelListener.ACTION_SAVE_SCM_AS:
                  if(Main.scmFile != null && Main.scmFile.length() > 0)
                     menuItems[i].setEnabled(true);
                  else
                     menuItems[i].setEnabled(false);
                  break;
               case MySelListener.ACTION_EDITCHAN:
               case MySelListener.ACTION_MOVECHAN:
               case MySelListener.ACTION_DELETE:
               case MySelListener.ACTION_FAVADD:
               case MySelListener.ACTION_FAVDEL:
                  if(Main.getSelected().length > 0) {
                     menuItems[i].setEnabled(true);
                  } else {
                     menuItems[i].setEnabled(false);
                  }
                  break;
               case MySelListener.ACTION_LOCKADD:
               case MySelListener.ACTION_LOCKDEL:
                  if(Main.getSelected().length > 0 &&
                        (Main.mapType & (Channel.TYPE_AIR|Channel.TYPE_CABLE)) != 0) {
                     menuItems[i].setEnabled(true);
                  } else {
                     menuItems[i].setEnabled(false);
                  }
                  break;
               case MySelListener.ACTION_FAV79_1:
               case MySelListener.ACTION_FAV79_2:
               case MySelListener.ACTION_FAV79_3:
               case MySelListener.ACTION_FAV79_4:
                  if(Main.getSelected().length > 0 &&
                        (Main.mapType & (Channel.TYPE_AIR|Channel.TYPE_CABLE|Channel.TYPE_SAT)) != 0)
                     menuItems[i].setEnabled(true);
                  else
                     menuItems[i].setEnabled(false);
                  break;
            }
         } catch (NullPointerException e) {
            continue;
         }
      }
   }
}

/** implents a sorting function for the table */
class SortListenerFactory implements Listener {
   public static final int TYPE_INTEGER = 0;
   public static final int TYPE_TEXT = 1;
   private Comparator<TableItem> currentComparator = null;

   private SortListenerFactory() {
      currentComparator = Comparator;
   }

   public static Listener getListener() {
      return new SortListenerFactory();
   }

   private int colIndex = 0;
   private int colType = 0;
   private int updown   = -1;

   // do a comparator for array sort
   private Comparator<TableItem> Comparator = new Comparator<TableItem>() {
      public int compare(TableItem t1, TableItem t2) {
         /* order by the text in the column, ignore the case of the chars */
         String v1 = t1.getText(colIndex).toLowerCase();
         String v2 = t2.getText(colIndex).toLowerCase();

         switch(colType) {
            /* we are in a text column, treat as text */
            case TYPE_TEXT:
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
      colType = (Integer)currentColumn.getData("type"); 

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
