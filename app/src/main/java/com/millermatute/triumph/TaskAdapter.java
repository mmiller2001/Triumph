package com.millermatute.triumph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends FirebaseRecyclerAdapter<Task
        , TaskAdapter.taskViewHolder> {

    private Context context;
    private List<Task> listTask;

    public TaskAdapter(@NonNull FirebaseRecyclerOptions<Task> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskAdapter.taskViewHolder holder, int position, @NonNull Task model) {
        holder.task_person_creator.setText(model.getTask_person_creator());
        holder.task_date_created.setText(model.getTask_date_created());
        holder.task_date_modified.setText(model.getTask_date_modified());
        holder.task_time_created.setText(model.getTask_time_created());
        holder.task_title.setText(model.getTask_title());
        holder.task_priority.setText(model.getTask_priority());
        holder.task_description.setText(model.getTask_description());
        holder.task_due_date.setText(model.getTask_due_date());
        holder.task_date_completed.setText(model.getTask_date_completed());

        //holder.task_person_image.setImageResource(R.drawable.ic_baseline_person_24);

        FirebaseDatabase.getInstance().getReference("Users").child(model.getTask_creator_uid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User tempUser = snapshot.getValue(User.class);
                        if(tempUser.getPhotoUrl().equalsIgnoreCase("empty"))
                            holder.task_person_image.setImageResource(R.drawable.ic_baseline_person_24);
                        else {
                            Glide.with(context).load(tempUser.getPhotoUrl()).into(holder.task_person_image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @NonNull
    @Override
    public TaskAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_task_list, parent, false);
        return new TaskAdapter.taskViewHolder(view);
    }

    public class taskViewHolder extends RecyclerView.ViewHolder {
        CircleImageView task_person_image;
        TextView task_person_creator;
        TextView task_date_created;
        TextView task_date_modified;
        TextView task_time_created;
        TextView task_title;
        TextView task_priority;
        TextView task_description;
        TextView task_due_date;
        TextView task_date_completed;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_person_image = (CircleImageView) itemView.findViewById(R.id.task_person_image);
            task_person_creator = (TextView) itemView.findViewById(R.id.task_person_creator);
            task_date_created = (TextView) itemView.findViewById(R.id.task_date_created);
            task_date_modified = (TextView) itemView.findViewById(R.id.task_date_modified);
            task_time_created = (TextView) itemView.findViewById(R.id.task_time_created);
            task_title = (TextView) itemView.findViewById(R.id.task_title);
            task_priority = (TextView) itemView.findViewById(R.id.task_priority);
            task_description = (TextView) itemView.findViewById(R.id.task_description);
            task_due_date = (TextView) itemView.findViewById(R.id.task_due_date);
            task_date_completed = (TextView) itemView.findViewById(R.id.task_date_completed);
        }
    }
}
