package com.varos.todolist.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.varos.todolist.items.NoteItem;
import com.varos.todolist.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<NoteItem> mNoteItems;
    private View.OnClickListener itemClickListener;


    public void setNoteItems(ArrayList<NoteItem> mNoteItems) {
        this.mNoteItems = mNoteItems;
        notifyDataSetChanged();
    }


    public void addNote(NoteItem noteItem) {
        mNoteItems.add(noteItem);
        notifyDataSetChanged();
    }

    public void setItemClickListener(View.OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        view.setOnClickListener(itemClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitleTxv.setText(mNoteItems.get(position).getTitle());
        holder.mDescriptionTxv.setText(mNoteItems.get(position).getDescription());
        holder.mColorView.setBackgroundColor(mNoteItems.get(position).getColor());
        if (mNoteItems.get(position).getDate().isDateChanged()) {
            String date = mNoteItems.get(position).getDate().getDateInfo();
            if (mNoteItems.get(position).getDate().isTimeChanged()) {
                date = date + " " + mNoteItems.get(position).getDate().getTimeInfo();
            }
            holder.mDateTx.setText(date);
        }
        if (mNoteItems.get(position).isNotificationEnabled()) {
            holder.mNotificationImv.setVisibility(View.VISIBLE);
        }
        if (mNoteItems.get(position).isOverdue()) {
            holder.mOverdueImv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mNoteItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mNotificationImv;
        ImageView mOverdueImv;

        TextView mTitleTxv;
        TextView mDescriptionTxv;
        TextView mDateTx;

        View mDateContainer;
        View mColorView;

        ViewHolder(View itemView) {
            super(itemView);
            mColorView = itemView.findViewById(R.id.color_view);
            mTitleTxv = itemView.findViewById(R.id.title_view);
            mDescriptionTxv = itemView.findViewById(R.id.description_view);
            mDateTx = itemView.findViewById(R.id.date_view);
            mDateContainer = itemView.findViewById(R.id.date_container);
            mNotificationImv = itemView.findViewById(R.id.notification_view);
            mOverdueImv = itemView.findViewById(R.id.overdue_view);
        }
    }
}
