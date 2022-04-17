package com.example.fastfood_app.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.CreateCategoriesHolderBinding;

import java.util.List;

public class SearchCategoriesAdapter extends RecyclerView.Adapter<SearchCategoriesAdapter.ViewHolder> {

    Context context;
    List<Category> list;
    OnClickListener onClickListener;
    OnLongClickListener onLongClickListener;

    public SearchCategoriesAdapter(Context context, List<Category> list, OnClickListener onClickListener, OnLongClickListener onLongClickListener) {
        this.context = context;
        this.list = list;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.create_categories_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);

        holder.binding.categoryName.setText(category.getCategoryName());

        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(pos);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure to delete this Category?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onLongClickListener.onClick(pos);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CreateCategoriesHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CreateCategoriesHolderBinding.bind(itemView);
        }
    }

    public interface OnClickListener{
        void onClick(int position);
    }

    public interface OnLongClickListener{
        void onClick(int position);
    }
}
