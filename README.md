# User Routine Manager Android Application

User Routine Manager Android App developed as last project for mobile college course.

![Application Routine In Course](https://raw.githubusercontent.com/davikawasaki/user-routine-app/master/img/08_user_routine_app_demo_routine_in_course.png)

## TECHNOLOGIES & LIBRARIES

1\. [Android Gradle 3.0.0](https://docs.gradle.org/3.0/release-notes.html)

2\. [App Compact v7](https://developer.android.com/topic/libraries/support-library/features.html)

3\. [Material Design for Android](https://developer.android.com/design/material/index.html)

4\. [ORMLite 5.0](http://ormlite.com/)

5\. [Android Studio IDE](https://developer.android.com/studio/index.html)

## PROJECT STRUCTURE

The basic project structure, with some hidden files, is as follows:

``` bash
.
|-app/ # src files
  |-build/
  |
  |-src/ # database conection classes
    |-androidTest/ # just basic files (not unit tested)
    |-main/
      |-java/
        |-[package]/config # database start/upgrade main config
        |-[package]/fragments # date/time pickers as fragments
        |-[package]/model # application domain files
          | Place.java
          | PlaceType.java
          | Routine.java
        |-[package]/services # service layer for each appication domain
        |-[package]/utils # other service layer with utilitary methods
        | AboutActivity.java
        | MainActivity.java
        | PlaceListActivity.java
        | PlaceTypeListActivity.java
        | RegisterPlaceActivity.java
        | RegisterPlaceTypeActivity.java
        | RegisterRoutineActivity.java
        | RoutineListActivity.java
      |-res/ # resource files
        |-drawable # icon and other vector/image files
        |-layout # application layouts
        |-menu # application menus
        |-values # strings, arrays in portuguese
          | arrays.xml # basic place types
          | colors.xml # theme colors
          | dimens.xml
          | strings.xml # strings used in app
          | styles.xml
        |-values-en # strings, arrays in english
      | AndroidManifest.xml # app main config doc
  |
|-build /
|-gradle/ # graddle wrapper
|-img/ # application screens
```

The model domain relationships is as below:

1\. Routine-Place: 1-1 (with two types of places - origin and destination)

2\. Place-PlaceType: 1-1

## REFERENCES

[Pickers](https://developer.android.com/guide/topics/ui/controls/pickers.html)

[Fragments](https://developer.android.com/guide/components/fragments.html)

## AUTHORS

This work was developed to Mobile undergrad-subject final project. The people involved in the project are:

Student: KAWASAKI, Davi // davishinjik [at] gmail.com

Professor: FEITOSA, Alexandre Rômolo Moreira // alexandrefeitosa [at] utfpr.edu.br

## CONTACT & FEEDBACKS

Feel free to contact or pull request me to any relevant updates you may enquire:

KAWASAKI, Davi // davishinjik [at] gmail.com
