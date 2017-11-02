package com.varos.todolist.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.varos.todolist.helpers.NoteAdapter;
import com.varos.todolist.items.NoteItem;
import com.varos.todolist.R;

import java.util.ArrayList;


public class NoteListFragment extends Fragment {

    private ImageButton gridButton;
    private ImageButton alignButton;

    private RecyclerView recyclerView;

    private NoteAdapter noteAdapter;

    private ArrayList<NoteItem> noteItems;

    private NoteListListerer noteListListerer;
    private boolean alignMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alignMode = true;
        if (savedInstanceState != null) {
            noteItems = savedInstanceState.getParcelableArrayList("noteItems");
            alignMode = savedInstanceState.getBoolean("mode");
        }

        recyclerView = view.findViewById(R.id.note_list);

        noteAdapter = new NoteAdapter();

        if (noteItems != null) {
            noteAdapter.setNoteItems(noteItems);
        }

        noteAdapter.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteListListerer.openCreateNoteCreate(noteItems.get(recyclerView.getChildLayoutPosition(view)));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteListListerer.openCreateNoteCreate(null);
            }
        });

        view.findViewById(R.id.log_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutUser();
            }
        });

        if (noteItems == null) {
            noteItems = new ArrayList<>();
        }

        gridButton = view.findViewById(R.id.grid_button);
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                noteAdapter.notifyDataSetChanged();
                alignButton.setVisibility(View.VISIBLE);
                gridButton.setVisibility(View.GONE);
                alignMode = false;
            }
        });

        alignButton = view.findViewById(R.id.align_button);
        alignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                noteAdapter.notifyDataSetChanged();
                gridButton.setVisibility(View.VISIBLE);
                alignButton.setVisibility(View.GONE);
                alignMode = true;

            }
        });
        if (alignMode) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            gridButton.setVisibility(View.VISIBLE);
            alignButton.setVisibility(View.GONE);
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            alignButton.setVisibility(View.VISIBLE);
            gridButton.setVisibility(View.GONE);
        }


        noteAdapter.setNoteItems(noteItems);
    }

    public void logOutUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.string_logout_answer)
                .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        noteListListerer.logOut();
                    }
                })
                .setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void refreshList(NoteItem oldItem, NoteItem newItem) {
        int position = noteItems.indexOf(oldItem);
        noteItems.set(position, newItem);
        noteAdapter.notifyDataSetChanged();
    }

    public void addNote(NoteItem noteItem) {
        noteAdapter.addNote(noteItem);
        noteAdapter.notifyDataSetChanged();
    }

    public void setNotes(ArrayList noteItems) {
        this.noteItems = noteItems;
    }

    public void setNoteListListerer(NoteListListerer noteListListerer) {
        this.noteListListerer = noteListListerer;
    }

    public void removeNote(NoteItem noteItem) {
        noteItems.remove(noteItem);
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("noteItems", noteItems);
        outState.putSerializable("mode", alignMode);
    }

    public interface NoteListListerer {
        void openCreateNoteCreate(NoteItem noteItem);

        void logOut();
    }

}
