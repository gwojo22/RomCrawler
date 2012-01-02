package com.t3hh4xx0r.poc;

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

import android.app.ListActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class POCActivity extends ListActivity  {
	String[] mURLURLS;
    private ArrayAdapter<String> linkArrayAdapter;
    ArrayList<String> linkArray;
    Elements links;
    Thread getNameThread;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
 

        getNameThread = new Thread();
        linkArray = new ArrayList<String>();
        mURLURLS = new String[] {"http://rootzwiki.com/topic/12083-unsecured-stock-bootimg402/", "http://rootzwiki.com/topic/12451-rom-android-open-kang-project-toro-build-2012-dec-31/"};	        
		for (int i=0; i<mURLURLS.length; i++) {
	        getListView().addHeaderView(buildHeader(mURLURLS[i]));
	        try {
				getNames(mURLURLS[i]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			getLinks();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cleanList();
    }

	public void getNames(final String mURLURLS) throws IOException, InterruptedException {
		Thread getNameThread = new Thread() {
			public void run() {
				try {
					URL url = new URL(mURLURLS);
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
						linkArray.add(lastWord);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Log.d("POC", "GETNAMETHREAD IS FINISHED!");
		getNameThread.start();
		getNameThread.join();
	} 	
	
	public void getLinks() throws IOException, InterruptedException {
		Thread getLinksThread = new Thread() {
			public void run() {
	       		try {
					getNameThread.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

	    		for (int i=0; i<mURLURLS.length; i++) {
					try {
						Document doc = Jsoup.connect(mURLURLS[i]).get();
	        			Elements links = doc.select("a[href]");
        				for (Element link : links) {
        					for (Iterator<String> c = linkArray.iterator(); c.hasNext();) {
        						String newName = c.next();
		       					if (link.attr("abs:href").contains(newName)) {		        						
		       						c.remove();
		       						//addList.add(link.attr("abs:href").toString());
		       					}
        					}
        					if (link.attr("abs:href").contains(".zip")) {	
       							linkArray.add(link.attr("abs:href"));
       						}
       					}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
            } 
		};
		getLinksThread.start();
		getLinksThread.join();
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cleanList() {
		HashSet hashSet = new HashSet(linkArray);
 	    linkArray = new ArrayList(hashSet);
	    linkArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item,
             R.id.itemName, linkArray);
        setListAdapter(linkArrayAdapter);
	}
    
    private View buildHeader(String mURLURLS) {
    	TextView header = new TextView(this);
    	header.setText(mURLURLS);
    	header.setBackgroundResource(R.drawable.dialog_full_holo_dark);
    	header.setTextColor(getResources().getColor(R.color.ics));
    	return header;
      }
}