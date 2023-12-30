package com.example.app8.UIStudent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app8.R;
import java.util.List;

public class CustomJoinedClassesAdapter extends RecyclerView.Adapter<CustomJoinedClassesAdapter.ClassViewHolder> {

    private Context context;
    private List<String> classList;
    private OnClassClickListener onClassClickListener;

    public interface OnClassClickListener {
        void onClassClick(String className);
    }

    public void setOnClassClickListener(OnClassClickListener listener) {
        this.onClassClickListener = listener;
    }

    public CustomJoinedClassesAdapter(Context context, List<String> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        String className = classList.get(position);
        holder.bind(className);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void filterList(List<String> filteredList) {
        classList = filteredList;
        notifyDataSetChanged();
    }

    public class ClassViewHolder extends RecyclerView.ViewHolder {

        private TextView classNameTextView;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);

            itemView.setOnClickListener(view -> {
                if (onClassClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String className = classList.get(position);
                        onClassClickListener.onClassClick(className);
                    }
                }
            });
        }
        public void bind(String className) {
            classNameTextView.setText(className);
        }
    }
    public void updateClasses(List<String> classes) {
        classList.clear();
        classList.addAll(classes);
        notifyDataSetChanged();
    }

}
