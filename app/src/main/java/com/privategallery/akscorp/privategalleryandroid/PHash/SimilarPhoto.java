package com.privategallery.akscorp.privategalleryandroid.PHash;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.privategallery.akscorp.privategalleryandroid.PHash.entry.Group;
import com.privategallery.akscorp.privategalleryandroid.PHash.entry.Photo;

import java.util.ArrayList;
import java.util.List;

import static com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialogKt.CURRENT_PROGRESS_BROADCAST_RECEIVER;
import static com.privategallery.akscorp.privategalleryandroid.Dialogs.LoadDialogKt.PROGRESS_BROADCAST_RECEIVER_TAG;


/**
 * Created by gavin on 2017/3/27.
 */

public class SimilarPhoto {

    private static final String TAG = SimilarPhoto.class.getSimpleName();

    public static int DIFFERENT_DIST = 3;

    private static void sentProgressToReceiver(Context context, int progress) {
        Intent intent = new Intent();
        intent.setAction(PROGRESS_BROADCAST_RECEIVER_TAG);
        intent.putExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, progress);
        context.sendBroadcast(intent);
    }

    public static List<Group> find(Context context, List<Photo> photos) {
        calculateFingerPrint(photos);

        List<Group> groups = new ArrayList<>();

        double current = 0;
        double size = photos.size() * photos.size() / 2;
        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);

            List<Photo> temp = new ArrayList<>();
            temp.add(photo);

            for (int j = i + 1; j < photos.size(); j++) {

                Photo photo2 = photos.get(j);

                int dist = hamDist(photo.getFinger(), photo2.getFinger());

                if (dist < DIFFERENT_DIST) {
                    temp.add(photo2);
                    photos.remove(photo2);
                    j--;
                }
                current++;
            }


            sentProgressToReceiver(context, (int) ((current / size) * 100.0));

            Group group = new Group();
            group.setPhotos(temp);
            groups.add(group);
        }
        sentProgressToReceiver(context, 100);

        return groups;
    }


    private static void calculateFingerPrint(List<Photo> photos) {
        float scale_width, scale_height;

        for (Photo p : photos) {
            if (!p.getFinger().equals("")) {
                continue;
            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(p.getPath(), bmOptions);
            scale_width = 8.0f / bitmap.getWidth();
            scale_height = 8.0f / bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scale_width, scale_height);

            Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            p.setFinger(getFingerPrint_(scaledBitmap));

            bitmap.recycle();
            scaledBitmap.recycle();
        }
    }

    public static String getFingerPrint(Bitmap bitmap) {
        float scale_width, scale_height;

        scale_width = 8.0f / bitmap.getWidth();
        scale_height = 8.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale_width, scale_height);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

        String rez = getFingerPrint_(scaledBitmap);
        scaledBitmap.recycle();
        return rez;
    }

    private static String getFingerPrint_(Bitmap bitmap) {
        double[][] grayPixels = getGrayPixels(bitmap);
        double grayAvg = getGrayAvg(grayPixels);
        return getFingerPrint(grayPixels, grayAvg);
    }


    private static String getFingerPrint(double[][] pixels, double avg) {
        int width = pixels[0].length;
        int height = pixels.length;

        byte[] bytes = new byte[height * width];

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] >= avg) {
                    bytes[i * height + j] = 1;
                    stringBuilder.append("1");
                } else {
                    bytes[i * height + j] = 0;
                    stringBuilder.append("0");
                }
            }
        }

        Log.d(TAG, "getFingerPrint: " + stringBuilder.toString());

        return stringBuilder.toString();
    }

    private static double getGrayAvg(double[][] pixels) {
        int width = pixels[0].length;
        int height = pixels.length;
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count += pixels[i][j];
            }
        }
        return count / (width * height);
    }


    private static double[][] getGrayPixels(Bitmap bitmap) {
        int width = 8;
        int height = 8;
        double[][] pixels = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = computeGrayValue(bitmap.getPixel(i, j));
            }
        }
        return pixels;
    }

    private static double computeGrayValue(int pixel) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = (pixel) & 255;
        return 0.3 * red + 0.59 * green + 0.11 * blue;
    }

    private static int hamDist(String finger1, String finger2) {
        int dist = 0;
        for (int i = 0; i < finger1.length(); i++) {
            if (dist > DIFFERENT_DIST) {
                return dist;
            }
            if (finger1.charAt(i) != finger2.charAt(i)) {
                dist++;
            }
        }
        return dist;
    }
}
