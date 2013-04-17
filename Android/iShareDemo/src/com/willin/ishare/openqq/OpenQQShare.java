package com.willin.ishare.openqq;


import java.net.URI;
import java.util.Date;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import com.willin.ishare.base.IDoShare;

public class OpenQQShare implements IDoShare {
	
	private static final String TAG = "OpenQQShare";
	
	private static final String APP_ID = "100405322";
	
	private static final String APP_KEY = "2d86090c77e167d3b83f9e3db7244fff";
	
	private static final String SCOPE = "all";
	
    public static final String WX_ACTION = "action";
    
    public static final String WX_ACTION_INVITE = "invite";
    
    public static final String WX_RESULT_CODE = "ret";
    
    public static final String WX_RESULT_MSG = "msg";
    
    public static final String WX_RESULT = "result";
	
	
	private Tencent tencent = null;
	
	private Context context = null;
	
	
	//================================================================================================================//
	
	// constructor
	public OpenQQShare( Context ctx ) {
		tencent = Tencent.createInstance(APP_ID, ctx);
		context = ctx;
	}
	
	
    private boolean ready( Activity activity ) {
    	boolean ready = tencent.isSessionValid() && tencent.getOpenId() != null;        
        
    	if (!ready)
            Toast.makeText( activity, "login and get openId first, please!", Toast.LENGTH_SHORT).show();
        
        return ready;
    }	
	
	
    
	public void login( Activity activity, IUiListener listener ) {
		
        if (!tencent.isSessionValid()) {        	
            tencent.login( activity, SCOPE, listener);
        } else {
        	tencent.logout(context);
        }
		
	}
	
	
	
	public void pay( Activity activity, IUiListener listener ) {		
		
		if ( tencent.isSessionValid() ) {
			tencent.pay( activity, listener );
		} else {
			Log.i(TAG, " isSessionValid = false");
		}
		
	}
	
	
	
	public void invite( Activity activity, IUiListener listener, String iconUrl, String appDesc, String source, String act ) {
		
		Bundle params = new Bundle();
		
		params.putString( Constants.PARAM_APP_ICON, iconUrl );
		params.putString( Constants.PARAM_APP_DESC, appDesc );
		params.putString( Constants.PARAM_SOURCE, source );
		params.putString( Constants.PARAM_ACT, act );
		
		tencent.invite( activity, params, listener );
		
	}
	
	
	
	public void sendStory( Activity activity, IUiListener listener, String title, String comment, String imgUrl, String summary, String swfUrl,
						String[] receivers, String act) {
		
		
		Bundle params = new Bundle();
		
		params.putString( Constants.PARAM_TITLE, title );
		params.putString( Constants.PARAM_COMMENT, comment );
		params.putString( Constants.PARAM_IMAGE, imgUrl );
		params.putString( Constants.PARAM_SUMMARY, summary );
		params.putString( Constants.PARAM_TYPE, "21" );
        params.putString( Constants.PARAM_PLAY_URL, swfUrl );
        params.putStringArray( Constants.PARAM_RECEIVER, receivers );
        params.putString( Constants.PARAM_ACT, act );
		
		tencent.story( activity, params, listener );
		
	}
	
	
	
	public void setAvatar( Activity activity, URI uri ) {
		
		Bundle params = new Bundle();
		params.putString(Constants.PARAM_AVATAR_URI, uri.toString());
//		这个return_activity是可选的
//		params.putString(Constants.PARAM_AVATAR_RETURN_ACTIVITY, "com.tencent.sample.ReturnActivity");
		tencent.setAvatar( activity, params );
		
	}
	
	
    public void openId( IRequestListener listener ) {
        if(tencent.isSessionValid()) {
        	tencent.requestAsync(Constants.GRAPH_OPEN_ID, null, Constants.HTTP_GET, listener, null);
        }
    }
	
	
    
