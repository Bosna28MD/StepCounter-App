# Step-Counter Mobile App

## Description
This is an mobile app created with Android Studio(it's the official integrated development environment (IDE) for Google's Android operating system).
This app contains 4 functionality:
1. **StepCounter** which monitorize how many steps user is making everyday
2. **Friend-List** is a way which users can create friends relationships with other users
3. **LeaderBoard** is a system which print for every week the users who are making the most steps
4. **Calendar-Events** is a system where users can schedule an event to make together for example running together...

As languages for creating this app I used Android Studio with Java programming language.
As service from Firebase I used:
1. Firebase Authentication
2. Firebase Realtime Database
3. Firebase Cloud Messaging
4. Firebase Cloud Functions

This app is composed of two Activity when user is logged-in and when is logged-out; <br>
When user is in activity logged-out he has two page that can access Register and Log-in. <br>
When user is log-in in the bottom of the aplication is printed a bottom navigation view with capacity to acces all pages with all functionality, this pages are: <br>
- "Home-Page" where is printed a Step-Counter functionality with number of steps made by the userr daily and also a report of steps from the last days;
- "Friend List Page" where is formated from 3 pages(fragments) first page is a Friend List(all users which you have a friend relationship),
  Friend-Add (page which print users which you don't have a friend relationship) and Friend-Request(print users which send to you a friend request);
- "LeaderBoard Page" which is formated of two pages "LeaderBoard Registration" in this page you register for the next week to participate in leaderboard
  and "LeaderBoard" which prints and Leaderboard with users who made the most of the steps from this week;
- "Calendar Events Page" is formated of 3 pages "Calendar Events" page where print all events scheduled already,
  "Request ScheduleEvent" in this page print all request received from other users to do an event together and
  "Schedule Event" this page is accesed from Friend List page if user have a relationship with another user and in this page you can create a request of an event;
- "UserInfo Page" is page where show the information of current user logged-in and exists functionality of log-out. 

## Prerequisites:
- Android Studio IDE installed 
- Firebase project created


### Prerequisites for Firebase Function Service:
- Generate a service key from Firebase service acounts from Settings
- Rename that .jso key file generated to ```server-account.json```
- Insert that service acount key into ```FirebaseCloudFunction_API1_FP3/functions``` and ```FirebaseCloudFunction_API2_FP3/functions```
- Deploy this firebase folders into firebase function service


