package com.example.melih.a14011077_digitalnotebook;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener{

    Intent createNote;
    Intent noteDetailsIntent;
    ArrayList<Note> notes=new ArrayList<>();
    SharedPreferencesHandler spHandler;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spHandler=new SharedPreferencesHandler(MainActivity.this);
        //spHandler.clearPreferences();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        createNote = new Intent(MainActivity.this,CreateNote.class);
        noteDetailsIntent=new Intent(MainActivity.this,NoteDetailsActivity.class);

        notes=spHandler.readNotes();
        Collections.sort(notes,Collections.<Note>reverseOrder());
        mAdapter = new MyAdapter(this,notes,this);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                startActivity(createNote);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_note) {
            startActivity(createNote);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void recyclerViewListClicked(View v, int position){
        noteDetailsIntent.putExtra("Note",notes.get(position));
        startActivity(noteDetailsIntent);
    }

    @Override
    public void recyclerViewListLongCliked(View v, int position) {
        showNoteDialog(notes.get(position),position);
    }

    public void showNoteDialog(final Note note,final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an operation");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spHandler.removeNote(note);
                notes.remove(position);
                mAdapter = new MyAdapter(MainActivity.this,notes,MainActivity.this);
                recyclerView.swapAdapter(mAdapter,false);
            }
        });

        builder.setNeutralButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                }
            });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
