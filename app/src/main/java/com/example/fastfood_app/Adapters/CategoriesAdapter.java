package com.example.fastfood_app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.CategoriesHolderBinding;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    Context context;
    List<Category> list;
    OnClickListener onClickListener;
    int selectedPosition = 0;

    public CategoriesAdapter(Context context, List<Category> list, OnClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_holder, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);

        holder.binding.categoryName.setText(category.getCategoryName());

        Glide.with(context).load(category.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.image);

        if(position == selectedPosition) {
            holder.binding.layout.setBackgroundResource(R.color.selectedCategory);
            holder.binding.categoryName.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.image.setColorFilter(context.getResources().getColor(R.color.white));
        }
        else {
            holder.binding.layout.setBackgroundResource(R.color.white);
            holder.binding.categoryName.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.image.setColorFilter(context.getResources().getColor(R.color.black));
        }

        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = pos;
                notifyDataSetChanged();
                onClickListener.onClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CategoriesHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CategoriesHolderBinding.bind(itemView);
        }
    }

    public interface OnClickListener{
        void onClick(int position);
    }
}
