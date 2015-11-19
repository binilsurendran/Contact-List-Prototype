package com.polus.binil.androidlistviewwithsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	// List view
	private ListView lv;
	
	// Listview Adapter
	//ArrayAdapter<String> adapter;
	
	// Search EditText
	EditText inputSearch;
	
	// ArrayList for Listview
	ArrayList<HashMap<String, String>> productList;
	ArrayList<ItemListPogo> arraylist = new ArrayList<ItemListPogo>();
	ListViewAdapter adapter;

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        String name[] ;
        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        
        
        // Adding items to listview
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        int count = cursor.getCount();
        Log.e("Tag ", "tootal count " + count);

        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String nameContact, number;
                while (cursor.moveToNext()) {
                    nameContact = cursor.getString(nameIndex);
                    // itemsData.add(new ItemData(name));
                    ItemListPogo wp = new ItemListPogo( nameContact );
                    arraylist.add(wp);

                }
            } finally {
                cursor.close();
            }
        }

        adapter = new ListViewAdapter(MainActivity.this, arraylist);
        lv.setAdapter(adapter);
        
        
   
        inputSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				
				String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
				adapter.filter(text);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub							
			}
		});
    }


    public  void  addContacts(View v)
    {
        Intent i = new Intent(MainActivity.this,AddContacts.class);
        finish();
        startActivity(i);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Tag ","onResume");
    }


}
