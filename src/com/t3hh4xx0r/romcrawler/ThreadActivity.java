package com.t3hh4xx0r.romcrawler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.commonsware.cwac.merge.MergeAdapter;

import android.app.ListActivity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ThreadActivity extends ListActivity  {
	String[] mURLURLS;
	private MergeAdapter adapter=null;
	private ArrayAdapter<String> arrayAdapter=null;
    ArrayList<String> linkArray;
    Elements links;
    Thread getNameThread;
    String message;
    MainActivity mainActivity;
    String threadTitle = null;
    ProgressBar pB;
    ListView lv1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle); 
        setContentView(R.layout.main);

        lv1 = (ListView) findViewById(android.R.id.list);
        pB = (ProgressBar) findViewById(R.id.progressBar1);
        
        getNameThread = new Thread();
        linkArray = new ArrayList<String>();
        adapter = new MergeAdapter();
        Bundle extras = getIntent().getExtras();
        try {
        	threadTitle = extras.getString("title");
        } catch (Exception e) {
        	
        }
    	new CreateArrayListTask().execute("poop");


	    BroadcastReceiver receiver = new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	                String action = intent.getAction();
	                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
	                    long downloadId = intent.getLongExtra(
	                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
	                    
	        		 	String ns = Context.NOTIFICATION_SERVICE;
	        		 	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

	        		 	int icon = R.drawable.ic_launcher;        // icon from resources
	        		 	CharSequence tickerText = "Download ready!";              // ticker-text
	        		 	long when = System.currentTimeMillis();         // notification time
	        		 	CharSequence contentTitle = "OMG";  // expanded message title
	        		 	CharSequence contentText = "Your download is finished!";      // expanded message text

	        		 	Intent notificationIntent = new Intent(context, ThreadActivity.class);

	        		 	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	        		 	
	        			Notification notification = new Notification(icon, tickerText, when);
	        			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	        			notification.defaults |= Notification.DEFAULT_VIBRATE;
	        			final int HELLO_ID = 1;
	        			 
	        			mNotificationManager.notify(HELLO_ID, notification);
	                }
	            }
	    };
	    
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
        String linkURL = linkArray.get(position - 1);
        Log.d("POC", linkURL + " " + position);
        String[] bits = linkURL.split("/");
		String zipName = bits[bits.length-1];
		if (zipName.startsWith("download.php?=")) {
			zipName.replace("download.php?=", "");
    	}
        File f = new File(Constants.extSD + "/" + "t3hh4xx0r/romCrawler/" + zipName);
        if (!f.exists()) {
        	f.mkdirs();
        }
        message = linkURL;
        makeToast(message);

        DownloadManager dManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri down = Uri.parse(linkURL);
		DownloadManager.Request req = new DownloadManager.Request(down);
 		req.setShowRunningNotification(true);
 		req.setVisibleInDownloadsUi(true);
 		req.setDestinationUri(Uri.fromFile(f));
 		dManager.enqueue(req);
    }
    
    private ArrayAdapter<String> buildList(ArrayList<String> linkArray) {
        return(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, linkArray));
      }
    
    private class CreateArrayListTask extends AsyncTask<String, Void, ArrayList<String>> {               
		@Override
		protected ArrayList<String> doInBackground(String... params) {
			try {
			        linkArray = new ArrayList<String>();
					URL url = new URL(Constants.THREADURL);
					URLConnection con = url.openConnection();
					Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
					Matcher m = p.matcher(con.getContentType());
					String charset = m.matches() ? m.group(1) : "ISO-8859-1";
					Reader r = new InputStreamReader(con.getInputStream(), charset);
					StringBuilder buf = new StringBuilder();
					while (true) {
						int ch = r.read();
						if (ch < 0)
							break;
						buf.append((char) ch);
					}
					String strMessy = buf.toString();
					String strClean = new String (strMessy.replaceAll("\\<.*?>",""));
					while (strClean.contains(".zip")){
						String trimmed = strClean.substring(0, strClean.lastIndexOf(".zip"));
						String[] parts = trimmed.split("\n");
						String lastWord = parts[parts.length - 1] + ".zip";
						strClean = new String(strClean.replace(lastWord, ""));
						if (!lastWord.contains(" ")) {
							linkArray.add(lastWord);
						}					}
					Document doc = Jsoup.connect(Constants.THREADURL).get();
        			Elements links = doc.select("a[href]");
    				for (Element link : links) {
    					for (Iterator<String> c = linkArray.iterator(); c.hasNext();) {
    						String newName = c.next();
	       					if (link.attr("abs:href").contains(newName)) {		        						
	       						c.remove();
	       					}
    					}
    					if (link.attr("abs:href").contains(".zip")) {	
   							linkArray.add(link.attr("abs:href"));
   						}
   					}
    				if (linkArray.isEmpty()) {
    					String warning = "No direct links detected. Please have your rom dev switch to dev-host.org for their hosting.\nIts free and provides direct links to all downloads.";
    					linkArray.add(warning);
    				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			return linkArray;
		}
			
		       protected void onPostExecute(ArrayList<String> linkArray) {
		    	    setUI();
		    	    createUI();
		        	pB.setVisibility(View.GONE);        	
		        }
	} 	
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setUI() {
		HashSet hashSet = new HashSet(linkArray);
 	    linkArray = new ArrayList(hashSet);
        arrayAdapter = buildList(linkArray);
        adapter.addView(buildHeader(threadTitle));
        adapter.addAdapter(arrayAdapter);
	}
	
	public void createUI() {
        setListAdapter(adapter);
	}
    
    private View buildHeader(String mURLURLS) {
    	TextView header = new TextView(this);
    	header.setText(mURLURLS);
    	header.setBackgroundResource(R.drawable.dialog_full_holo_dark);
    	header.setTextColor(getResources().getColor(R.color.ics));
    	return header;
      }
    
	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}