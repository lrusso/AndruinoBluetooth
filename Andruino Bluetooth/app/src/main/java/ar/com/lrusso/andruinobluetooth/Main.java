package ar.com.lrusso.andruinobluetooth;

import android.content.ClipboardManager;
import android.text.Html;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Main extends Activity
	{
	private Context 						context;
	private Activity						activity;

	private Button							senderButton;
	private EditText						senderTextbox;

	public static EditText					receiverTextbox;
	public static ScrollView 				receiverScrollbar;
	
	public static BluetoothUtils			bluetooth = null;

	private boolean connected = false;
	
	@Override protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		senderButton = (Button) findViewById(R.id.senderButton);
		senderTextbox = (EditText) findViewById(R.id.senderTextbox);

		receiverTextbox = (EditText) findViewById(R.id.receiverTextbox);
		receiverScrollbar = (ScrollView) findViewById(R.id.receiverScrollbar);

		context = this;
		activity = this;
		
		senderButton.setTextColor(Color.LTGRAY);
		senderButton.setEnabled(false);
		senderTextbox.setText("");
		senderTextbox.setEnabled(false);

		setTitle(getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.textDisconnected));
		
		senderButton.setOnClickListener(new OnClickListener()
		 	{
		    public void onClick(View v)
		    	{
				try
					{
					if (senderTextbox.length()>0)
						{	
						bluetooth.send(senderTextbox.getText().toString());
						senderTextbox.setText("");
						}
					}
					catch(Exception e)
					{
					}
		    	}
		 	});
		
		senderTextbox.setOnEditorActionListener(new EditText.OnEditorActionListener()
			{
			@Override public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2)
				{
				if (arg1 == EditorInfo.IME_ACTION_SEND)
	        		{
					try
						{
						if (senderTextbox.length()>0)
							{	
							bluetooth.send(senderTextbox.getText().toString());
							senderTextbox.setText("");
							}
						}
						catch(Exception e)
						{
						}
					return true;
	        		}
				return false;
				}
			});
		 
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	    registerReceiver(mReceiver, filter);
		}

	public void onDestroy()
		{
		if (bluetooth!=null)
			{
			try
				{
				bluetooth.disconnect();
				}
				catch(Exception e)
				{
				}
			}
		super.onDestroy();
		}

	@Override public boolean onCreateOptionsMenu(Menu menu)
		{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
		}	

	public boolean onOptionsItemSelected(MenuItem item)
		{
		switch (item.getItemId())
			{
			case R.id.action_settings:
			View menuItemView = findViewById(R.id.action_settings);
			PopupMenu popupMenu = new PopupMenu(this, menuItemView);
			popupMenu.inflate(R.menu.popup_menu);

    		Menu popupMenu2 = popupMenu.getMenu();
    		if (connected==true)
    			{
        	    popupMenu2.findItem(R.id.connect).setVisible(false);
    			}
    			else
    			{
            	popupMenu2.findItem(R.id.disconnect).setVisible(false);
    			}

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{  
				public boolean onMenuItemClick(MenuItem item)
    				{
					if (item.getTitle().toString().contains(getResources().getString(R.string.textConnect)))
						{
						clickInConnect();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textDisconnect)))
						{
						clickInDisconnect();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textClear)))
						{
						clickInClearText();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textCopy)))
						{
						clickInCopyText();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textSketch1)))
						{
	    				clickInSketch1();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textSchematic1)))
						{
						clickInSchematics1();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textSketch2)))
						{
						clickInSketch2();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textSchematic2)))
						{
						clickInSchematics2();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textPrivacy)))
						{
	    				clickInPolicy();
						}
	    			else if (item.getTitle().toString().contains(getResources().getString(R.string.textAbout)))
						{
	    				clickInAbout();
						}
					return true;  
    				}  
				});              
			popupMenu.show();
			return true;
			
			default:
			return super.onOptionsItemSelected(item);
			}
		}

	private void clickInConnect()
		{
		if (bluetoothIsEnabled()==true)
			{
			try
				{
				if (bluetooth==null)
					{
					try
						{
						bluetooth = new BluetoothUtils();
						}
						catch(Exception e)
						{
						}
					}
				final AlertDialog.Builder singlechoicedialog = new AlertDialog.Builder(context);
				final String[] names = bluetooth.getNames();
				singlechoicedialog.setTitle(getResources().getString(R.string.textPairedDevices));
				singlechoicedialog.setItems(names, new DialogInterface.OnClickListener()
					{
					public void onClick(DialogInterface dialog, int item)
						{
						try
							{
							bluetooth.disconnect();
							}
							catch(Exception e)
							{
							}
						
						boolean connectionResult = false;
						try
							{
							connectionResult = bluetooth.connect(item);
							}
							catch(Exception e)
							{
							}

						if (connectionResult==true)
							{
							dialog.cancel();
							bluetooth.setTargetDeviceName(names[item]);
							Toast.makeText(activity, R.string.textConnected, Toast.LENGTH_SHORT).show();
							activity.setTitle(activity.getString(R.string.app_name) + " - " + activity.getResources().getString(R.string.textConnectedTo) + " " + names[item]);
							senderButton.setTextColor(Color.BLACK);
							senderButton.setEnabled(true);
							senderTextbox.setText("");
							senderTextbox.setEnabled(true);
							senderTextbox.requestFocus();
							try
								{
								InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
								}
								catch(Exception e)
								{
								}
							connected = true;
							}
							else
							{
							dialog.cancel();
							bluetooth.setTargetDeviceName("");
							activity.setTitle(activity.getString(R.string.app_name) + " - " + activity.getResources().getString(R.string.textDisconnected));
							senderButton.setTextColor(Color.LTGRAY);
							senderButton.setEnabled(false);
							senderTextbox.setText("");
							senderTextbox.setEnabled(false);
							connected = false;
							try
								{
								bluetooth.disconnect();
								}
								catch(Exception e)
								{
								}
							AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
							alertDialog.setTitle(activity.getResources().getString(R.string.textMessage));
							alertDialog.setMessage(activity.getResources().getString(R.string.textConnectingError));
							alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.textOK),new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}});
							alertDialog.show();
							}
						}
					});
				AlertDialog alert_dialog = singlechoicedialog.create();
				alert_dialog.show();
				}
				catch(Exception e)
				{
				}
			}
			else
			{
			AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
			alertDialog.setTitle(activity.getResources().getString(R.string.textMessage));
			alertDialog.setMessage(activity.getResources().getString(R.string.textBluetoothOff));
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.textOK),new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}});
			alertDialog.show();
			}
		}
		
	private void clickInSketch1()
		{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.sketch1, null);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);  
		alertDialog.setTitle(getResources().getString(R.string.textSketch1));
		alertDialog.setView(view);
		alertDialog.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
			});
		alertDialog.show();
		}

	private void clickInSketch2()
		{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.sketch2, null);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(getResources().getString(R.string.textSketch2));
		alertDialog.setView(view);
		alertDialog.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
			});
		alertDialog.show();
		}

	private void clickInSchematics1()
		{
		Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen); 
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setContentView(R.layout.schematic1);
		dialog.show();
		}

	private void clickInSchematics2()
		{
		Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.schematic2);
		dialog.show();
		}

	private void clickInClearText()
		{
		try
			{
			receiverTextbox.setText("");
			}
			catch(Exception e)
			{
			}
		}
	
	private void clickInCopyText()
		{
		try
			{
			if (receiverTextbox.getText().toString().length()>0)
				{
			    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
			    ClipData clip = ClipData.newPlainText("Data",receiverTextbox.getText().toString());
			    clipboard.setPrimaryClip(clip);
			    Toast.makeText(this,getResources().getString(R.string.textCopyOK),Toast.LENGTH_SHORT).show();
				}
				else
				{
			    Toast.makeText(this,getResources().getString(R.string.textCopyError),Toast.LENGTH_SHORT).show();
				}
		    }
			catch(Exception e)
			{
			}
		}
	
	private void clickInDisconnect()
		{
		try
			{
			Toast.makeText(context, context.getResources().getString(R.string.textDisconnectedToast), Toast.LENGTH_LONG).show();
			setTitle(context.getResources().getString(R.string.app_name) + " - " + context.getResources().getString(R.string.textDisconnected));
			senderButton.setTextColor(Color.LTGRAY);
			senderButton.setEnabled(false);
			senderTextbox.setText("");
			senderTextbox.setEnabled(false);
			}
			catch(Exception e)
			{
			}
		try
			{
			bluetooth.disconnect();
			}
			catch (Exception e)
			{
			}
		try
			{
		    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    View view = activity.getCurrentFocus();
		    if (view == null)
		    	{
		        view = new View(activity);
		    	}
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);			
			}
			catch(Exception e)
			{
			}
		connected = false;
		}

	private void clickInPolicy()
		{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.privacy, null);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);  
		alertDialog.setTitle(getResources().getString(R.string.textPrivacy));  
		alertDialog.setView(view);
		alertDialog.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
			});
		alertDialog.show();
		}
	
	private void clickInAbout()
		{
		String anos = "";
		String valor = getResources().getString(R.string.textAboutMessage);
		int lastTwoDigits = Calendar.getInstance().get(Calendar.YEAR) % 100;
		if (lastTwoDigits<=5)
			{
			anos = "2005";
			}
			else
			{
			anos ="2005 - 20" + String.valueOf(lastTwoDigits).trim();
			}
		
		valor = valor.replace("ANOS", anos);
		
		TextView msg = new TextView(this);
		msg.setText(Html.fromHtml(valor));
		msg.setPadding(10, 20, 10, 25);
		msg.setGravity(Gravity.CENTER);
		float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
		float size = new EditText(this).getTextSize() / scaledDensity;
		msg.setTextSize(size);			

		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.textAbout)).setView(msg).setIcon(R.drawable.ic_launcher).setPositiveButton(getResources().getString(R.string.textOK),new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog,int which)
				{
				}
			}).show();
		}
	
	private boolean bluetoothIsEnabled()
		{
		try
			{
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null)
				{
			    // Device does not support Bluetooth
				return false;
				}
			else
				{
				if (mBluetoothAdapter.isEnabled())
					{
					return true;
					}
					else
					{
					return false;
					}
				}
			}
			catch(Exception e)
			{
			}
		return false;
		}	

	// RECEIVER TO KNOW WHEN THE CONNECTION IS LOST/DISCONNECTED WITH THE BLUETOOTH TARGET DEVICE
	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
		{
	    @Override public void onReceive(Context context, Intent intent)
	    	{
	    	try
	    		{
		        String action = intent.getAction();
		        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
		        	{
		        	if (device.getName().equals(bluetooth.getTargetDeviceName()))
		        		{
		        		if (connected==true)
		        			{
		        			clickInDisconnect();
		        			}
		        		}
		        	}    
	    		}
	    		catch(Exception e)
	    		{
	    		}
	    	}
		};
	}