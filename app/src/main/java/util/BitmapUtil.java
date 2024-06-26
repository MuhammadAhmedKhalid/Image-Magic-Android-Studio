package util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BitmapUtil {

    public static Bitmap uriToBitmap(Uri imageUri, Context context) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
    }

    public static Boolean saveImageToDevice(Context context, Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm ss", Locale.getDefault()).format(new Date());
        String fileName = "ImageMagic_" + timeStamp + ".png";

        ContentResolver contentResolver = context.getContentResolver();

        ContentValues imageDetails = new ContentValues();
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        imageDetails.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ImageMagic");

        try {
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDetails);
            if (uri != null) {
                OutputStream imageOutputStream = contentResolver.openOutputStream(uri);
                if (imageOutputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutputStream);
                    imageOutputStream.close();
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static Bitmap rotateBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
    }

    public static Bitmap createRoundedBitmap(Bitmap bitmap, float cornerRadius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true); // Enable anti-aliasing for smooth edges

        // Create a bitmap shader
        // A BitmapShader specifically allows you to use a bitmap as a fill pattern for shapes.
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);

        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint); // Draw rounded rectangle with specified corner radius

        return output;
    }

    public static Bitmap addBackgroundToBitmap(Bitmap originalBitmap, int color) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        int margin = 150;

        int newWidth =  width + 2 *  margin;
        int newHeight =  height + 2 *  margin;

        Bitmap newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(color);

        int right = margin + width;
        int bottom = margin + height;

        canvas.drawBitmap(originalBitmap, null, new Rect(margin, margin, right, bottom), null);

        return newBitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        // Create a Bitmap of the appropriate format to hold the drawable
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a Canvas to draw the drawable onto the Bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


}
