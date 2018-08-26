package ar.com.lrusso.andruinobluetooth;

import android.text.Html;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{  
				public boolean onMenuItemClick(MenuItem item)
    				{
					if (item.getTitle().toString().contains(getResources().getString(R.string.textConnect)))
						{
						clickInConnect();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textSketch)))
						{
	    				clickInSketch();
						}
					else if (item.getTitle().toString().contains(getResources().getString(R.string.textSchematic)))
						{
						clickInSchematics();
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
		
	private void clickInSketch()
		{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view=inflater.inflate(R.layout.sketch, null);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);  
		alertDialog.setTitle(getResources().getString(R.string.textSketch));  
		alertDialog.setView(view);
		alertDialog.setPositiveButton(getResources().getString(R.string.textOK), new DialogInterface.OnClickListener()
			{
			public void onClick(DialogInterface dialog, int whichButton)
				{
				}
			});
		alertDialog.show();
		}
	
	private void clickInSchematics()
		{
		Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen); 
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setContentView(R.layout.schematic); 
		dialog.show();
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
		        		try
		    				{
		        			Toast.makeText(context, context.getResources().getString(R.string.textDisconnected), Toast.LENGTH_LONG).show();
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
		        		}
		        	}    
	    		}
	    		catch(Exception e)
	    		{
	    		}
	    	}
		};
	}