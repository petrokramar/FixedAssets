package com.example.fixedassets;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

	DBHelper helper;
	EditText editNumber;
	TextView textCard;
	
	public static final String APP_PREFERENCES = "mysettings";
	public static final String APP_PREFERENCE_NUMBER = "Number";
	public static final String APP_PREFERENCE_CARD = "Card";
	SharedPreferences settings;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        editNumber = (EditText) findViewById(R.id.editNumber);
        textCard = (TextView) findViewById(R.id.textCard);
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (settings.contains(APP_PREFERENCE_NUMBER)) {
			editNumber.setText(settings.getString(APP_PREFERENCE_NUMBER, ""));
		}
        if (settings.contains(APP_PREFERENCE_CARD)) {
			textCard.setText(settings.getString(APP_PREFERENCE_CARD, ""));
		}
    }


    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(APP_PREFERENCE_CARD, textCard.getText().toString());
        editor.putString(APP_PREFERENCE_NUMBER, editNumber.getText().toString());
        editor.commit();
        super.onStop();
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
		case R.id.action_settings:
        	Intent intent = new Intent(this, Import.class);
        	startActivity(intent);
			break;
			case R.id.about:
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(getString(R.string.app_name))
						.setMessage(getString(R.string.author))
						.setIcon(R.drawable.ic_launcher)
						.setCancelable(false)
						.setPositiveButton("ОК",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			break;
		default:
			break;
		}
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
		case R.id.buttonFind:
			find();
			break;
		case R.id.buttonScan:
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		break;
	}
    	
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanningResult != null) {
			String result = scanningResult.getContents().replaceFirst("^0*", "");
			int length = result.length();
			result = result.substring(0, length-1);
			editNumber.setText(result);
			find();
		}
	}


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		textCard.setText(savedInstanceState.getString("textCard"));
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("textCard", textCard.getText().toString());
	}
	
	public void find(){
		if (! editNumber.getText().toString().trim().equals("")) {
			Asset asset;
			try {
				asset = helper.getAsset(editNumber.getText().toString().trim());
				if (asset != null) {
					textCard.setText("Инв. номер: " + asset.getNumber() + "\n" 
							+ "Код: " + asset.getCode() + "\n"
							+ "Наименование: " + asset.getName() + "\n"
							+ "Подразделение: " + asset.getDepartment() + "\n"
							+ "МОЛ: " + asset.getPerson());
				}else{
					textCard.setText("Основное средство отсутствует");
				}
			} catch (Exception e) {
				textCard.setText("Ошибка в инвентарном номере");
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}
}
