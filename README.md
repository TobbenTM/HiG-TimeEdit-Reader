#HiG TimeEdit Reader

Play store link: https://play.google.com/store/apps/details?id=com.tobbentm.higreader

Currently in stage: 
* Prod: 1.8.1
* Beta: 1.8.1

![Icon](http://tobbentm.com/ul/HiG-Reader_Icon.png "App Icon")


##Using libraries:

* android-async-http from loopj
* ActionBar-PullToRefresh from chrisbanes
* OpenCSV from opencsv.sourceforge.net

##Porting

Want to port this to your institution of choice?

2 Options:

* Use my [TimeEdit Reader](https://play.google.com/store/apps/details?id=com.tobbentm.timeeditreader) app, which currently supports 45 different institutions.
* See the [PORTING.md](PORTING.md) file.

##License:

See [LICENSE](LICENSE) file

##Version log:

See [CHANGELOG.md](CHANGELOG.md) file

##TODO:

* Create PORTING.md guide to porting this app
* Make the homescreen widget able to fetch timetable from timeedit
* Update screenshots with new sexy dialogs
* Need message for when there are no upcoming lectures withing the timeframe
* Merge all search fragments into one
* Comment all the code
* ~~Thread DB stuff / optimise lecture listview~~
* ~~Abstract lecture updating~~

##Known bugs:

* Somewhat slow when closing the app, might need some optimizing

##Screens

![Screenshots](http://tobbentm.com/ul/HiG-Reader_Screens.png "Screenshots")