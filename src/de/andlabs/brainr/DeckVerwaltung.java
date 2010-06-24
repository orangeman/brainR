package de.andlabs.brainr;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class DeckVerwaltung extends Activity {

    private Gallery mGallery;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        mGallery = new Gallery(this);
        mGallery.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        setContentView(mGallery);
        mGallery.setAdapter(new SpinnerAdapter() {
            
            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void registerDataSetObserver(DataSetObserver observer) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public boolean isEmpty() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean hasStableIds() {
                // TODO Auto-generated method stub
                return true;
            }
            
            @Override
            public int getViewTypeCount() {
                // TODO Auto-generated method stub
                return 1;
            }
            
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.gallery, parent, false);
                } 
                
                ((TextView)convertView.findViewById(R.id.text)).setText("D"+position);
                final String pos = ""+position;
                convertView.setOnFocusChangeListener(new OnFocusChangeListener() {
                    
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        ((ListView)v.findViewById(R.id.gallery)).requestFocus();
                        
                    }
                });
                convertView.setOnKeyListener(new OnKeyListener() {
                    
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return v.findViewById(R.id.gallery).dispatchKeyEvent(event);
                    }
                });
                ((ListView)convertView.findViewById(R.id.gallery)).setAdapter(new ListAdapter() {
                    
                    @Override
                    public void unregisterDataSetObserver(DataSetObserver observer) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void registerDataSetObserver(DataSetObserver observer) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public boolean isEmpty() {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public boolean hasStableIds() {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public int getViewTypeCount() {
                        // TODO Auto-generated method stub
                        return 1;
                    }
                    
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv;
                        if (convertView == null) {
                            tv = new TextView(DeckVerwaltung.this);
                            tv.setTextSize(42);
                        } else 
                            tv = (TextView) convertView;
                    
                        tv.setText(pos+position);
                        
                        return tv;
                    }
                    
                    @Override
                    public int getItemViewType(int position) {
                        // TODO Auto-generated method stub
                        return 1;
                    }
                    
                    @Override
                    public long getItemId(int position) {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                    
                    @Override
                    public Object getItem(int position) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public int getCount() {
                        // TODO Auto-generated method stub
                        return 42;
                    }
                    
                    @Override
                    public boolean isEnabled(int position) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                    
                    @Override
                    public boolean areAllItemsEnabled() {
                        // TODO Auto-generated method stub
                        return true;
                    }
                });
                
                return convertView;
            }
            
            @Override
            public int getItemViewType(int position) {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return 23;
            }
            
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                return null;
            }
        });
        
    }

}
