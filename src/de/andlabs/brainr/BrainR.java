package de.andlabs.brainr;


import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class BrainR extends Activity {
    private static final String TAG = "BrainR";
    private static final int MENU_ITEM_INSERT = 1;
    private static final int MENU_ITEM_DELETE = 2;
    private CardsView mCardsView;
    private CursorTreeAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Item.URI);
        }
        
        mCardsView = new CardsView(this);
        setContentView(mCardsView);
        
        mAdapter = new CursorTreeAdapter(getContentResolver().query(Item.URI, 
                                new String[]{"_id", "text"}, null, null, null), this) {
            
            @Override
            protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
                TextView v = new TextView(context);
                v.setBackgroundColor(Color.BLACK);
                v.setTextColor(Color.GRAY);
                v.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                v.setText("hui");
                return v;
            }
            
            @Override
            protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
                Log.d(TAG, cursor.getString(1));
                ((TextView)view).setText(cursor.getString(1));
                scaleToFit((TextView) view);
            }
            
            
            @Override
            protected Cursor getChildrenCursor(Cursor groupCursor) {
                return getContentResolver().query(Uri.withAppendedPath(Item.URI, "/"+groupCursor.getInt(0)+"/translations"), 
                                            new String[]{"items._id", "items.text", "items.sound"}, null, null, null);
            }
            
            @Override
            protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
                TextView v = new TextView(context);
                v.setBackgroundColor(Color.BLACK);
                v.setTextColor(Color.LTGRAY);
                v.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                return v;
            }
            
            @Override
            protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
                ((TextView)view).setText(cursor.getString(1));
                scaleToFit((TextView) view);
            }
        };
        mCardsView.setAdapter(mAdapter);
//        registerForContextMenu(mCardsView);
        mCardsView.setOnCreateContextMenuListener(this);
    }
    
    private void scaleToFit(TextView view) {
        float factor = (getWindowManager().getDefaultDisplay().getWidth()-42) / view.getPaint().measureText(view.getText().toString());
        view.setTextSize(view.getTextSize()*factor);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_insert)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete)
        .setIcon(android.R.drawable.ic_menu_delete);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        case MENU_ITEM_DELETE:
            getContentResolver().delete(ContentUris.withAppendedId(Item.URI, 
                                        mAdapter.getGroupId(mCardsView.mGroupPosition)), null, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) mAdapter.getGroup(info.position);
        if (cursor == null) {
            return;
        }

        menu.setHeaderTitle(cursor.getString(1));
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                getContentResolver().delete(ContentUris.withAppendedId(getIntent().getData(), info.id), null, null);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mCardsView.onTouchEvent(event);
    }
    
}