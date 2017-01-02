# Circular Progress Bar for android

This repository is a fork from [CircularProgressBar](https://github.com/lopspower/CircularProgressBar) but completely rewritten.

## How to use 

Clone git and import module to your project.

```xml

<view
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            class="com.mikhaellopez.circularprogressbar.CircularProgressBar"
            android:layout_centerVertical="true"
            android:layout_centerHorizontal="true"
            android:layout_marginLeft="150dp"
            app:cpb_background_progressbar_color="#1f394f"
            app:cpb_background_progressbar_width="7dp"
            app:cpb_shadow_width="4dp"
            android:id="@+id/progressBar" />

```

```java

CircularProgressBar progressBar = (CircularProgressBar) view.findViewById(R.id.progressBar);
CircularProgressBar.Progress progressLeft, progressRight;
progressLeft = new CircularProgressBar.Progress(-90, 25, 14, Color.parseColor("#eace18"));
progressRight = new CircularProgressBar.Progress(90, 25, 14, Color.parseColor("#eace18"));

progressBar.addProgress(progressLeft);
progressBar.addProgress(progressRight);
progressBar.setBackgroundColor(Color.parseColor("#3d3d3d"));
progressBar.startWheelMode();

```

## LICENCE

CircularProgressBar by Mohsen Timar is licensed under a Apache License 2.0.