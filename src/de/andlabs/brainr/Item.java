package de.andlabs.brainr;

import android.net.Uri;

public class Item {
	
	
	static final String AUTHORITY = "de.andlabs.brainr";
	
	static final String TYPE = "vnd.android.cursor.dir/vnd.andlabs.brainr.items";
	
	static final Uri URI = Uri.parse("content://" + AUTHORITY + "/items");
	
	
	
	static final String TEXT = "text";

}
