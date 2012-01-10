package com.t3hh4xx0r.romcrawler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.pontiflex.mobile.webview.sdk.AdManagerFactory;
import com.pontiflex.mobile.webview.sdk.IAdConfig;
import com.pontiflex.mobile.webview.sdk.IAdManager;
import com.pontiflex.mobile.webview.sdk.IAdManager.RegistrationMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
    ArrayList<String> threadArray;
    ArrayList<String> authorArray;
    String message;
    String threadTitle = null;  
    ProgressBar pB;
    ListView lv1;
    private IAdManager adManager;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        adManager = AdManagerFactory.createInstance(getApplication());
        
        showAdAfterLaunchCount(3);
        lv1 = (ListView) findViewById(android.R.id.list);
        pB = (ProgressBar) findViewById(R.id.progressBar1);
        threadArray = new ArrayList<String>();
        authorArray = new ArrayList<String>();

        getDevice();
        
        lv1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
             Object o = lv1.getItemAtPosition(position);
             TitleResults fullObject = (TitleResults)o;
             threadTitle = fullObject.getItemName();
             Constants.THREADURL = threadArray.get(position);
             Intent intent = new Intent(MainActivity.this, ThreadActivity.class);
             Bundle b = new Bundle();
             b.putString("title", threadTitle);
             intent.putExtras(b);
             startActivity(intent);    	
            }  
           });
        
        if(Constants.DEVICE != null){
        	new CreateArrayListTask().execute(Constants.FORUM);

            }
        }
    
	private void showAdAfterLaunchCount(int count) {
		IAdConfig adConfig = adManager.getAdConfig();
		adConfig.setWithRegistration(false);
		adConfig.setLaunchInterval(count);
		adConfig.setRegistrationMode(RegistrationMode.RegistrationAfterIntervalInLaunches);
		adManager.showAd(adConfig);
	}
	
    public void getDevice() {
    	String device = android.os.Build.DEVICE.toUpperCase();
    	try {
    		DeviceType type = Enum.valueOf(DeviceType.class, device);
    		Constants.DEVICE = type.name();
    		Constants.FORUM = type.getForumUrl();
    	} catch (IllegalArgumentException e) {
    		message = "Device not found/supported!";
        	makeToast(message);        	
          		AlertDialog.Builder builder = new AlertDialog.Builder(this);
          		builder.setTitle("Request Support");
          		builder.setMessage("Send developer information about your device.\nYou will need to copy and paste your devices rootzwiki and xda development subforums into the message.")
          		   .setCancelable(false)
          		   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
                 	       	 String email = "r2doesinc@gmail.com";
                             Intent i = new Intent(Intent.ACTION_SEND);
                             i.setType("text/plain");
                             i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                             i.putExtra(Intent.EXTRA_SUBJECT, "Rom Crawler submission.");
                             i.putExtra(Intent.EXTRA_TEXT, Constants.REQUEST);
                             startActivity(i);        	 
          		       }
          		   })
          		   .setNegativeButton("No thank you", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
          		            dialog.cancel();
          		       }
          		   });
          		AlertDialog alert = builder.create();
          		alert.show();
    	}
    }
    
    private class CreateArrayListTask extends AsyncTask<String, Void, ArrayList<TitleResults>> {       
        final ArrayList<TitleResults> results = new ArrayList<TitleResults>();
        
		@Override
		protected ArrayList<TitleResults> doInBackground(String... params) {
	        TitleResults titleArray =  new TitleResults();
			StringBuilder whole = new StringBuilder();

			try {
				URL url = new URL(
						Constants.FORUM);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				try {
					BufferedReader in = new BufferedReader(
						new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
					String inputLine;
					while ((inputLine = in.readLine()) != null)
						whole.append(inputLine);
					in.close();
				} catch (IOException e) {}
				finally {
					urlConnection.disconnect();
				}
			} catch (Exception e) {}
			Document doc = Parser.parse(whole.toString().replaceAll("<!-- end ad tag -->?.*?<!-- BEGIN TOPICS -->", ""), Constants.FORUM);
			Elements threads = doc.select(".topic_title");
	       	Elements authors = doc.select("a[hovercard-ref]");
      		for (Element author : authors) {
       			authorArray.add(author.text());
      		}
      		cleanAuthors();
       		for (Element thread : threads) {
       			titleArray =  new TitleResults();

       			titleArray.setAuthorDate(authorArray.get(0));
       			authorArray.remove(0);
       			
       			//Thread title
       			threadTitle = thread.text();
       			titleArray.setItemName(threadTitle);
	       		
       			//Thread link
       			String threadStr = thread.attr("abs:href");
       			String endTag = "/page__view__getnewpost"; //trim link
       			threadStr = new String(threadStr.replace(endTag, ""));
       			threadArray.add(threadStr);
				results.add(titleArray);
       		}
 			return results;
		}
		
        @Override
        protected void onPostExecute(ArrayList<TitleResults> results) {
        	pB.setVisibility(View.GONE);        	
            lv1.setAdapter(new TitleAdapter(MainActivity.this, results)); 
        }
    }
    

    public void cleanAuthors() {
        ArrayList<String> tmpArray = new ArrayList<String>();
		for (int i=0; i<authorArray.size(); i++) {
	        tmpArray.add(authorArray.get(i));
		}
		authorArray = new ArrayList<String>();
		for (int i=0; i<tmpArray.size(); i += 2) {
			authorArray.add(tmpArray.get(i));
		}
    }
    
	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuinflate = new MenuInflater(this);
		menuinflate.inflate(R.menu.main_menu, menu);
		return true;
	}	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.upgrade:
          		AlertDialog.Builder builder = new AlertDialog.Builder(this);
          		builder.setTitle("Upgrade to premium");
          		builder.setMessage("Premium users get constant updates, ad free services, and many new features.\nUpgrade to premium today and get\n-Access to XDA crawling.\n-Access to custom url crawling, with custom url database that allows you to save as many as you want.\n-Access to screenshot viewer.\n-Long press on thread to open in browser.")
          		   .setCancelable(false)
          		   .setPositiveButton("Lets check it out", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
          					Intent marketApp = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.t3hh4xx0r.romcrawlerpremium"));
          					marketApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
          		 			try{
          						startActivity(marketApp);
          					}catch(Exception e){
          						e.printStackTrace();
          					}        	 
          		       }
          		   })
          		   .setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
          		       public void onClick(DialogInterface dialog, int id) {
          		            dialog.cancel();
          		       }
          		   });
          		AlertDialog alert = builder.create();
          		alert.show();
	        break;
	        case R.id.apps:
	        	AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	        	builder2.setTitle("Other apps from r2DoesInc");
	        	final ArrayList<String> appArray = new ArrayList<String>();
	        	appArray.add("gMusic Sniper");
	        	CharSequence[] cs = appArray.toArray(new CharSequence[appArray.size()]);

	        	builder2.setItems(cs, new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int pos) {
	        	    	String app = appArray.get(pos);
	        	    	if (app.equals("gMusic Sniper")) {
          					Intent marketApp = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.t3hh4xx0r.gmusicsniper"));
          					marketApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      						startActivity(marketApp);
	        	    	} else {
	        	    		Log.d("POC", app);
	        	    	}
	        	    }
	        	});
	        	AlertDialog alert2 = builder2.create();
	        	alert2.show();
	        break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		return false;
	}	
}
