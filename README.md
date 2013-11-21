#HiG TimeEdit Reader

Play store link: https://play.google.com/store/apps/details?id=com.tobbentm.higreader

Currently in stage: 
* Prod: 1.7.2
* Beta: 1.7.2

![Icon](http://tobbentm.com/ul/HiG-Reader_Icon.png "App Icon")


##Using libraries:

* android-async-http from loopj
* ActionBar-PullToRefresh from chrisbanes
* OpenCSV from opencsv.sourceforge.net

##License:

See [LICENSE](LICENSE) file

##Version log:

See [CHANGELOG.md](CHANGELOG.md) file

##TODO:

* Thread DB stuff
* Hide lectures that are older than today (Might not be needed with autoupdate)
* Parse, save and show if a lecture has been changed in the last 4 days (html tag)
* Comment all the code
* ~~Switch to CSV data source~~
* ~~Check for invalid characters in search term (![a-zA-Z0-9])~~
* ~~Implement pull to refresh on timetable listview~~
* ~~Remove progressbar in timetable, and instead add it to actionbar(?)~~
* ~~Update timetable after user removes a subscription~~
* ~~Add room/lecturer view (separate from timetable)~~
* ~~Create new icon~~
* ~~Autoupdate at intervals and after initial welcome dialog~~
* ~~Remove unneeded permissions (Storage R/W)~~
* ~~Translate to Norwegian~~
* ~~Updating uses too much bandwidth, set intervalls to update~~

##Known bugs:

* Somewhat slow when closing the app, might need some optimizing
* ~~Crashes when searching with invalid URL characters~~
* ~~Would crash when network failed after closing app~~
* ~~Unable to get correct timetable when subscribing to class AND course~~
* ~~ANR when updating, then immediately switching network mode (from mobile to wifi for examle)~~
* ~~Samsung Note unable to properly parse date, causing app to display all dates as "Today"~~
* ~~Unable to parse time correctly between 00.00 and 01.00 at night~~
* ~~ANR when closing app directly after updating~~

##Known problems

* ~~TimeEdit HTML is really bloated, with a least ~1.3k lines. Not much to do about it. Translates to about 250kB for each update.~~
	* ~~Will possibly be fixed with CSV datasource~~

##Screens

![Screenshots](http://tobbentm.com/ul/HiG-Reader_Screens.png "Screenshots")