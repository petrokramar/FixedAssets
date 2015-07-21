package com.example.fixedassets;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Import extends Activity {

	private static final String TAG = "MyLog";
	
	
	ProgressBar pb;
	TextView filePath;
	TextView textProgress;
	DBHelper dbHelper;
	String textType = "";
	Button buttonImport;
	ImageButton buttonOpenFile;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);
	
		pb = (ProgressBar) findViewById(R.id.pbDownload);
		filePath = (TextView) findViewById(R.id.filePath);
		textProgress = (TextView) findViewById(R.id.textProgress);
		buttonImport = (Button) findViewById(R.id.buttonImport);
		buttonOpenFile = (ImageButton) findViewById(R.id.buttonOpenFile);
		dbHelper = new DBHelper(this);	

		
	}

	public void onClick(View v){

		int id = v.getId();
		switch (id) {
		case R.id.buttonImport:
			if (filePath.getText().toString().trim() != "") {
				TaskParse task = new TaskParse();
				task.execute();
			} else {
               	Toast.makeText(getApplicationContext(), "Не выбран файл", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.buttonOpenFile:
			OpenFileDialog fileDialog = new OpenFileDialog(this)
            .setFilter(".*\\.xml")
            .setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                @Override
                public void OnSelectedFile(String fileName) {
                	filePath.setText(fileName);
                }
            });
			fileDialog.show();
			break;
		default:
			break;
		}
	}
	
	class TaskParse extends AsyncTask<Void, Integer, Void>{

		int totalObjects;
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			pb.setProgress(100 * values[0] / totalObjects);
			textProgress.setText(textType + "" + values[0] + "/" + totalObjects);
		}

		@Override
		protected Void doInBackground(Void... params) {

			SQLiteDatabase db = dbHelper.getWritableDatabase();	
			
			if (db != null) {
		    	db.execSQL("drop table if exists assets");
		        db.execSQL("drop table if exists departments");
		        db.execSQL("drop table if exists persons");
		        dbHelper.onCreate(db);
			}
			
			
			/*
			db.beginTransaction();
			try {
				db.delete("departments", null, null);
				db.delete("persons", null, null);
				db.delete("assets", null, null);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			*/
			
			ContentValues cv = new ContentValues();
			
			int currentObject = 0;
			
			try {
				
				XmlPullParserFactory factory;
				factory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = factory.newPullParser();
//				File file = new File(Environment.getExternalStorageDirectory()+ "/os.xml");
				File file = new File(filePath.getText().toString());
				FileInputStream fis;
				fis = new FileInputStream(file);
				parser.setInput(new InputStreamReader(fis, "cp1251"));	
				while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
					if (parser.getEventType() == XmlPullParser.START_TAG) {
						if (parser.getName().equals("МОЛ")) {
							cv.put("code", parser.getAttributeValue(0));
						    cv.put("name", parser.getAttributeValue(1));
						    long rowID = db.insert("persons", null, cv);
						    publishProgress(currentObject++,2);
						    Log.d(TAG, "row persons inserted, ID = " + rowID);							
						}else{if (parser.getName().equals("Подразделение")) {
							cv.put("code", parser.getAttributeValue(0));
						    cv.put("name", parser.getAttributeValue(1));
						    long rowID = db.insert("departments", null, cv);
						    publishProgress(currentObject++,1);
						    Log.d(TAG, "row departments inserted, ID = " + rowID);							
						} else {if (parser.getName().equals("ОсновноеСредство")) {
							cv.put("code", parser.getAttributeValue(0));
						    cv.put("name", parser.getAttributeValue(1));
						    cv.put("number", parser.getAttributeValue(2));
							cv.put("code_department", parser.getAttributeValue(3));
							cv.put("code_person", parser.getAttributeValue(4));
						    long rowID = db.insert("assets", null, cv);
						    publishProgress(currentObject++,3);
						    Log.d(TAG, "row assets inserted, ID = " + rowID);							
						} else{if (parser.getName().equals("КоличествоПодразделения")) {
							totalObjects = Integer.parseInt(parser.getAttributeValue(0));
							currentObject = 0;
							textType = "Подразделения: "; 
						}else{if (parser.getName().equals("КоличествоМОЛ")) {
							totalObjects = Integer.parseInt(parser.getAttributeValue(0));
							currentObject = 0;
							textType = "МОЛ: "; 
						}else{if (parser.getName().equals("КоличествоОС")) {
							totalObjects = Integer.parseInt(parser.getAttributeValue(0));
							currentObject = 0;
							textType = "ОС: "; 
						}
						}
						}
						}
						}
						}
						
					}
				    parser.next();
				}
				
				/*
				try {
				      // открываем поток для чтения
				      BufferedReader br = new BufferedReader(new FileReader(file));
				      String str = "";
				      // читаем содержимое
				      int i = 0;
				      while ((str = br.readLine()) != null) {
				        Log.d(TAG, ""+str.charAt(0)+str.charAt(1)+str.charAt(2)+str.charAt(3)
				        		+str.charAt(4)+str.charAt(5)+str.charAt(6)+str.charAt(7));
				      }
				    } catch (FileNotFoundException e) {
				      e.printStackTrace();
				    } catch (IOException e) {
				      e.printStackTrace();
				    }
				
				
				/*
				
				Log.d(TAG, "Имя файла = " + file.getName());
				int fileLength = (int) file.length();
				Log.d(TAG, "Длина файла = " + fileLength);
				Log.d(TAG, "Тип события = " + parser.getEventType());
					
					
					try {
				      // открываем поток для чтения
				      BufferedReader br = new BufferedReader(new FileReader(file));
				      String str = "";
				      // читаем содержимое
				      int i = 0;
				      while ((str = br.readLine()) != null) {
						publishProgress( ++i * 100 / fileLength);
				        Log.d(TAG, ""+i);
//				        Log.d(TAG, ""+fileLength);
//				        Log.d(TAG, ""+(i * 100 / fileLength));
				        Log.d(TAG, ""+str.charAt(0));
				        Log.d(TAG, ""+str.charAt(1));
				        Log.d(TAG, ""+str.charAt(2));
				        Log.d(TAG, ""+str.charAt(3));
				        Log.d(TAG, ""+str.charAt(4));
				      }
				    } catch (FileNotFoundException e) {
				      e.printStackTrace();
				    } catch (IOException e) {
				      e.printStackTrace();
				    }
				 */
				
				
				
				/*
				XmlPullParser parser = getResources().getXml(R.xml.contacts);	

				while (parser.getEventType()!= XmlPullParser.END_DOCUMENT) {
					if (parser.getEventType() == XmlPullParser.START_TAG
			            && parser.getName().equals("contact")) {
						Log.d(TAG, parser.getAttributeValue(0));
						Log.d(TAG, parser.getAttributeValue(1));
						Log.d(TAG, parser.getAttributeValue(2));
					}
				    parser.next();
				}
				/*



				/*
				while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
					Log.d(TAG, "текст = " + parser.getText());
					String tmp = "";

					switch (parser.getEventType()) {
					case XmlPullParser.START_DOCUMENT:
						Log.d(TAG, "Начало документа");
						break;
					// начало тэга
					case XmlPullParser.START_TAG:
						Log.d(TAG, "Start");
						Log.d(TAG,
								"START_TAG: имя тега = " + parser.getName()
										+ ", уровень = " + parser.getDepth()
										+ ", число атрибутов = "
										+ parser.getAttributeCount());
						tmp = "";
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							tmp = tmp + parser.getAttributeName(i) + " = "
									+ parser.getAttributeValue(i) + ", ";
						}
						if (!TextUtils.isEmpty(tmp))
							Log.d(TAG, "Атрибуты: " + tmp);
						break;
					// конец тега
					case XmlPullParser.END_TAG:
						Log.d(TAG, "END_TAG: имя тега = " + parser.getName());
						break;
					// содержимое тега
					case XmlPullParser.TEXT:
						Log.d(TAG, "текст = " + parser.getText());
						break;

					default:
						break;
					}
					Log.d(TAG, "Конец строки");
					parser.next();
				}
								*/		


			} catch (Throwable t) {
				Log.e(TAG, "Ошибка при загрузке XML-документа: " + t.toString());

			}finally{
				pb.setProgress(0);
				textProgress.setText("");
				if (db != null) {
					db.close();
				}
			}
			
			
			return null;
			// TODO Auto-generated method stub
		}

		@Override
		protected void onPreExecute() {
			/*
			Toast.makeText(Import.this, Environment.getExternalStorageDirectory()+ "/os.xml", Toast.LENGTH_SHORT).show();
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(Import.this, filePath.getText().toString(), Toast.LENGTH_SHORT).show();
			*/
			buttonImport.setEnabled(false);
			buttonOpenFile.setEnabled(false);
		}

		@Override
		protected void onPostExecute(Void result) {
			textProgress.setText("");
			buttonImport.setEnabled(true);
			buttonOpenFile.setEnabled(true);
		}
		
	}

	
}
