#HiG TimeEdit Reader

Play store link: https://play.google.com/store/apps/details?id=com.tobbentm.higreader

Currently in stage: beta


##Using libraries:

* android-async-http from loopj

##License:

See LICENSE file

##TODO:

* Colorize listviews either per subscription or per course
* Implement swipe to unsubscribe from individual courses
* Implement pull to refresh on timetable listview
* ~~Create new icon~~
* ~~Autoupdate at intervals and after initial welcome dialog~~
* ~~Remove unneeded permissions (Storage R/W)~~
* ~~Translate to Norwegian~~
* Hide lectures that are older than today (Might not be needed with autoupdate)
* Update timetable after user removes a subscription 
* Adjust progressbar in timetable
* ~~Updating uses too much bandwidth, set intervalls to update~~
* Parse, save and show if a lecture has been changed in the last 4 days (html tag)

##Known bugs:

* ~~Samsung Note unable to properly parse date, causing app to display all dates as "Today"~~
* Somewhat slow when closing the app, might need some optimizing
* ~~Unable to parse time correctly between 00.00 and 01.00 at night~~
* ~~ANR when closing app directly after updating~~

##Known problems

* TimeEdit HTML is really bloated, with a least ~1.3k lines. Not much to do about it. Translates to about 250kB for each update.