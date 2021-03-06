package com.zykmanhua.app.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import com.zykmanhua.app.bean.ManhuaPicture;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetChapterContent {
	
	private Context mContext = null;
	private Handler mHandler = null;
	
	public GetChapterContent(Context context , Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	
	public void getChapterContent(final String name , final int id) {
		Parameters parameters = new Parameters();
		parameters.add(Config.KEY, Config.APP_KEY);
		parameters.add(Config.KEY_COMICNAME, name);
		parameters.add(Config.KEY_ID, id);
		
		JuheData.executeWithAPI(
				mContext, 
				Config.APP_ID, 
				Config.URL_MANHUA_CONTENT , 
				JuheData.GET, 
				parameters, 
				new DataCallBack() {
					@Override
					public void onSuccess(int statusCode, String responseString) {
						if(statusCode == Config.STATUS_CODE_SUCCESS) {
							ArrayList<ManhuaPicture> manhuaPictureList = parseJSON(responseString);
							if(manhuaPictureList != null && mHandler != null) {
								Message msg = Message.obtain(mHandler , Config.RESULT_SUCCESS_CODE , manhuaPictureList);
								msg.sendToTarget();
							}
						}
						else {
							Message msg = Message.obtain(mHandler , Config.RESULT_FAIL_CODE , statusCode);
							msg.sendToTarget();
						}
					}
					
					@Override
					public void onFinish() {
						
					}
					
					@Override
					public void onFailure(int statusCode, String responseString, Throwable throwable) {
						Message msg = Message.obtain(mHandler, Config.RESULT_FAIL_CODE, statusCode + ":" + throwable.getMessage());
						msg.sendToTarget();
					}
				});
		
	}
	
	
	
	/**
	 */
	private ArrayList<ManhuaPicture> parseJSON(String responseString) {
		
		ArrayList<ManhuaPicture> manhuaPictureList = null;
		
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			int code = jsonObject.getInt(Config.JSON_error_code);
			
			if(code == 200) {
				manhuaPictureList = new ArrayList<ManhuaPicture>();
				JSONArray jsonArray = jsonObject.getJSONObject(Config.JSON_result).getJSONArray(Config.JSON_imageList);
				int arrLength = jsonArray.length();
				
				for(int i = 0 ; i < arrLength ; i++) {
					JSONObject dataElement = jsonArray.getJSONObject(i);
					ManhuaPicture manhuaPicture = new ManhuaPicture();
					manhuaPicture.setmName(jsonObject.getJSONObject(Config.JSON_result).getString(Config.JSON_comicName));
					manhuaPicture.setmChapterId(jsonObject.getJSONObject(Config.JSON_result).getInt(Config.JSON_chapterId));
					manhuaPicture.setmImageUrl(dataElement.getString(Config.JSON_imageUrl));
					manhuaPicture.setmId(dataElement.getInt(Config.JSON_id));
					manhuaPictureList.add(manhuaPicture);
				}
				return manhuaPictureList;
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

}
