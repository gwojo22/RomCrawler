package com.t3hh4xx0r.romcrawler;

import java.io.File;

import android.os.Build;
import android.os.Environment;

public class Constants {

	public static File extSD = Environment.getExternalStorageDirectory();

	public static String DEVICE = null;
	public static String FORUM = null;
	public static String THREADURL = null;
	public static String THREADTITLE = null;	
	public static String REQUEST = android.os.Build.DEVICE.toUpperCase() + "\n\nPaste link for your devices rootzwiki and xda development subforums below.";
}
