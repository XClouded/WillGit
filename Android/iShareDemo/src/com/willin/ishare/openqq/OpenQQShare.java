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
//		���return_activity�ǿ�ѡ��
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
            
            // ���롣feeds�ı��⣬�36�������֣��������ֻᱻ�ضϡ�
            parmas.putString("title", title);
            
            // ���롣����������ҳ��Դ�����ӣ��������ת����������ҳ��
            // ����http://��ͷ��
            parmas.putString("url", url + "#" + System.currentTimeMillis());
             
            // �û��������ݣ�Ҳ�з������ʱ�ķ������ɡ���ֹʹ��ϵͳ�����������д��档�40�������֣��������ֻᱻ�ضϡ�
            parmas.putString("comment", (comment + new Date()));
            // ���������ҳ��Դ��ժҪ���ݣ���������ҳ�ĸ�Ҫ������
            parmas.putString("summary", summary);
                                                       
            // �80�������֣��������ֻᱻ�ضϡ�
            // ���������ҳ��Դ�Ĵ�����ͼƬ����"������http://��ͷ����������255�ַ�������ͼƬ�����ߣ�|���ָ���Ŀǰֻ�е�һ��ͼƬ��Ч��ͼƬ���100*100Ϊ�ѡ�
            parmas.putString("images", imgUrl );
            
            // �������ݵ����͡�
            parmas.putString("type", "5");
            // ��������Ϊ256�ֽڡ�����type=5��ʱ����Ч��
            parmas.putString("playurl", playUrl );

            tencent.requestAsync(Constants.GRAPH_ADD_SHARE, parmas,Constants.HTTP_POST, listener, null);
    		
    	}
    }
    
    
    
    public void addTopic( Activity activity, IRequestListener listener, int type, String richval, String content, String address, int lbs_x, int lbs_y, String lbs_id, String lbs_idnm ) {
    	
    	if( ready(activity) ) {
    		
            Bundle params = new Bundle();
            params = new Bundle();
            
            // ��������ʱ���õ���Ϣ�����͡�1��ʾͼƬ��2��ʾ��ҳ�� 3��ʾ��Ƶ��
            params.putString("richtype", Integer.toString(type) );

            // ��������ʱ���õ���Ϣ��ֵ����richtypeʱ������richval
            params.putString("richval", ( richval + "#" + System.currentTimeMillis()));
            
            // ��������������ݡ�
            params.putString("con", content);
            
            // ��ַ��
            params.putString("lbs_nm", address);
            // ���ȡ���ʹ��ԭʼ���ݣ�����γ�ȣ�0-360����
            params.putString("lbs_x", Integer.toString(lbs_x));
            // γ�ȡ���ʹ��ԭʼ���ݣ�����γ�ȣ�0-360����
            params.putString("lbs_y", Integer.toString(lbs_y));
            // �ص�ID��lbs_id��lbs_idnmͨ��һ��ʹ�ã�����ȷ��ʶһ����ַ��
            params.putString("lbs_id", lbs_id);
            // �ص����ơ�lbs_id��lbs_idnmͨ��һ��ʹ�ã�����ȷ��ʶһ����ַ��
            params.putString("lbs_idnm", lbs_idnm);

            tencent.requestAsync(Constants.GRAPH_ADD_TOPIC, params, Constants.HTTP_POST, listener, null);
    		
    	}
    	
    }
    
    
    
    public void uploadPicture( Activity activity, IRequestListener listener, byte[] buff, String desc, String title, int x, int y ) {
    	
    	if( ready(activity) ) {
    		
    		 Bundle params = new Bundle();

    		 // ����.�ϴ���Ƭ���ļ����Լ�ͼƬ�����ݣ��ڷ�������ʱ��ͼƬ�����Զ���������������ʽ���ͣ������������ʾ������ע����Ƭ���Ʋ��ܳ���30���ַ���
             params.putByteArray("picture", buff);
             // ��Ƭ������ע����Ƭ�������ܳ���200���ַ���
             params.putString("photodesc", desc + new Date());
             
             // ��Ƭ��������������.jpg,.gif,.png,.jpeg,.bmp�����׺��β��
             params.putString("title", title);

             // ��Ƭ����ʱ�ĵ���λ�õľ��ȡ���ʹ��ԭʼ���ݣ�����γ�ȣ�0-360����
             params.putString( "x", Integer.toString(x) );
             // ��Ƭ����ʱ�ĵ���λ�õ�γ�ȡ���ʹ��ԭʼ���ݣ�����γ�ȣ�0-360����
             params.putString( "y", Integer.toString(y) );

             tencent.requestAsync(Constants.GRAPH_UPLOAD_PIC, params, Constants.HTTP_POST, listener, null);

    	}
    	
    }
    
    
    
    public void addAlbum( Activity activity, IRequestListener listener, String albumName, String desc, String priv, String question, String answer ) {
    	
    	if( ready(activity) ) {
    		
            Bundle params = new Bundle();
            
            // ���롣����������ܳ���30���ַ���
            params.putString("albumname", albumName + System.currentTimeMillis());
        	// ������������ܳ���200���ַ���
            params.putString("albumdesc", desc + new Date());
            
            // ���Ȩ�ޣ���ȡֵ����Ϊ�� 1=������3=ֻ���˿ɼ���4=QQ���ѿɼ���5=�ʴ���ܡ����������Ĭ��Ϊ����Ȩ�ޡ�
            params.putString("priv", priv);

            // ���privȡֵΪ5����������ʴ���ܵģ�������������ʹ�����������
            params.putString("question", question );
            // ���privȡֵΪ5����������ʴ���ܵģ�������������ʹ�����������
            params.putString("answer", answer );

            tencent.requestAsync(Constants.GRAPH_ADD_ALBUM, params, Constants.HTTP_POST, listener, null);
            
    	}
    	
    }
    
    
    
    public void requestNickTips( Activity activity, IRequestListener listener, int requestNum, int match ) {
    	
    	if( ready(activity) ) {
    		
            Bundle parmas = new Bundle();
            
            // �������(1-10)
            parmas.putString( "reqnum", Integer.toString(requestNum) );
            // �������
            parmas.putString( "match", Integer.toString(match) );

            tencent.requestAsync(Constants.GRAPH_NICK_TIPS, parmas, Constants.HTTP_GET, listener, null);    		
    	}
    	
    }
    
    
    
    
    public void requestIntimateFriends( Activity activity, IRequestListener listener, int requestNum ) {
        
    	if( ready(activity) ) {
    		
        	Bundle parmas = new Bundle();
        	
        	// �������(1-10)
            parmas.putString("reqnum", Integer.toString(requestNum));
            tencent.requestAsync(Constants.GRAPH_INTIMATE_FRIENDS, parmas,Constants.HTTP_GET, listener, null);
    	}    	
    }
    
	
	//================================================================================================================//
	
	

}


// end of file