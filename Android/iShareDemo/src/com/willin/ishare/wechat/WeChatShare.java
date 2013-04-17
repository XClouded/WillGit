package com.willin.ishare.wechat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.willin.utils.Util;

import com.willin.ishare.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.willin.ishare.base.IDoShare;



///

/*
 *  WeChat open API 
 */

///

public class WeChatShare implements 
	IDoShare,
	IWXAPIEventHandler{

	// WeChat Timeline supported version
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	// the APPID you register from official Website.
	private static final String APP_ID = "";
	
	// the API core
	private IWXAPI api;
		
	//
	private boolean isTimelineCb = false;
	
	private static final int THUMB_SIZE = 150;
	

	//================================================================================================================//
	
	
	private void regToWeChat( Context c ){
		
		api = WXAPIFactory.createWXAPI( c, APP_ID, true);
		api.registerApp( APP_ID );
		
	}
	
	
	// constructor
	public WeChatShare( Context c ){		
		regToWeChat( c );
	}
	
	
	
	// destroy
	public void destory(){
		api.unregisterApp();
	}
	


	@Override
	public void onReq(BaseReq req) {

		switch (req.getType()) {
		
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
					
			break;
			
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			
			break;
			
		case ConstantsAPI.COMMAND_SENDAUTH:
			break;
			
		case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
			break;
			
		case ConstantsAPI.COMMAND_UNKNOWN:
			break;
			
		default:
			break;
		}
		
	}


	@Override
	public void onResp(BaseResp resp) {
		
		int result = 0;
		String errorStr = resp.errStr;
		
		switch (resp.errCode) {
		
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
			
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
			
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
			
		default:
			result = R.string.errcode_unknown;
			break;
		}
		
	}
	
	
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	
	
	private int getScene() {
		return isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
	}
	
	
	private String getTransaction(Bundle bundle) {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}
	
	
	public void sendText(String text) {
		
		assert( text != null );
		
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = getScene();
		
		// 调用api接口发送数据到微信
		api.sendReq(req);
		
	}
	
	
	
	public void sendImage(Bitmap bmp) {
		
		assert( bmp != null );
		
		WXImageObject imgObj = new WXImageObject(bmp);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);		
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);  // 设置缩略图

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendImageInFile(String bitmapPath) {
		
		assert( bitmapPath != null );
		
		WXImageObject imgObj = new WXImageObject();
		imgObj.setImagePath(bitmapPath);
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		
		Bitmap thumbBmp = Util.extractThumbNail(bitmapPath, THUMB_SIZE, THUMB_SIZE, true);
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendImageInURL(String url) {
		
		assert( url != null );
		
		try {
			
			WXImageObject imgObj = new WXImageObject();
			imgObj.imageUrl = url;
			
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imgObj;
	
			Bitmap bmp;		
			bmp = BitmapFactory.decodeStream(new URL(url).openStream());
	
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
			bmp.recycle();
			msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("img");
			req.message = msg;
			req.scene = getScene();
			api.sendReq(req);
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void sendMusic(String musicUrl, String musicLowBandUrl, String title, String description, String bitmapPath ) {
		
		assert( musicUrl != null );
		assert( musicLowBandUrl != null );
		assert( title != null );
		
		WXMusicObject music = new WXMusicObject();
		music.musicUrl=musicUrl;
		music.musicLowBandUrl = musicLowBandUrl;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = music;
		msg.title = title;
		msg.description = description;


		if ( bitmapPath != null ) {
			Bitmap thumbBmp = Util.extractThumbNail(bitmapPath, THUMB_SIZE, THUMB_SIZE, true);
			msg.thumbData = Util.bmpToByteArray(thumbBmp, true);	
		}
		

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("music");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendVideo(String videoUrl, String videoLowBandUrl, String title, String description, String bitmapPath ) {
		
		assert( videoUrl != null );
		assert( videoLowBandUrl != null );
		assert( title != null );
		
		WXVideoObject video = new WXVideoObject();
		video.videoUrl = videoUrl;
		video.videoLowBandUrl = videoLowBandUrl;

		WXMediaMessage msg = new WXMediaMessage(video);
		msg.title = title;
		msg.description = description;
		
		if ( bitmapPath != null ) {
			Bitmap thumbBmp = Util.extractThumbNail(bitmapPath, THUMB_SIZE, THUMB_SIZE, true);
			msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("video");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendWebPage(String webUrl, String title, String description, String bitmapPath ) {
		
		assert( webUrl != null );
		assert( title != null );
		
		WXWebpageObject webpage = new WXWebpageObject();		
		webpage.webpageUrl = webUrl;
		
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = description;
		
		if ( bitmapPath != null ) {
			Bitmap thumbBmp = Util.extractThumbNail(bitmapPath, THUMB_SIZE, THUMB_SIZE, true);
			msg.thumbData = Util.bmpToByteArray(thumbBmp, true);	
		}
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendExtendData(String attachmentPath, String title, String description, String bitmapPath, String extInfo ) {
		
		assert( attachmentPath != null );
		assert( title != null );
		
		final WXAppExtendObject appdata = new WXAppExtendObject();
		appdata.fileData = Util.readFromFile(attachmentPath, 0, -1);
		appdata.extInfo = extInfo;

		final WXMediaMessage msg = new WXMediaMessage();
		
		if ( bitmapPath != null ) {
			msg.setThumbImage(Util.extractThumbNail(bitmapPath, THUMB_SIZE, THUMB_SIZE, true));
		}
		
		msg.title = title;
		msg.description = description;
		msg.mediaObject = appdata;
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("appdata");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendEmoji(String emojiPath, String title, String description){
		
		assert( emojiPath != null );
		assert( title != null );
		
		WXEmojiObject emoji = new WXEmojiObject();
		emoji.emojiPath = emojiPath;
		
		WXMediaMessage msg = new WXMediaMessage(emoji);
		msg.title = title;
		msg.description = description;

		Bitmap thumbBmp = Util.extractThumbNail(emojiPath, THUMB_SIZE, THUMB_SIZE, true);
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("emoji");
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
		
	}
	
	
	
	public void sendAuthor(String scope, String state) {
		
		assert( scope != null );
		assert( state != null );
		
		// send oauth request
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = scope;
		req.state = state;
		api.sendReq(req);
		
	}
	
	
	
	public void replyMessage(Bundle bundle, String text) {
		
		assert( bundle != null );
		assert( text != null );
		
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage(textObj);
		msg.description = text;
		
		// 构造一个Resp
		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		// 将req的transaction设置到resp对象中，其中bundle为微信传递过来的intent所带的内容，通过getExtras方法获取
		resp.transaction = getTransaction( bundle );
		resp.message = msg;
		
		// 调用api接口响应数据到微信
		api.sendResp(resp);
		
	}
	
	
	
	public void replyImage(Bundle bundle, Bitmap bmp) {
		
		assert( bundle != null );
		assert( bmp != null );
		
		// respond with image message
		WXImageObject imgObj = new WXImageObject(bmp);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		// 设置消息的缩略图
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
		bmp.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		resp.transaction = getTransaction(bundle);
		resp.message = msg;
		
		api.sendResp(resp);
		
	}
	
	
	
	public void replyMusic(Bundle bundle, String musicUrl, String title, String description, String imgPath) {
		
		assert( bundle != null );
		assert( musicUrl != null );
		assert( title != null );
		
		WXMusicObject music = new WXMusicObject();
		music.musicUrl = musicUrl;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = music;
		msg.title = title;
		msg.description = description;

		if ( imgPath != null ) {
			Bitmap thumbBmp = Util.extractThumbNail(imgPath, THUMB_SIZE, THUMB_SIZE, true);
			msg.thumbData = Util.bmpToByteArray(thumbBmp, true);	
		}

		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		resp.transaction = getTransaction(bundle);
		resp.message = msg;
		
		api.sendResp(resp);
	}
	
	
	
	public void replyVideo(Bundle bundle, String videoUrl, String title, String description) {
		
		assert( bundle != null );
		assert( videoUrl != null );
		assert( title != null );
		
		WXVideoObject video = new WXVideoObject();
		video.videoUrl = videoUrl;

		WXMediaMessage msg = new WXMediaMessage(video);
		msg.title = title;
		msg.description = description;

		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		resp.transaction = getTransaction(bundle);
		resp.message = msg;
		
		api.sendResp(resp);
		
	}
	
	
	
	public void replyWebPage(Bundle bundle, String webpageUrl, String title, String description) {
		
		assert( bundle != null );
		assert( webpageUrl != null );
		assert( title != null );
		
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = webpageUrl;

		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = description;

		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		resp.transaction = getTransaction(bundle);
		resp.message = msg;
		
		api.sendResp(resp);
		
	}
	
	
	
	
	
	
	//==================================================================================================================//
	
}


// end of file