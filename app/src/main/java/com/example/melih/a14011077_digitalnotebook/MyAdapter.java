package com.example.melih.a14011077_digitalnotebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Note> mDataset;
    private LayoutInflater mInflater;
    private static RecyclerViewClickListener itemListener;
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        public TextView titleTV;
        public TextView dateTV;
        public CardView cardView;
        public ImageView coverImage;
        public MyViewHolder(View v) {
            super(v);
            titleTV = v.findViewById(R.id.noteTitle);
            cardView=v.findViewById(R.id.card_view);
            dateTV=v.findViewById(R.id.noteDate);
            coverImage=v.findViewById(R.id.noteImage);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        public void onClick(View v){
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            itemListener.recyclerViewListLongCliked(v,this.getLayoutPosition());
            return false;
        }
    }

    public MyAdapter(Context context, ArrayList<Note> myDataset, RecyclerViewClickListener listener) {
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        mDataset = myDataset;
        itemListener=listener;
    }

    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  mInflater.inflate(R.layout.my_textview_for_rv, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note=mDataset.get(position);
        holder.titleTV.setText(note.getTitle());
        Date date=note.getDate();
        String imgUriS=note.getCoverImageUri();

        if(date!=null){
            SimpleDateFormat format=new SimpleDateFormat("HH:mm dd-MM-yyyy");

            holder.dateTV.setText(format.format(date));
        }
        if(imgUriS!=null){
            try{
                Uri imgUri=Uri.parse(imgUriS);
                final InputStream imageStream = context.getContentResolver().openInputStream(imgUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                holder.coverImage.getLayoutParams().height=600;
                holder.coverImage.setImageBitmap(selectedImage);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        NoteColor nc=note.getNoteColor();
        int a=nc.getA();
        int r=nc.getR();
        int g=nc.getG();
        int b=nc.getB();
        holder.cardView.setCardBackgroundColor(Color.argb(a,r,g,b));
    }

    public int getItemCount() {
        return mDataset.size();
    }

}
