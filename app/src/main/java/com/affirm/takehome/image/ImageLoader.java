package com.affirm.takehome.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {

    private class LoadImageAsyncTask extends AsyncTask<URL, Void, Bitmap> {

        private ImageView mImageView;
        private URL mUrl;

        private LoadImageAsyncTask(ImageView imageView) {
            mImageView = imageView;
        }

        protected Bitmap doInBackground(URL... urls) {
            mUrl = urls[0];
            try {
                return BitmapFactory.decodeStream(mUrl.openConnection().getInputStream());
            } catch (Exception e) {
                return null;
            }
        }

        protected void onProgressUpdate(Void... progress) {
            // No-op
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                return;
            }
            mImageView.setImageBitmap(bitmap);
            addToMemoryCache(mUrl.toString(), bitmap);
            runningTasks.remove(mImageView.hashCode());
        }
    }

    public class BitmapCreator {

        private URL mUrl;

        private BitmapCreator(URL url) {
           mUrl = url;
        }

        public void into(ImageView imageView) {
            Bitmap cachedBitmap = getFromMemoryCache(mUrl.toString());
            if (cachedBitmap != null) {
                imageView.setImageBitmap(cachedBitmap);
            } else {
                LoadImageAsyncTask task = new LoadImageAsyncTask(imageView);
                runningTasks.put(imageView.hashCode(), task);
                task.execute(mUrl);
            }
        }
    }

    private final LruCache<String, Bitmap> memoryCache;

    private final Map<Integer, AsyncTask> runningTasks;

    private static ImageLoader INSTANCE = new ImageLoader();

    public static ImageLoader get() {
        return INSTANCE;
    }

    private ImageLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        runningTasks = new HashMap<>();
    }

    public BitmapCreator load(String urlString) {
        try {
            URL url = new URL(urlString);
            return new BitmapCreator(url);
        } catch (Exception e) {
            return null;
        }
    }

    public void cancelRequest(ImageView imageView) {
        AsyncTask runningTask = runningTasks.get(imageView.hashCode());
        if (runningTask != null) {
            runningTask.cancel(true);
        }
        imageView.setImageBitmap(null);
    }

    private void addToMemoryCache(String key, Bitmap bitmap) {
        if (getFromMemoryCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    private Bitmap getFromMemoryCache(String key) {
        return memoryCache.get(key);
    }

}
