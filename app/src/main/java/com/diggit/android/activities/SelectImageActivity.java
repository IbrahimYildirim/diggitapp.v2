package com.diggit.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.diggit.android.Controller;
import com.diggit.android.ModelFactory;
import com.diggit.android.R;
import com.diggit.android.model.ProfilePicture;

import java.io.File;
import java.io.IOException;

/**
 * Created by tokb on 30-03-2015.
 */
public class SelectImageActivity extends Activity {
    public final static String SELFIE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/selfie.jpg";
    private static final String TAG = SelectImageActivity.class.getSimpleName();
    int TAKE_PHOTO_CODE = 10;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker);

        Button takePicture = (Button) findViewById(R.id.takePicture);
        takePicture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
                        File file = new File(SELFIE_PATH);
                        if (file.exists()) {
                            file.delete();
                            file = new File(SELFIE_PATH);
                        }
                        Uri outputFileUri = Uri.fromFile(file);

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                    }
                });

        Button savePicture = (Button) findViewById(R.id.savePicture);
        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Pic saved");
                new SaveImageTask().execute();
            }
        });

        //Set icons for button - Font Awesome
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        savePicture.setTypeface(tf);
        takePicture.setTypeface(tf);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
//            Log.d(TAG, "Pic saved");
//            new SaveImageTask().execute();
            ImageView image = (ImageView)findViewById(R.id.imageSelecterView);
            File file = new File(SELFIE_PATH);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            image.setImageBitmap(bitmap);

            ViewGroup saveImageCont = (ViewGroup)findViewById(R.id.saveImageContainer);
            if (saveImageCont.getVisibility() == View.GONE){
                saveImageCont.setVisibility(View.VISIBLE);
            }
        }
        else if (resultCode == RESULT_CANCELED){
            Log.d("Image Picker", "Cancelled");
        }
        else {
            throw new RuntimeException("Handle error");
        }
    }


    private class SaveImageTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            ProfilePicture profilePicture = ModelFactory.getProfilePicture(SelectImageActivity.this);
            profilePicture.saveImage(getImageFromFile(), SelectImageActivity.this);

            return ModelFactory.sendPicture(SelectImageActivity.this);
        }

        private Bitmap getImageFromFile() {
            File file = new File(SELFIE_PATH);
            if (!file.exists()) {
                Log.i(TAG, "Image file was not found.");
                return null;
            }
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                float scaleRatio = Math.min(600f / (float) bitmap.getHeight(), 1f);

                Matrix matrix = new Matrix();
                matrix.preScale(scaleRatio, scaleRatio);

                ExifInterface ei = new ExifInterface(SELFIE_PATH);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Log.d(TAG, "Picture orientation: " + orientation);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }

                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            Controller.showStudentCardScreen(SelectImageActivity.this);
        }
    }
}