    public void getUserInfo( Activity activity, IRequestListener listener ) {
        if( ready(activity) ) {
        	tencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, listener, null);
        }
    }
    
	
    
    public void checkVipInfo( Activity activity, IRequestListener listener ) {
        if( ready(activity) ) {
        	tencent.requestAsync(Constants.GRAPH_VIP_INFO, null,Constants.HTTP_GET, listener, null);
        }
    }
    
    
    
    public void checkVipRichInfo( Activity activity, IRequestListener listener ) {
        if( ready(activity) ) {
        	tencent.requestAsync(Constants.GRAPH_VIP_RICH_INFO, null,Constants.HTTP_GET, listener, null);
        }
    }
    
    
    
    public void checkListAlbum( Activity activity, IRequestListener listener ) {
        if( ready(activity) ) {
        	tencent.requestAsync(Constants.GRAPH_LIST_ALBUM, null,Constants.HTTP_GET, listener, null);
        }
    }
    
    
    
    public void addShare( Activity activity, IRequestListener listener, String title, String url, String comment, String summary, String imgUrl, String playUrl ) {
    	if( ready(activity) ) {
    		
            Bundle parmas = new Bundle();
            
            // 必须。feeds的标题，最长36个中文字，超出部分会被截断。
            parmas.putString("title", title);
            
            // 必须。分享所在网页资源的链接，点击后跳转至第三方网页，
            // 请以http://开头。
            parmas.putString("url", url + "#" + System.currentTimeMillis());
             
            // 用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
            parmas.putString("comment", (comment + new Date()));
            // 所分享的网页资源的摘要内容，或者是网页的概要描述。
            parmas.putString("summary", summary);
                                                       
            // 最长80个中文字，超出部分会被截断。
            // 所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
            parmas.putString("images", imgUrl );
            
            // 分享内容的类型。
            parmas.putString("type", "5");
            // 长度限制为256字节。仅在type=5的时候有效。
            parmas.putString("playurl", playUrl );

            tencent.requestAsync(Constants.GRAPH_ADD_SHARE, parmas,Constants.HTTP_POST, listener, null);
    		
    	}
    }
    
    
    
    public void addTopic( Activity activity, IRequestListener listener, int type, String richval, String content, String address, int lbs_x, int lbs_y, String lbs_id, String lbs_idnm ) {
    	
    	if( ready(activity) ) {
    		
            Bundle params = new Bundle();
            params = new Bundle();
            
            // 发布心情时引用的信息的类型。1表示图片；2表示网页； 3表示视频。
            params.putString("richtype", Integer.toString(type) );

            // 发布心情时引用的信息的值。有richtype时必须有richval
            params.putString("richval", ( richval + "#" + System.currentTimeMillis()));
            
            // 发布的心情的内容。
            params.putString("con", content);
            
            // 地址文
            params.putString("lbs_nm", address);
            // 经度。请使用原始数据（纯经纬度，0-360）。
            params.putString("lbs_x", Integer.toString(lbs_x));
            // 纬度。请使用原始数据（纯经纬度，0-360）。
            params.putString("lbs_y", Integer.toString(lbs_y));
            // 地点ID。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
            params.putString("lbs_id", lbs_id);
            // 地点名称。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
            params.putString("lbs_idnm", lbs_idnm);

            tencent.requestAsync(Constants.GRAPH_ADD_TOPIC, params, Constants.HTTP_POST, listener, null);
    		
    	}
    	
    }
    
    
    
    public void uploadPicture( Activity activity, IRequestListener listener, byte[] buff, String desc, String title, int x, int y ) {
    	
    	if( ready(activity) ) {
    		
    		 Bundle params = new Bundle();

    		 // 必须.上传照片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，见下面的请求示例），注意照片名称不能超过30个字符。
             params.putByteArray("picture", buff);
             // 照片描述，注意照片描述不能超过200个字符。
             params.putString("photodesc", desc + new Date());
             
             // 照片的命名，必须以.jpg,.gif,.png,.jpeg,.bmp此类后缀结尾。
             params.putString("title", title);

             // 照片拍摄时的地理位置的经度。请使用原始数据（纯经纬度，0-360）。
             params.putString( "x", Integer.toString(x) );
             // 照片拍摄时的地理位置的纬度。请使用原始数据（纯经纬度，0-360）。
             params.putString( "y", Integer.toString(y) );

             tencent.requestAsync(Constants.GRAPH_UPLOAD_PIC, params, Constants.HTTP_POST, listener, null);

    	}
    	
    }
    
    
    
    public void addAlbum( Activity activity, IRequestListener listener, String albumName, String desc, String priv, String question, String answer ) {
    	
    	if( ready(activity) ) {
    		
            Bundle params = new Bundle();
            
            // 必须。相册名，不能超过30个字符。
            params.putString("albumname", albumName + System.currentTimeMillis());
        	// 相册描述，不能超过200个字符。
            params.putString("albumdesc", desc + new Date());
            
            // 相册权限，其取值含义为： 1=公开；3=只主人可见；4=QQ好友可见；5=问答加密。不传则相册默认为公开权限。
            params.putString("priv", priv);

            // 如果priv取值为5，即相册是问答加密的，则必须包含问题和答案两个参数：
            params.putString("question", question );
            // 如果priv取值为5，即相册是问答加密的，则必须包含问题和答案两个参数：
            params.putString("answer", answer );

            tencent.requestAsync(Constants.GRAPH_ADD_ALBUM, params, Constants.HTTP_POST, listener, null);
            
    	}
    	
    }
    
    
    
    public void requestNickTips( Activity activity, IRequestListener listener, int requestNum, int match ) {
    	
    	if( ready(activity) ) {
    		
            Bundle parmas = new Bundle();
            
            // 请求个数(1-10)
            parmas.putString( "reqnum", Integer.toString(requestNum) );
            // 请求个数
            parmas.putString( "match", Integer.toString(match) );

            tencent.requestAsync(Constants.GRAPH_NICK_TIPS, parmas, Constants.HTTP_GET, listener, null);    		
    	}
    	
    }
    
    
    
    
    public void requestIntimateFriends( Activity activity, IRequestListener listener, int requestNum ) {
        
    	if( ready(activity) ) {
    		
        	Bundle parmas = new Bundle();
        	
        	// 请求个数(1-10)
            parmas.putString("reqnum", Integer.toString(requestNum));
            tencent.requestAsync(Constants.GRAPH_INTIMATE_FRIENDS, parmas,Constants.HTTP_GET, listener, null);
    	}    	
    }
    
	
	//================================================================================================================//
	
	

}


// end of file