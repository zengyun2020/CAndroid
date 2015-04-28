package com.crazychen.candroid.cand.httputil.request;

import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.crazychen.candroid.cand.httputil.base.Listener;
import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;
import com.crazychen.candroid.cand.httputil.config.RequestConfig;

public class ImageRequest extends Request<Bitmap> {
    private static final int IMAGE_TIMEOUT_MS = 1000;
    private static final int IMAGE_MAX_RETRIES = 2;
    private static final float IMAGE_BACKOFF_MULT = 2f;

    private Config mDecodeConfig;
    private int mMaxWidth;
    private int mMaxHeight;

    /** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
    private static final Object sDecodeLock = new Object();

    /**    
     * @param url
     * @param listener
     * @param maxWidth 图片最大宽度
     * @param maxHeight 图片最大高度
     * @param decodeConfig 图片颜色属性
     */
    public ImageRequest(String url, Listener<Bitmap> listener, int maxWidth, int maxHeight, Config decodeConfig) {
        super(HttpMethod.GET, url, listener);       
        //setRetryPolicy(new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
        mDecodeConfig = decodeConfig;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        this.setCacheExpireTime(TimeUnit.MILLISECONDS, 1000*60*60);
    }

	public ImageRequest(String url, int maxWidth, int maxHeight) {
		this(url, null, maxWidth, maxHeight, Config.RGB_565);
	}

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
  
    /**    
     * @param maxPrimary
     * @param maxSecondary
     * @param actualPrimary
     * @param actualSecondary
     * @return 缩放比例
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
            int actualSecondary) {
    	
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }
      
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }
             
        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    @Override
	public Bitmap parseResponse(Response response) {		
    	// Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                return doParse(response);
            } catch (OutOfMemoryError e) {
                //NetroidLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                //return Response.error(new ParseError(e));
            	return null;
            }
        }
	}
    
    /**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    private Bitmap doParse(Response response) {    	
        byte[] data = response.rawData;
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        if (mMaxWidth == 0 && mMaxHeight == 0) {//如果高宽都为0，直接返回原图片
            decodeOptions.inPreferredConfig = mDecodeConfig;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {          
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;
            //计算缩放倍数
            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                    actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                    actualHeight, actualWidth);
           
            decodeOptions.inJustDecodeBounds = false;
          
            decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap =
                BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);

            //如果缩放后大于需求，进行剪切
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        /*
        if (bitmap == null) {
            return Response.error(new ParseError(response));
        } else {
            return Response.success(bitmap, response);
        }
        */
        return bitmap;
    }
   
    /**
     * 寻找最合适的缩放比例，为2^n
     * @param actualWidth
     * @param actualHeight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
	}

	public void setDecodeConfig(Config decodeConfig) {
		this.mDecodeConfig = decodeConfig;
	}

	public void setMaxWidth(int maxWidth) {
		this.mMaxWidth = maxWidth;
	}

	public void setMaxHeight(int maxHeight) {
		this.mMaxHeight = maxHeight;
	}	
}
