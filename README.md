#VAST 2012 Challenge MC1 Entry by giCentre, City University London
#### Award: “Efficient Use of Visualization”

Description
-----------

See http://gicentre.org/vast2012/

Licence
-------
Application source code is released under under GNU Lesser General Public License.
See COPYING.LESSER included in `src/` or http://www.gnu.org/licenses/

Installation
------------
The easiest way to install the application is to compiled version as an archive from http://gicentre.org/vast2012/ and run `bomnetworkstatus.bat` or `bomnetworkstatus.sh` depending on your operating system (JRE 1.6 is required). It is recommended to launch the app on a machine having least 2GB of RAM.

Alternatively, you can try to download the source code and rebuild the app. This implies the following steps:

* Clone git repository to a new directory.
* Download missing `data/facilitystatus.tab` file, which is excluded form the repository due to its large size (≈270MB). See `data/facilitystatus.tab.readme` for details.
* Download dependent libraries and add them to build path:
    * __giCentre Utilities__ http://gicentre.org/utils/
    * __Processing__ http://processing.org/
    * __Postgres JDBC__ http://jdbc.postgresql.org/
* Run `org.gicentre.vast2012.bomnetworkstatus.BOMNetworkStatusApp` as Java Application with `-Xmx2048M` in the list of Java VM arguments.

**Note**: Regardless of the installation method, it will not be possible to see individual machine details in the right column — such feature requires local Postgres database (≈15GB). However, the application can still work with some limitations only using csv files  with aggregated statistics in `data/` directory. Full dump of the database, which is different from the one given as challenge input data, is available upon request (≈1.4GB).

Changelog
---------

__2012-06-14__

* Colours are now loaded from config
* Improvements in overall view and snapshot view


__2012-06-01__

* Loading of the stats is now done in a separate thread, progress can be seen.
* Stats are now available for 8 more machine groups, total is now 12 (all machines in a facility, 3x machine classes, 8x machine functions (excl. atms-null). The app consumes more memory, around 1.3G when just loaded. Size of source files is optimised using less space-consuming types when exporting from Postgres db + removing redundant information that can be calculated on fly from other numbers (e.g. overall count of machines with AF = x = sum of machines with different machine functions). This significantly increases parsing performance.
* Mouse interaction improvements: Clicking on a bar in machine details box selects a corresponding machine group, clicking on a number in PS / AF / conn areas changes the view of the grid.
* Tooltips are added when rolling over numbers in the facility status bar.
* Fixes for facility sorting: now PS and AF are being sorted by percentage, not absolute values
* Changes in some keyboard shortcuts


__2012-05-31 (2)__

* Machine details are now zoomable and sortable by IP, PS, AF, connection count.
* Counts for machines in each column are displayed when rolling over them with the mouse. If only a particular machine group is currently selected (M+0-4), details of machines not in this group become semitransparent, not completely removed.

__2012-05-31 (1)__

* Grid can now be sorted geographically (g+2, g+3)

__2012-05-30__

* Initial release

    