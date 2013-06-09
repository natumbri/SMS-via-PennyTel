package com.natumbri.pennytelsms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.natumbri.pennytelsms.R;
import com.natumbri.pennytelsms.pennytelsms_about;
import com.natumbri.pennytelsms.pennytelsms_help;
import com.natumbri.pennytelsms.pennytelsms_settings;
import com.natumbri.pennytelsms.R.id;

public class pennytelsms extends Activity{
	public static final String SETTINGS_NAME = "PennyTelSMS-settings";
	private static final int CONTACT_PICKER_RESULT = 1001;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                     
        // "Send" Button
        Button send_button = (Button)findViewById(id.send_button);
        send_button.setOnClickListener(new OnClickListener() {
    		
    		public void onClick(View arg0) {
    			// Send message in background
    			HTTP_sms_sendsms sendThread = new HTTP_sms_sendsms();
    			sendThread.execute();
    		}
    	});
        
        // "Clear" Button 
        ((Button)findViewById(id.cancel_button)).setOnClickListener(new OnClickListener() {
    		
    		public void onClick(View arg0) {
    			// Clear the text in both text boxes
    			((EditText) findViewById(id.to_textbox)).setText("");
    			((EditText) findViewById(id.message_textbox)).setText("");
    		}
    		});
        
        // "Exit" Button 
        ((Button)findViewById(id.exit_button)).setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			// Exit the app
    			finish();
    		}
    		});
        
        // "Add Contact" Button 
        ((Button)findViewById(id.add_contact_button)).setOnClickListener(new OnClickListener() {
    		public void onClick(View arg0) {
    			Intent contactPickerIntent = new Intent(Intent.ACTION_PICK);
    			contactPickerIntent.setType(Phone.CONTENT_TYPE);
    		    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    		}
    		});
        
        // Show length on changing the message text
        // Also called when loaded to set to zero.
        ((EditText)findViewById(id.message_textbox)).addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			public void afterTextChanged(Editable s) {
				int length = s.length();
				int text_colour = Color.WHITE;
				
				// Display number of characters left
				((TextView)findViewById(id.message_textcount)).setText((160 - length) + " left");
				
				//Sets colour of text
				if(length < 140)
				{
					text_colour = Color.GREEN;
				}
				else if(length < 150)
				{
					text_colour = Color.YELLOW;
				}
				else if(length < 160)
				{
					text_colour = Color.rgb(254, 127, 1);
				}
				else if(length >= 160)
				{
					text_colour = Color.RED;
				}
				((TextView)findViewById(id.message_textcount)).setTextColor(text_colour);
			}
		});
        
        SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
        SharedPreferences.Editor settings_editor = settings.edit();
        if(settings.getBoolean("firstrun", true))
        {
        	new AlertDialog.Builder(pennytelsms.this).setTitle(R.string.firstrun_title).setMessage(R.string.firstrun).setPositiveButton("OK", null).show();
        	settings_editor.putBoolean("firstrun", false);
        	settings_editor.commit();
        }
        
        if (null == savedInstanceState) {
            processIntentData(getIntent());
        }
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem mi_settings = menu.findItem(R.id.settingsbutton);
        mi_settings.setIntent(new Intent(this, pennytelsms_settings.class));
        
        MenuItem mi_about = menu.findItem(R.id.about_button);
        mi_about.setIntent(new Intent(this, pennytelsms_about.class));
        
        MenuItem mi_help = menu.findItem(R.id.help_button);
        mi_help.setIntent(new Intent(this, pennytelsms_help.class));
        
        return true;
    }
    
    // After user picks contact 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        	Cursor cursor = null;
            String phone_num = "";
            switch (requestCode) {
            case CONTACT_PICKER_RESULT:
                // handle contact results
            	try {
                    Uri result = data.getData();

                    // get the contact id from the Uri
                    String id = result.getLastPathSegment();

                    // query for everything phone
                    cursor = getContentResolver().query(
                    		Phone.CONTENT_URI,
                    		null,
                    		Phone._ID + " = ?",
                    		new String[] { id },
                    		null);

                    int phonenumIdx = cursor.getColumnIndex(Phone.NUMBER);

                    // let's just get the first phone number
                    if (cursor.moveToFirst()) {
                        phone_num = cursor.getString(phonenumIdx);
                    } else {
                    }
                } catch (Exception e) {
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    EditText phonenumEntry = (EditText) findViewById(R.id.to_textbox);
                    
                    
                    //Code below is for multiple phone numbers, which PennyTel doesn't do
                    
                    //Check if there is a comma at the end
                    //Pattern pattern = Pattern.compile(",.*$");
                    //Matcher matcher = pattern.matcher(phonenumEntry.getText().toString());
                    //Pattern pattern_empty = Pattern.compile("^( |,)*$");
                    //Matcher matcher_empty = pattern_empty.matcher(phonenumEntry.getText().toString());
                    
                    // Add comma if not present (and if not empty
                    //if(!(matcher.matches()) || (matcher_empty.matches()))
                    //{
                    //	phonenumEntry.append(", ");
                    //}
                    //
                    //if(matcher_empty.matches())
                    //{
                    	SharedPreferences settings = getSharedPreferences(SETTINGS_NAME, 0);
                    	if(settings.getBoolean("fix_phone", false))
                    	{
                    		// Format phone number correctly
                    		phone_num = fix_phone(phone_num);
                    	}

                        // Put number into field
                    	phonenumEntry.setText(phone_num);
                    	
                    //}
                    //else
                    //{
                    //	phonenumEntry.append(phone_num);
                    	phonenumEntry = (EditText) findViewById(R.id.to_textbox);     //}
                    
                    if (phone_num.length() == 0) {
                        Toast.makeText(this, "Error getting phone number for contact.",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

        } else {
            // gracefully handle failure
        	Toast.makeText(this, "No phone number added", Toast.LENGTH_SHORT).show();
        }
    }
    
    private class HTTP_sms_sendsms extends AsyncTask<Void, Void, Integer>
    {
    	// Set up vars for sending SMS
    	private String URL_username = "";
    	private String URL_password = "";
    	private String URL_numbers = "";
    	private String URL_subscription = "";
    	private String URL_message = "";
        
    	// Other Vars
    	HttpURLConnection sms_request = null;
        int req;
        URL SMS_url = null;
        String sms_result = "";
        ProgressDialog pd;

    	protected void onPreExecute()
    	{
    		pd = ProgressDialog.show(pennytelsms.this , "Sending...", "Sending", false, true);
    		
    		// Get preferences - Method not thread safe
    		SharedPreferences settings = getSharedPreferences(pennytelsms.SETTINGS_NAME, 0);
    		
    		try{
    			// Extract SMS info from settings or from fields in View. 
            	URL_username = URLEncoder.encode(settings.getString("user_number", ""), "UTF-8");
            	URL_password = URLEncoder.encode(settings.getString("user_password", ""), "UTF-8");
            	URL_numbers = URLEncoder.encode((fix_phone(((EditText) findViewById(id.to_textbox)).getText().toString())), "UTF-8");
            	URL_subscription = URLEncoder.encode(settings.getString("user_smssubid", ""), "UTF-8");
            	URL_message = URLEncoder.encode(((EditText) findViewById(id.message_textbox)).getText().toString(), "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
    	}
    	
    	@Override
    	protected Integer doInBackground(Void... v) {
    		try {
				SMS_url = new URL("https://www.pennytel.com.au/sms?user=" + URL_username + "&password=" + URL_password + "&sender=" + URL_subscription + "&destination=" + URL_numbers + "&text=" + URL_message);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			
	        try{
	        sms_request = (HttpURLConnection) SMS_url.openConnection();
			sms_request.setDoInput(true);
			sms_request.connect();
				req = ((HttpURLConnection)sms_request).getResponseCode();
	        } catch (IOException e) {
	        	// May indicate 401 (Unauthorised) error.
				req = 401;
			}
	        
	        return req;
    	}
    	
    	protected void onPostExecute(Integer req)
		{
			pd.dismiss();
			
			switch (req) {
			case 200:
				sms_result = "Message Queued for Delivery";
				
				ContentValues values = new ContentValues(); 
	            
				values.put("address", ((EditText) findViewById(id.to_textbox)).getText().toString()); 
			    values.put("body", ((EditText) findViewById(id.message_textbox)).getText().toString()); 
			    getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				
				break;
				
			case 401:
				sms_result = "SMS failed to send. Please check settings (MENU => Settings) to ensure they are correct.";
				break;
				
			case 500:
				sms_result = "SMS failed to send. Ensure there is a message to send.";
					break;
					
			case 407:
			default:
				sms_result = "SMS failed to send because of an unknown error. Please check you have internet access, and all details (including MENU => Settings) are correct.";
				break;
			}
				
			new AlertDialog.Builder(pennytelsms.this).setMessage(sms_result).setPositiveButton("Close", null).show();
		}

    }

    public void show_msg_length(Editable s)
    {
    	int length = s.length();
		int text_colour = Color.WHITE;
		
		// Display number of characters left
		((TextView)findViewById(id.message_textbox)).setText((160 - length) + " left");
		
		//Sets colour of text
		if(length < 110)
		{
			text_colour = Color.GREEN;
		}
		else if(length < 135)
		{
			text_colour = Color.YELLOW;
		}
		else if(length < 155)
		{
			text_colour = Color.rgb(255, 127, 0);
		}
		else if(length >= 160)
		{
			text_colour = Color.GREEN;
		}
		((TextView)findViewById(id.message_textbox)).setTextColor(text_colour);
    }
    
    public String fix_phone(String old_phone)
    {
    	
    	String new_phone = old_phone.replaceAll("\\D","");
    	
		// Remove the 0 from the start of a recipient's number and replace with country code (61 for Australia).
			
    	if (new_phone.charAt(0) == '0') {
			new_phone = "61" + new_phone.substring(1);
		}

    	return new_phone;
    }
   
    private void processIntentData(Intent intent)
    {
    	    	
    	if (null == intent) return;

        if (Intent.ACTION_SENDTO.equals(intent.getAction())) {
            //in the data i'll find the number of the destination
            String destionationNumber = intent.getDataString();
            try {
				destionationNumber = URLDecoder.decode(destionationNumber, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}            
            
            //clear the string
            destionationNumber = fix_phone(destionationNumber);
            
            //and set fields
            ((EditText) findViewById(R.id.to_textbox)).setText(destionationNumber);

        } 
    }
}
