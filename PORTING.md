# Porting

To get this to work for other institutions a few things have to be changed:

## Rebranding

Obviously a simple rebranding should be done, changing text and such. Remember to adhere to the license.

## URLs

In the file [Network.java](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/java/com/tobbentm/higreader/Network.java) you will have to alter the baseURL to fit your institution. The part that needs to be altered is the part before /r.csv. This will be the url you see when viewing the TimeEdit website. It usually ends in /public, /student, /schema or something like that. You also need to change the sid argument. You can find the correct sid either via the page source or via sniffing. 

## Element types

In the file(s) [strings.xml](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/res/values/strings.xml) you will want to update the search_array items to match your TimeEdit format. The order matters. Next go back to the [Network.java](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/java/com/tobbentm/higreader/Network.java) and change the element IDs in search(). The easiest way to find these IDs is via TimeEdit search page source. 

## CSV Layout

Finally, the hardest part, changing the CSV reading. In the file [TimeParser.java](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/java/com/tobbentm/higreader/TimeParser.java) you will have to change the columns to read from in timetable(). The columns for date and time seems to consistently be date-starttime-enddate-endtime. Thereby setting date to line[0], starttime to line[1] and endtime to line[3]. The rest varies greatly from school to school. Easiest is probably intercepting or downloading the csv and using the column names or example data.

## Misc.

In [MainActivity.java](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/java/com/tobbentm/higreader/MainActivity.java) there are some things that should be removed, like openReservationsURL(), which is unique to HiG. 

In [TimeTableFragment.java](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/java/com/tobbentm/higreader/TimeTableFragment.java) and [ViewFragment.java](https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/master/java/com/tobbentm/higreader/ViewFragment.java) I have used some nasty workarounds for HiG in the updateLectures() function where I append to 'id' the string ",-1,1.182". This may not work for your school, and it may fuck up the entire timetable.

## fin

If anyone does actually try to port it, feel free to let us know if these intructions are enough or if you have any questions!