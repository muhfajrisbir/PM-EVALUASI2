package com.example.praktikum2_13120220024;

import androidx.activity.result.contract.ActivityResultContracts;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private final int REQ_CODE_PERM_STORAGE = 1;
    private final int REQ_CODE_BUKA_GALLERY = 1;
    private final int REQ_CODE_BUKA_KAMERA = 2;
    private DataFileGambar dataFileGambar;
    private String BuildConfig;
    private String READ_MEDIA_IMAGES;
    private String WRITE_EXTERNAL_STORAGE;

    @SuppressWarnings("deprecation")
    private void bukaGallery(){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        startActivityForResult(intent, REQ_CODE_BUKA_GALLERY);
    }

    @SuppressWarnings("deprecation")
    public void bukaKameraClick(View view){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                dataFileGambar = getDataFileGambar();
                Uri uri = FileProvider.getUriForFile(this,
                        BuildConfig + ".fileprovider",
                        dataFileGambar.getFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQ_CODE_BUKA_KAMERA);
            }catch (Exception e){}
    }

    public void bukaGalleryClick(View view){
        int permissionReadStorage = -1;
        if (android.os.Build.VERSION.SDK_INT > 30)
            permissionReadStorage = ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES);
        else
            permissionReadStorage = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        if (permissionReadStorage != PackageManager .PERMISSION_GRANTED){
            if (android.os.Build.VERSION.SDK_INT > 30)
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_MEDIA_IMAGES},
                        REQ_CODE_PERM_STORAGE);
            else
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_CODE_PERM_STORAGE);
            return;
        }
        bukaGallery();
    }

    private DataFileGambar getDataFileGambar() throws Exception{
        DataFileGambar dataFileGambar = null;
        File StorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("Hasil", ".jpg", StorageDir);
        return dataFileGambar;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BUKA_GALLERY) {
            Cursor cursor;
            String col = "_data";
            String projection[] = {col};
            cursor = getApplicationContext().getContentResolver().query(data.getData(), projection, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndexOrThrow(col);
            String pathGambar = cursor.getString(index);
            Bitmap gambar = BitmapFactory.decodeFile(pathGambar);
            imageView.setImageBitmap(gambar);
        } else if (requestCode == REQ_CODE_BUKA_KAMERA) {
            Bitmap bitmap = BitmapFactory.decodeFile(dataFileGambar.getPathfile());
            imageView.setImageBitmap(bitmap);
            }

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERM_STORAGE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) bukaGallery();
    }

    private class READ_MEDIA_IMAGES {
    }

    private class WRITE_EXTERNAL_STORAGE {
    }
}