package com.dpkm95.maze.activity;

import java.util.ArrayList;
import java.util.List;

import com.dpkm95.maze.R;
import com.dpkm95.maze.bluetooth.BluetoothChatService;
import com.dpkm95.maze.bluetooth.BluetoothEncoderDecoder;
import com.dpkm95.maze.bluetooth.BluetoothMediator;
import com.dpkm95.maze.bluetooth.MazeArbiter;
import com.dpkm95.maze.utils.Archiver;
import com.dpkm95.maze.utils.MazeConstants;
import com.dpkm95.maze.utils.MazeGenerator;
import com.dpkm95.maze.view.LaunchView;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	LaunchView lv;	
	private UiLifecycleHelper uiHelper;
	
	private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Local Bluetooth adapter
	public BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	public BluetoothChatService mChatService = null;

	int[][] mOwnMaze;
	int[][] mOppMaze;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MA","create");
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (mBluetoothAdapter == null) {
	    	MazeConstants.NO_BLUETOOTH = true; 						
		}
	    
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float width = size.x;
		float height = size.y;
		lv = new LaunchView(this, width, height);
		lv.setBackgroundColor(Color.WHITE);
		setContentView(lv);
	}	

	@Override
	protected void onResume() {
	  super.onResume();

	  // Logs 'install' and 'app activate' App Events.
	  AppEventsLogger.activateApp(this);
	  uiHelper.onResume();
	  
	  if(mBluetoothAdapter.isEnabled()){
		  	stopService(new Intent(this, BluetoothChatService.class));
		  	startService(new Intent(this, BluetoothChatService.class));
		  	
		  	if (mChatService != null) {
				mChatService.setHandler(mHandler);// to ensure handler changes in
													// other activities hasn't
													// affected this
				// Only if the state is STATE_NONE, do we know that we haven't
				// started already
				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
					// Start the Bluetooth chat services
					mChatService.start();
				}
			}
	  	}		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
	  super.onPause();

	  // Logs 'app deactivate' App Event.
	  AppEventsLogger.deactivateApp(this);
	  uiHelper.onPause();	  
	}
	
	public void onStop(){
		super.onStop();
		if(mBluetoothAdapter.isEnabled()){
			Intent i = new Intent(this, BluetoothChatService.class);
			stopService(i);
		}		
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	public void shareFacebookDialog(){
		String linkString = "https://developers.facebook.com/android";
		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this).setLink(linkString)
	    		.setApplicationName("maze")
	            .setLink("https://www.google.co.in")
	            .setName("Maze Challenge!")
	            .setDescription("My top score: "+Archiver.get_top_score(this)+"\nBeat that!").build();
	    uiHelper.trackPendingDialogCall(shareDialog.present());
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success!");
	        }
	    });	 
	}

	public void shareFb() {
		Intent shareIntent = new Intent("android.intent.action.SEND");
		   shareIntent.setType("text/plain");
		   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
		   shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, R.drawable.ic_launcher);

		   PackageManager pm = this.getPackageManager();
		   List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		     for (final ResolveInfo app : activityList) 
		     {
		         if ((app.activityInfo.name).contains("facebook")) 
		         {
		           final ActivityInfo activity = app.activityInfo;
		           final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		          shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		          shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		          shareIntent.setComponent(name);
		          startActivity(shareIntent);
		          break;
		        }
		      }
		
	}
	
	public void generateOwnMaze() {
		MazeGenerator mg;
		if (MazeConstants.SIZE) {
		mg = new MazeGenerator(16,10);
		} else {
		mg = new MazeGenerator(12,8);
		}
		mOwnMaze = mg.getMaze();
	}
	
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send

		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			mChatService.write(message.getBytes());
		}
	}
	
	private void sendOwnMaze() {
		sendMessage(BluetoothEncoderDecoder.encodeMaze(mOwnMaze));
	}

	private void startGame(int[][] maze1, int[][] maze2) {
		Intent i = new Intent(this, FlexibleMazeActivity.class);

		int[][] chosenMaze = MazeArbiter.getMaze(maze1, maze2);
		i.putExtra(FlexibleMazeActivity.KEY_MAZE,
				BluetoothEncoderDecoder.encodeMaze(chosenMaze));

		BluetoothMediator.mChatService = mChatService;

		startActivity(i);
	}

	/**
	 * Expected format - delimiter used is ':', first token represents
	 * 
	 * @param message
	 *            contains encoded maze as described in BluetoothEncoderDecoder
	 * @return
	 */
	public int[][] getOpponentMaze(String message) {
		return BluetoothEncoderDecoder.decodeMaze(message);
	}
	
	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					Toast.makeText(getApplicationContext(),getString(R.string.title_connected_to,
									mConnectedDeviceName), Toast.LENGTH_SHORT)
							.show();
					sendOwnMaze();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					Toast.makeText(
							getApplicationContext(),
							getString(R.string.title_connecting,
									mConnectedDeviceName), Toast.LENGTH_SHORT)
							.show();
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					Toast.makeText(
							getApplicationContext(),
							getString(R.string.title_not_connected,
									mConnectedDeviceName), Toast.LENGTH_SHORT)
							.show();
					break;
				}
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				Log.d("receivedString", readMessage);
				try {
					mOppMaze = getOpponentMaze(readMessage);
				} catch (Exception e) {
					break;
				}
				startGame(mOwnMaze, mOppMaze);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				break;
			case MESSAGE_TOAST:
				Log.e("MESSAGE_TOAST", "ConnectActivity Handler");
				Toast.makeText(getApplicationContext(),
						"mt:\n" + msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	public void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
	
	public void enableBluetooth() {
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}		
		generateOwnMaze();
	}
	
	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device);
	}
	
	private void setupChat() {
		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);
	}

	public void requestConnectDevice() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		
	}
		
}
