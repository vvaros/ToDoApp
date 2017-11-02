package com.varos.todolist.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.varos.todolist.items.NoteItem;
import com.varos.todolist.R;
import com.varos.todolist.items.RealmDate;

import java.util.Calendar;

public class NoteCreateFragment extends Fragment {

    private ImageButton dateCancelButton;
    private ImageButton timeCancelButton;
    private ImageButton colorCancelButton;
    private ImageButton timePickerButton;

    private EditText descriptionEditText;
    private EditText titleEditText;

    private ImageView colorView;

    private TextView dateText;
    private TextView timeText;
    private TextView overdueText;

    private Switch mSwitch;


    private String description;
    private String title;

    private boolean dateChanged;
    private boolean timeChanged;
    private boolean editMode;
    private boolean notificationChecked;
    private boolean overdue;

    private int color;

    private NoteItem noteItem;

    private Calendar calendar;

    private CreateNoteListener createNoteListener;

    public NoteCreateFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_note, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendar = Calendar.getInstance();
        if (savedInstanceState != null) {
            color = savedInstanceState.getInt("color");
            title = savedInstanceState.getString("title");
            description = savedInstanceState.getString("description");
            notificationChecked = savedInstanceState.getBoolean("isNotificationChecked)");
            noteItem = savedInstanceState.getParcelable("noteItem");
            editMode = savedInstanceState.getBoolean("editMode");
            calendar = (Calendar) savedInstanceState.getSerializable("calendar");
            dateChanged = savedInstanceState.getBoolean("dateChanged");
            timeChanged = savedInstanceState.getBoolean("timeChanged");
            overdue = savedInstanceState.getBoolean("overdue");
        } else if (editMode && noteItem != null) {
            color = noteItem.getColor();
            title = noteItem.getTitle();
            description = noteItem.getDescription();
            timeChanged = noteItem.getDate().isTimeChanged();
            dateChanged = noteItem.getDate().isDateChanged();
            notificationChecked = noteItem.isNotificationEnabled();
            noteItem.getDate().initCalendar(calendar);
            if(calendar.getTimeInMillis() < System.currentTimeMillis()){
                overdue = true;
                notificationChecked = false;
            }
        } else {
            resetAll();
        }
        initUI(view);
    }

    private void initUI(View view) {
        titleEditText = view.findViewById(R.id.title_edit_view);
        titleEditText.setText(title);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 30){
                    titleEditText.setError(getString(R.string.title_max_char_count));
                    Toast.makeText(getActivity(),R.string.title_max_char_count,Toast.LENGTH_LONG);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        descriptionEditText = view.findViewById(R.id.description_edit_view);
        descriptionEditText.setText(description);
        descriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 30){
                    descriptionEditText.setError(getString(R.string.description_max_char_Count));
                    Toast.makeText(getActivity(),R.string.description_max_char_Count,Toast.LENGTH_LONG);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        colorView = view.findViewById(R.id.image_view_selected_color);
        colorView.setBackgroundColor(color);

        ImageButton colorPickerButton = view.findViewById(R.id.btn_choose_image);
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });

        ImageButton dateChooserButton = view.findViewById(R.id.date_chooser);
        dateChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateChooser();
            }
        });

        timePickerButton = view.findViewById(R.id.time_chooser);
        timePickerButton.setEnabled(false);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });

        overdueText = view.findViewById(R.id.overdue_text);

        dateText = view.findViewById(R.id.date_txt);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateChooser();
            }
        });
        timeText = view.findViewById(R.id.time_txt);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });

        mSwitch = view.findViewById(R.id.switch_notification);
        mSwitch.setChecked(notificationChecked);


        dateCancelButton = view.findViewById(R.id.date_chooser_cancel);
        dateCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwitch.setEnabled(false);
                mSwitch.setChecked(false);
                timeChanged = false;
                dateChanged = false;
                dateText.setText("");
                timeText.setText("");
                timeText.setClickable(false);
                dateCancelButton.setVisibility(View.GONE);
                timeCancelButton.setVisibility(View.GONE);
                timePickerButton.setEnabled(false);
            }
        });

        timeCancelButton = view.findViewById(R.id.time_chooser_cancel);
        timeCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwitch.setChecked(false);
                mSwitch.setEnabled(false);
                timeChanged = false;
                timeText.setText("");
                timeCancelButton.setVisibility(View.GONE);
            }
        });

        colorCancelButton = view.findViewById(R.id.color_chooser_cancel);
        colorCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color = 0;
                colorView.setBackgroundColor(0);
                colorCancelButton.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.note_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        view.findViewById(R.id.note_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNoteListener.deleteNote(noteItem);
                hideKeyboard();
            }
        });

        view.findViewById(R.id.note_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCreating();
                hideKeyboard();
            }
        });


        if (dateChanged) {
            dateText.setText(RealmDate.getDateInfo(calendar));
            dateCancelButton.setVisibility(View.VISIBLE);
            timePickerButton.setEnabled(true);
        }

        if (timeChanged) {
            timeText.setText(RealmDate.getTimeInfo(calendar));
            timeCancelButton.setVisibility(View.VISIBLE);
            mSwitch.setEnabled(true);
        }

        if (color != Color.TRANSPARENT) {
            colorView.setBackgroundColor(color);
            colorCancelButton.setVisibility(View.VISIBLE);
        }

        if (editMode) {
            view.findViewById(R.id.note_delete_button).setVisibility(View.VISIBLE);
        }

        if(overdue){
            overdueText.setVisibility(View.VISIBLE);
            mSwitch.setEnabled(false);
            mSwitch.setChecked(false);
        }
    }

    public void cancelCreating() {
        if (descriptionEditText.getText().toString().isEmpty() && titleEditText.getText().toString().isEmpty() && !dateChanged && color == 0) {
            createNoteListener.cancelCreating();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.discard_changes)
                    .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            createNoteListener.cancelCreating();
                            hideKeyboard();
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
    }

    private void openColorPicker() {
        hideKeyboard();
        if (color == 0) {
            color = R.color.colorPrimary;
        }
        ColorPickerDialogBuilder.with(getContext())
                .setTitle(R.string.choose_color)
                .initialColor(color)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .setPickerCount(5)
                .setPositiveButton(R.string.string_ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int color, Integer[] integers) {
                        colorView.setBackgroundColor(color);
                        NoteCreateFragment.this.color = color;
                    }
                })
                .setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .build()
                .show();
    }

    private void openDateChooser() {
        hideKeyboard();
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dateChanged = true;
                calendar.set(year, monthOfYear, dayOfMonth);
                dateText.setText(RealmDate.getDateInfo(calendar));
                dateCancelButton.setVisibility(View.VISIBLE);
                mSwitch.setChecked(false);
                mSwitch.setEnabled(false);
                timePickerButton.setEnabled(true);
                timeText.setClickable(true);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), ondate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void openTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                timeChanged = true;
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                timeText.setText(RealmDate.getTimeInfo(calendar));

                if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    mSwitch.setEnabled(true);
                    overdue = false;
                    overdueText.setVisibility(View.GONE);
                }else {
                    mSwitch.setEnabled(false);
                    mSwitch.setChecked(false);
                    overdue = true;
                    overdueText.setVisibility(View.VISIBLE);
                }
                timeCancelButton.setVisibility(View.VISIBLE);
            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }

    private void saveNote() {
        if (titleEditText.getText().toString().isEmpty()) {
            titleEditText.setError(getString(R.string.required_field));
            return;
        }
        if (descriptionEditText.getText().toString().isEmpty()) {
            descriptionEditText.setError(getString(R.string.required_field));
            return;
        }
        RealmDate date = new RealmDate(calendar);
        date.setTimeChanged(timeChanged);
        date.setDateChanged(dateChanged);
        NoteItem noteItem = new NoteItem();
        noteItem.setTitle(String.valueOf(titleEditText.getText()));
        noteItem.setDescription(String.valueOf(descriptionEditText.getText()));
        noteItem.setDate(date);
        noteItem.setNotificationEnabled(mSwitch.isChecked());
        noteItem.setColor(color);
        noteItem.setOverdue(overdue);
        if(overdue){
            noteItem.setNotificationEnabled(false);
        }
        if (editMode) {
            noteItem.setId(this.noteItem.getId());
            noteItem.setUserId(this.noteItem.getUserId());
            createNoteListener.refreshNoteList(this.noteItem, noteItem);
        } else {
            createNoteListener.openNoteList(noteItem);
        }
        hideKeyboard();
        resetAll();
    }

    public void setCreateNoteListener(CreateNoteListener listener) {
        this.createNoteListener = listener;
    }

    public void setNoteToCreate(NoteItem noteItem) {
        this.noteItem = noteItem;
        if (noteItem != null) {
            editMode = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        titleEditText.setText(title);
        descriptionEditText.setText(description);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", String.valueOf(titleEditText.getText()));
        outState.putString("description", String.valueOf(descriptionEditText.getText()));
        outState.putInt("color", color);
        outState.putBoolean("isNotificationChecked", mSwitch.isChecked());
        outState.putBoolean("editMode", editMode);
        outState.putParcelable("noteItem", noteItem);
        outState.putSerializable("calendar", calendar);
        outState.putBoolean("dateChanged", dateChanged);
        outState.putBoolean("timeChanged", timeChanged);
        outState.putBoolean("overdue",overdue);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        title = titleEditText.getText().toString();
        description = descriptionEditText.getText().toString();
    }

    private void resetAll() {
        color = 0;
        title = "";
        description = "";
        dateChanged = false;
        timeChanged = false;
        notificationChecked = false;
        calendar = Calendar.getInstance();
        editMode = false;
        noteItem = null;
        overdue = false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    public interface CreateNoteListener {

        void openNoteList(NoteItem noteItem);

        void refreshNoteList(NoteItem oldItem, NoteItem newItem);

        void deleteNote(NoteItem noteItem);

        void cancelCreating();
    }

}
