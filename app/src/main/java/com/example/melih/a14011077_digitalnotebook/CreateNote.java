package com.example.melih.a14011077_digitalnotebook;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateNote extends AppCompatActivity {

    EditText titleED;
    Button createNote;
    SeekBar seekBarFont;
    NoteColor noteColor;
    String priority;
    ImageView coverImage;

    Uri coverImageUri=null;
    String[] priorities={"HIGH","MEDIUM","LOW"};
    Intent menuIntent,photoSelectIntent;
    Spinner prioritySP;
    ArrayAdapter<String> adapter;

    private static final int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        priority=null;

        menuIntent=new Intent(CreateNote.this,MainActivity.class);
        createNote=(Button)findViewById(R.id.createNoteBtn);
        titleED=(EditText)findViewById(R.id.noteTitleED);
        prioritySP=(Spinner)findViewById(R.id.prioritySP);
        coverImage=(ImageView)findViewById(R.id.coverImage);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySP.setAdapter(adapter);

        noteColor=new NoteColor();
        noteColor.setARGB(255,255,255,255);

        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(CreateNote.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateNote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMG);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        prioritySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority=priorities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority=priorities[0];
            }
        });

        LinearGradient test = new LinearGradient(0.f, 0.f, 750.f, 0.0f,
                new int[] { 0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(test);

        seekBarFont = (SeekBar)findViewById(R.id.seekbar_font);
        seekBarFont.setProgressDrawable( (Drawable)shape );


        seekBarFont.setMax(256*7-1);
        seekBarFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    if(progress < 256){
                        b = progress;
                    } else if(progress < 256*2) {
                        g = progress%256;
                        b = 256 - progress%256;
                    } else if(progress < 256*3) {
                        g = 255;
                        b = progress%256;
                    } else if(progress < 256*4) {
                        r = progress%256;
                        g = 256 - progress%256;
                        b = 256 - progress%256;
                    } else if(progress < 256*5) {
                        r = 255;
                        g = 0;
                        b = progress%256;
                    } else if(progress < 256*6) {
                        r = 255;
                        g = progress%256;
                        b = 256 - progress%256;
                    } else if(progress < 256*7) {
                        r = 255;
                        g = 255;
                        b = progress%256;
                    }
                    noteColor.setARGB(255,r,g,b);
                    seekBarFont.setBackgroundColor(Color.argb(255, r, g, b));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title;

                title=titleED.getText().toString();
                if(!title.equals("") && priority!=null){
                    Log.d("countofit",priority);
                    SharedPreferencesHandler phandler=new SharedPreferencesHandler(CreateNote.this);
                    Note note=new Note(title,noteColor,Calendar.getInstance().getTime());
                    note.setPriority(priority);
                    if(coverImageUri!=null){
                        note.setCoverImageUri(coverImageUri.toString());
                    }
                    PageStateHolder psh=new PageStateHolder();
                    note.setPageStateHolder(psh);
                    phandler.storeNote(note);
                    startActivity(menuIntent);
                }else{
                    Toast.makeText(CreateNote.this,"All Fields Must Be Filled",Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case RESULT_LOAD_IMG:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                } else {
                    Toast.makeText(CreateNote.this,"Galllery Permission is Denied",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                coverImage.setImageBitmap(selectedImage);
                coverImageUri=imageUri;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(CreateNote.this, "Process Failed", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(CreateNote.this, "Choose an Image",Toast.LENGTH_LONG).show();
        }
    }
}
