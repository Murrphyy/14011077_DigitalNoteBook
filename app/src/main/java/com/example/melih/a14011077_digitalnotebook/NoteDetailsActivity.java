package com.example.melih.a14011077_digitalnotebook;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.zip.Inflater;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText ed,datePickerED,timePickerED;
    ImageView imgV;
    TextView titleTV;
    Note note;
    ArrayList<String> pageState;
    PageStateHolder psh;
    LinearLayout layout;

    ArrayList<View> views;
    View viewForDialog;
    MediaPlayer mPlayer;

    int mYear,mMonth,mDay,hour,minute;
    private static final int RESULT_LOAD_IMG = 1;
    private static final int RESULT_CHANGE_IMG = 2;
    private static final int RESULT_LOAD_AUDIO = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleTV=(TextView)findViewById(R.id.noteDetailsTitleTV) ;
        layout=(LinearLayout)findViewById(R.id.detailsLayout);
        views=new ArrayList<>();

        note=(Note)getIntent().getSerializableExtra("Note");
        titleTV.setText(note.getTitle());
        psh=note.getPageStateHolder();
        if(psh!=null){
            pageState=psh.getPageState();
            createPage(psh,layout);
        }else{
            psh=new PageStateHolder();
            pageState=psh.getPageState();
        }

        LinearLayout parentLayout=(LinearLayout)findViewById(R.id.parentLayoutDetails);
        NoteColor nc=note.getNoteColor();
        parentLayout.setBackgroundColor(Color.argb(nc.getA(),nc.getR(),nc.getG(),nc.getB()));

        FloatingActionButton addTvFab = (FloatingActionButton) findViewById(R.id.createTextView);
        addTvFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout=(LinearLayout)findViewById(R.id.detailsLayout);
                ed=new EditText(NoteDetailsActivity.this);
                if(ed.getParent() != null) {
                    ((ViewGroup)ed.getParent()).removeView(ed);
                }
                LinearLayout.LayoutParams lParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lParams.setMargins(0,10,0,0);
                ed.setLayoutParams(new LinearLayout.LayoutParams(lParams));
                Random generator=new Random();
                int randId;
                do{
                    randId=generator.nextInt(10000);
                }while(pageState.contains(Integer.toString(randId)));

                ed.setId(randId);
                psh.setView(1,ed,ed.getText().toString());
                layout.addView(ed);
                setLongTouchListener(ed);
                views.add(ed);
            }
        });

        FloatingActionButton uploadFab = (FloatingActionButton) findViewById(R.id.uploadView);
        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(NoteDetailsActivity.this);

                LayoutInflater inflater=LayoutInflater.from(NoteDetailsActivity.this);
                viewForDialog=inflater.inflate(R.layout.upload_file,null);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setView(viewForDialog);

                builder.show();

                FloatingActionButton uploadImageFab = (FloatingActionButton) viewForDialog.findViewById(R.id.imgFab);
                uploadImageFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout layout=(LinearLayout)findViewById(R.id.detailsLayout);
                        imgV=new ImageView(NoteDetailsActivity.this);
                        if(imgV.getParent() != null) {
                            ((ViewGroup)imgV.getParent()).removeView(imgV);
                        }
                        LinearLayout.LayoutParams lParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,400);
                        lParams.setMargins(0,20,0,0);

                        Random generator=new Random();
                        int randId;
                        do{
                            randId=generator.nextInt(10000);
                        }while(pageState.contains(Integer.toString(randId)));
                        imgV.setId(randId);


                        imgV.setLayoutParams(new LinearLayout.LayoutParams(lParams));
                        imgV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        layout.addView(imgV);
                        try {
                            if (ActivityCompat.checkSelfPermission(NoteDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(NoteDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMG);
                            } else {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                FloatingActionButton uploadAudioFab = (FloatingActionButton) viewForDialog.findViewById(R.id.audioFab);
                uploadAudioFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (ActivityCompat.checkSelfPermission(NoteDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(NoteDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_AUDIO);
                            } else {
                                Intent audioIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(audioIntent, RESULT_LOAD_AUDIO);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });


        FloatingActionButton setAlarmFab = (FloatingActionButton) findViewById(R.id.setAlarmBtn);
        setAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailsActivity.this);
                LayoutInflater inflater=LayoutInflater.from(NoteDetailsActivity.this);
                viewForDialog=inflater.inflate(R.layout.date_time_picker,null);
                builder.setView(viewForDialog);

                builder.setPositiveButton("Set Alarm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!datePickerED.getText().toString().equals("")&& !timePickerED.getText().toString().equals("")){
                            NotificationTimeHandler nth=new NotificationTimeHandler();
                            Calendar calendar=Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.YEAR, mYear);
                            calendar.set(Calendar.MONTH, mMonth);
                            calendar.set(Calendar.DAY_OF_MONTH, mDay);

                            calendar.set(Calendar.HOUR_OF_DAY,hour);
                            calendar.set(Calendar.MINUTE,minute);
                            nth.setTimetoNotify(NoteDetailsActivity.this,calendar,note);
                            Toast.makeText(NoteDetailsActivity.this,"Alarm OK!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NoteDetailsActivity.this,"Select Date!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.show();



                datePickerED=(EditText)viewForDialog.findViewById(R.id.datePickerED);
                timePickerED=(EditText)viewForDialog.findViewById(R.id.timePickerED);




                datePickerED.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentDate = Calendar.getInstance();
                        mYear = mcurrentDate.get(Calendar.YEAR);
                        mMonth = mcurrentDate.get(Calendar.MONTH);
                        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog mDatePicker = new DatePickerDialog(NoteDetailsActivity.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, month);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String myFormat = "dd/MM/yy"; //Change as you need
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                                datePickerED.setText(sdf.format(myCalendar.getTime()));

                                mDay =dayOfMonth;
                                mMonth = month;
                                mYear = year;
                            }
                        }, mYear, mMonth, mDay);
                        mDatePicker.show();
                    }
                });

                timePickerED.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(NoteDetailsActivity.this,R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if(selectedMinute<10){
                                    timePickerED.setText( selectedHour + ":0" + selectedMinute);
                                }else{
                                    timePickerED.setText( selectedHour + ":" + selectedMinute);
                                }

                                hour=selectedHour;
                                minute=selectedMinute;
                            }
                        }, hour, minute, true);
                        mTimePicker.show();
                    }
                });
            }
        });

        FloatingActionButton saveStateFab = (FloatingActionButton) findViewById(R.id.saveStateBtn);
        saveStateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePageStateHodler();
                Toast.makeText(NoteDetailsActivity.this,"Changes Saved",Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void setImageToPageState(Uri imgUri){
        if(imgUri!=null){
            psh.setView(2,imgV,imgUri.toString());
        }
    }

    public void setLongTouchListener(View v){
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(v);
                return false;
            }
        });
    }

    public void setClickListener(View v,final Uri uri){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer==null || !mPlayer.isPlaying()){
                    try{
                        mPlayer=new MediaPlayer();
                        mPlayer.setDataSource(NoteDetailsActivity.this,uri);
                        mPlayer.prepare();
                        mPlayer.start();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    mPlayer.stop();
                }
            }
        });
    }

    public void updatePageStateHodler(){
        for(View v : views){
            if(v instanceof TextView){
                psh.updateViewContent(v,((TextView) v).getText().toString());
            }
        }
        note.setPageStateHolder(psh);
        SharedPreferencesHandler sph=new SharedPreferencesHandler(NoteDetailsActivity.this);
        sph.updateNote(note);
    }

    public void removeFromPage(View v){
        layout.removeView(v);
    }

    public void showDeleteDialog(final View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an operation");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                psh.removeView(v);
                removeFromPage(v);
            }
        });
        if(v instanceof ImageView){
            builder.setNeutralButton("Change Image", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (ActivityCompat.checkSelfPermission(NoteDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(NoteDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMG);
                        } else {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            imgV=(ImageView)v;
                            startActivityForResult(galleryIntent, RESULT_CHANGE_IMG);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void setAudioSource(Uri uri,LinearLayout layout){
        Random generator=new Random();
        int randId;
        do{
            randId=generator.nextInt(10000);
        }while(pageState.contains(Integer.toString(randId)));
        ImageView iView=new ImageView(this);
        if(iView.getParent() != null) {
            ((ViewGroup)iView.getParent()).removeView(iView);
        }
        LinearLayout.LayoutParams lparams=new LinearLayout.LayoutParams(100,100);
        lparams.setMargins(0,10,0,0);
        iView.setLayoutParams(lparams);
        iView.setId(randId);
        iView.setBackgroundResource(android.R.drawable.ic_media_play);
        layout.addView(iView);
        views.add(iView);
        psh.setView(3,iView,uri.toString());
        setClickListener(iView,uri);
    }

    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if(reqCode==RESULT_CHANGE_IMG){

                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgV.setImageBitmap(selectedImage);
                    psh.updateViewContent(imgV,imageUri.toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(NoteDetailsActivity.this, "Process Failed", Toast.LENGTH_LONG).show();
                }
            }else if(reqCode==RESULT_LOAD_IMG){
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgV.setImageBitmap(selectedImage);
                    setImageToPageState(imageUri);
                    setLongTouchListener(imgV);
                    views.add(imgV);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(NoteDetailsActivity.this, "Process Failed", Toast.LENGTH_LONG).show();
                }
            }else if(reqCode==RESULT_LOAD_AUDIO){
                try{
                    Uri audioUri=data.getData();
                    setAudioSource(audioUri,layout);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }else {
            Toast.makeText(NoteDetailsActivity.this, "Choose an Image",Toast.LENGTH_LONG).show();
        }
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
                    Toast.makeText(NoteDetailsActivity.this,"Galllery Permission is Denied",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void createPage(PageStateHolder psholder,LinearLayout layout){
        ArrayList<String> viewContents=psholder.getViewContents();
        ArrayList<String> viewTypes=psholder.getViewTypes();
        ArrayList<String> pageState=psholder.getPageState();
        int i=0;
        for(String vId : pageState){
            String type=viewTypes.get(i);
            if(type.equals("txt")){
                EditText edt=new EditText(this);
                edt.setId(Integer.parseInt(vId));
                if(edt.getParent() != null) {
                    ((ViewGroup)edt.getParent()).removeView(edt);
                }
                LinearLayout.LayoutParams lParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lParams.setMargins(0,10,0,0);
                edt.setLayoutParams(new LinearLayout.LayoutParams(lParams));
                edt.setText(viewContents.get(i));
                layout.addView(edt);
                setLongTouchListener(edt);
                views.add(edt);

            }else if(type.equals("img")){
                ImageView image=new ImageView(this);
                if(image.getParent() != null) {
                    ((ViewGroup)image.getParent()).removeView(image);
                }
                LinearLayout.LayoutParams lParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,400);
                lParams.setMargins(0,20,0,0);

                image.setLayoutParams(new LinearLayout.LayoutParams(lParams));
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Uri imageUri=Uri.parse(viewContents.get(i));
                image.setId(Integer.parseInt(vId));
                layout.addView(image);
                try{
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    image.setImageBitmap(selectedImage);
                    setLongTouchListener(image);
                    views.add(image);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if(type.equals("audio")){
                ImageView image=new ImageView(this);
                if(image.getParent() != null) {
                    ((ViewGroup)image.getParent()).removeView(image);
                }
                LinearLayout.LayoutParams lParams= new LinearLayout.LayoutParams(100,100);
                lParams.setMargins(0,10,0,0);

                image.setLayoutParams(new LinearLayout.LayoutParams(lParams));
                image.setId(Integer.parseInt(vId));
                image.setBackgroundResource(android.R.drawable.ic_media_play);
                layout.addView(image);
                setClickListener(image,Uri.parse(viewContents.get(i)));
                setLongTouchListener(image);
                views.add(image);
            }
            i++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



}
