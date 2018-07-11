package io.dev.tanners.bakerhelper.model.support;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BaseBakerAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<E> mBase;

    public BaseBakerAdapter() {
        mBase = new ArrayList<E>();
    }

    public BaseBakerAdapter(List<E> mBase) {
        this.mBase = mBase;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inherit in child class
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // inherit in child class
    }

    @Override
    public int getItemCount() {
        return mBase == null ? 0 : mBase.size();
    }

    public void updateAdapter(List<E> mBase)
    {
        this.mBase = mBase;
        notifyDataSetChanged();
    }
}