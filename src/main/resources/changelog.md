**Version 0.42**

-   added dynamic menus showing only currently available options
-   added ability to edit clone.bin files
-   added new view mode for clone.bin files

**Version 0.41**

-   made links in about dialog clickable
-   added link to documentation page
-   fixed *Add Channel* on map-AirD set Symbol Rate field to 6900, instead of 0
-   fixed *Move Channel* to an empty channel number, resulted in a crash

**Version 0.40**

-   added ability to edit map-AirD and map-SateD
-   added different view modes to display only the necessary data for different list types
-   added ability to edit favourite lists 1-4 on x79 devices
-   fixed lost selection of channels after editing
-   fixed again table flickering when moving channels around
-   fixed deleted channels showing up again on map-AirD and map-CableD
-   fixed unknown fields in original files will get copied back from raw data instead of being overwritten with default values
-   fixed Sky.de interactive channel function does not set favourite flags randomly anymore

**Version 0.31**

-   fixed ordering of type, favourite, encryption and parental lock columns resulted in a crash
-   added About dialog

**Version 0.3**

-   double click on channel opens its edit window
-   added channel type for HD channels
-   corrected encryption flag
-   added favourite editing functions
-   fixed crashes when entering text into fields where a number was expected
-   added status bar displaying open/save status
-   added new accelerators (e.g. CTRL+M for channel move, DEL for delete, etc.)
-   fixed quick ‘Save’ after a ‘Save As’ not saving to newly selected filename
-   added parsing NID (network id) from channel list
-   fixed Open dialog not showing files without extension (like map-CableD) on Linux
-   fixed Move dialog not showing buttons on Linux
-   added error message quick saving to file location, that is not available anymore
-   added parental lock editing functions

**Version 0.2**

-   name change to *‘SamyGO ChanEdit*‘
-   fix in Sky.de Channel adding function, doesn’t create radio channels anymore
-   possibility to add own channels
-   possibility to edit existing channels
-   save function renamed to ‘Save As’, added another quick save function ‘Save’
-   some fixes in drag n’ drop (no table flickering and scroll position saved after dropping)
-   channel search function added (Ctrl+F)
-   move channel function added
-   cleaned up sources and added annotations
-   reworked channel move code, should produce better results on gaps and supports a target channel number in between selected channels

**Version 0.1**

-   initial release

