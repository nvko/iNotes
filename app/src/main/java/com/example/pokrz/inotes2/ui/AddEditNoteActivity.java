package com.example.pokrz.inotes2.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokrz.inotes2.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "com.example.pokrz.inotes2.ui.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.pokrz.inotes2.ui.EXTRA_DESCRIPTION";
    public static final String EXTRA_ID = "com.example.pokrz.inotes2.ui.EXTRA_ID";
    public static final String EXTRA_DATE_CREATED = "com.example.pokrz.inotes2.ui.EXTRA_DATE_CREATED";
    public static final String EXTRA_DATE_UPDATED = "com.example.pokrz.inotes2.ui.EXTRA_DATE_UPDATED";
    public static final String EXTRA_IMAGE_PATH = "com.example.pokrz.inotes2.ui.EXTRA_IMAGE_PATH";
    private static final int PICK_IMAGE = 1;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private ImageView image_view_optional_photo;
    private Bitmap bitmap;
    private Uri bitmapPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        SearchView searchViewToolbar = toolbar.findViewById(R.id.search_view_toolbar);
        searchViewToolbar.setVisibility(View.GONE);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        image_view_optional_photo = findViewById(R.id.image_view_optional_photo);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            toolbarTitle.setText(R.string.edit_note);
            editTextTitle.setText(getIntent().getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));
            if (intent.getStringExtra(EXTRA_IMAGE_PATH) != null) {
                bitmapPath = Uri.parse(getIntent().getStringExtra(EXTRA_IMAGE_PATH));
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), bitmapPath);
                    image_view_optional_photo.setVisibility(View.VISIBLE);
                    image_view_optional_photo.setImageBitmap(bitmap);
                    image_view_optional_photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO: powiększ foto na kliknięcie
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            toolbarTitle.setText(R.string.add_note);
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        Date currentDate = Calendar.getInstance().getTime();
        Date dateCreated = (Date) getIntent().getSerializableExtra(EXTRA_DATE_CREATED);

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, R.string.note_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
            intent.putExtra(EXTRA_DATE_CREATED, dateCreated);
        } else {
            intent.putExtra(EXTRA_DATE_CREATED, currentDate);
        }
        intent.putExtra(EXTRA_DATE_UPDATED, currentDate);

        if (bitmapPath != null) {
            intent.putExtra(EXTRA_IMAGE_PATH, bitmapPath.toString());
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_menu, menu);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            MenuItem item = menu.findItem(R.id.note_menu_add_image);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.note_menu_save:
                saveNote();
                break;
            case R.id.note_menu_add_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            image_view_optional_photo.setVisibility(View.VISIBLE);
            if (data == null) {
                return;
            }
            try {
                bitmapPath = data.getData();
                Toast.makeText(this, bitmapPath.toString(), Toast.LENGTH_LONG).show();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), bitmapPath);
                image_view_optional_photo.setImageBitmap(bitmap);
                image_view_optional_photo.setVisibility(View.VISIBLE);
            } catch (IOException f) {
                f.printStackTrace();
            }
        }
    }
}

//TODO: category
//TODO: search z filtered list category