package de.yehoudie.android.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import de.yehoudie.android.flashlight.R;

public class Main extends Activity
{
	private static final String TAG = "Main";

	public static final String FLASH_LIGHT = "com.example.flashlight.Flashlight";
	public static final String SCREEN_BRIGHTNESS = "com.example.flashlight.Screenbrightness";

	private Context context;
	private ImageButton flash_light_btn;
	private Camera cam;

	private boolean has_flash_light;
	private boolean is_switched_on;
	private Parameters p;
	private String light_type;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle(R.string.app_name);

		context = getBaseContext();
		has_flash_light = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		Log.d(TAG, "onCreate: has_flash_light: " + has_flash_light);
		if ( has_flash_light )
		{
			flash_light_btn = (ImageButton) findViewById(R.id.flash_light_btn);
			initCam();
			createListeners();
		}
		else
		{
			Toast.makeText(context, getResources().getString(R.string.no_flash_light), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onStart()
	{
		Log.d(TAG, "onStart: cam: " + cam + ", has_flash_light: " + has_flash_light);
		super.onStart();

		initCam();

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		light_type = sharedPrefs.getString("lightPref", FLASH_LIGHT);
		Log.d(TAG, "Main: use " + light_type);
	}

	@SuppressWarnings("deprecation")
	private void initCam()
	{
		Log.d(TAG, "initCam: cam: " + cam + ", has_flash_light: " + has_flash_light);
		if ( cam != null || !has_flash_light ) return;
		Log.d(TAG, "initCam");
		cam = Camera.open();
		p = cam.getParameters();
	}

	private void createListeners()
	{
		Log.d(TAG, "createListeners");
		// if ( has_flash_light )
		{
			flash_light_btn.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					toggleFlashLight();
				}
			});
		}

	}

	private void toggleFlashLight()
	{
		Log.d(TAG, "toggleFlashLight: is_switched_on" + is_switched_on);
		if ( is_switched_on )
		{
			switchOff();
		}
		else
		{
			switchOn();
		}
	}

	@SuppressWarnings("deprecation")
	private void switchOff()
	{
		Log.d(TAG, "switchOff: cam: " + cam + ", has_flash_light: " + has_flash_light);
		p.setFlashMode(Parameters.FLASH_MODE_OFF);
		cam.setParameters(p);
		// cam.stopPreview();
		is_switched_on = false;
		// flash_light_btn.setText(getResources().getString(R.string.flash_btn_off));
		// flash_light_btn.setTextAppearance(context, R.style.flash_btn_off);
//		flash_light_btn.setBackgroundResource(R.color.button_bg_off);
		flash_light_btn.setImageResource(R.drawable.ic_power_off);
		
	}

	@SuppressWarnings("deprecation")
	private void switchOn()
	{
		Log.d(TAG, "switchOn: cam: " + cam + ", has_flash_light: " + has_flash_light);
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		cam.setParameters(p);
		// cam.startPreview();
		// flash_light_btn.setText(getResources().getString(R.string.flash_btn_on));
		// flash_light_btn.setTextAppearance(context, R.style.flash_btn_on);
//		flash_light_btn.setBackgroundResource(R.color.button_bg_on);
		flash_light_btn.setImageResource(R.drawable.ic_power_on);
		is_switched_on = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// / Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.action_settings:
				Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
				startActivity(settingsActivity);
				return true;

			case R.id.action_close:
				closeApp();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private void closeApp()
	{
		Log.d(TAG, "closeApp: cam: " + cam + ", has_flash_light: " + has_flash_light);
		// releaseCam();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void releaseCam()
	{
		Log.d(TAG, "releaseCam: cam: " + cam + ", has_flash_light: " + has_flash_light);
		if ( cam == null ) return;

		switchOff();
		cam.release();
		cam = null;
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, "onStop: cam: " + cam + ", has_flash_light: " + has_flash_light);
		releaseCam();
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		Log.d(TAG, "onDestroy: cam: " + cam + ", has_flash_light: " + has_flash_light);
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState: is_switched_on: " + is_switched_on);
		outState.putBoolean("is_switched_on", is_switched_on);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		Log.d(TAG, "onRestoreInstanceState: is_switched_on: " + is_switched_on);
		if ( savedInstanceState == null ) return;
		
		is_switched_on = savedInstanceState.getBoolean("is_switched_on", false);
//		Log.d(TAG, "onRestoreInstanceState: savedInstanceState(is_switched_on): " + is_switched_on);
		if ( is_switched_on ) switchOn();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
//		Log.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if ( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
//			Log.d(TAG, "onConfigurationChanged: landscape");
//			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		}
		else if ( newConfig.orientation == Configuration.ORIENTATION_PORTRAIT )
		{
//			Log.d(TAG, "onConfigurationChanged: portrait");
//			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}
}
