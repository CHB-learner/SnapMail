package com.example.snapmail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapmail.R;
import com.example.snapmail.model.Recipient;

import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder> {

    private List<Recipient> recipientList;
    private OnDeleteClickListener onDeleteClickListener;

    // 构造函数
    public RecipientAdapter(List<Recipient> recipientList, OnDeleteClickListener onDeleteClickListener) {
        this.recipientList = recipientList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public RecipientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipient_item, parent, false);
        return new RecipientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipientViewHolder holder, int position) {
        Recipient recipient = recipientList.get(position);
        holder.emailText.setText(recipient.getEmail());
        holder.remarkText.setText(recipient.getRemark());

        // 设置删除按钮点击事件
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDelete(recipient);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipientList.size();
    }

    // 视图持有者类
    public static class RecipientViewHolder extends RecyclerView.ViewHolder {
        TextView emailText;
        TextView remarkText;
        ImageButton btnDelete;

        public RecipientViewHolder(@NonNull View itemView) {
            super(itemView);
            emailText = itemView.findViewById(R.id.email_text);
            remarkText = itemView.findViewById(R.id.remark_text);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    // 删除点击事件接口
    public interface OnDeleteClickListener {
        void onDelete(Recipient recipient);
    }
}