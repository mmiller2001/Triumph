package com.millermatute.triumph;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Dialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {

    private EditText task_title;
    private EditText task_description;
    private EditText task_due_date;
    private EditText task_date_completed;
//    private EditText task_priority;
    private String mTask_priority;

    private Spinner task_priority;

    private DialogListener dialogListener;
    final Calendar myCalendar= Calendar.getInstance();

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        builder.setView(view)
                .setTitle("Kanban Task")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mTask_title = task_title.getText().toString();
                        String mTask_description = task_description.getText().toString();
                        String mTask_due_date = task_due_date.getText().toString();
                        String mTask_date_completed = task_date_completed.getText().toString();
                        String mTask_priority_category = mTask_priority;

                        dialogListener.applyText(mTask_title,mTask_description,mTask_due_date,mTask_date_completed,mTask_priority_category);
                    }
                });

        task_title = view.findViewById(R.id.task_title);
        task_description = view.findViewById(R.id.task_description);
        task_due_date = view.findViewById(R.id.task_due_date);
        task_date_completed = view.findViewById(R.id.task_date_completed);
        //task_priority = view.findViewById(R.id.task_priority);
        task_priority = view.findViewById(R.id.task_priority);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Task_Priority, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        task_priority.setAdapter(adapter);
        task_priority.setOnItemSelectedListener(this);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
            }
        };

        task_date_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                update_date_completed();
            }
        });

        task_due_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                updateLabel();
            }
        });

        return builder.create();
    }

    private void update_date_completed() {
        String myFormat="MM/dd/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        task_date_completed.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void updateLabel() {
        String myFormat="MM/dd/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        task_due_date.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement DialogListener");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        mTask_priority = parent.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface DialogListener{
        void applyText(String task_title, String task_description, String task_due_date, String task_date_completed,
                       String task_priority);
    }
}
