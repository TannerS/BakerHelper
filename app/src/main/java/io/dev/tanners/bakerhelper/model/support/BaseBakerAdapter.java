package io.dev.tanners.bakerhelper.model.support;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Base adapter for our project
 *
 * @param <E>
 */
public class BaseBakerAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<E> mBase;

    /**
     *
     */
    public BaseBakerAdapter() {
        mBase = new ArrayList<E>();
    }

    /**
     * @param mBase
     */
    public BaseBakerAdapter(List<E> mBase) {
        this.mBase = mBase;
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inherit in child class
        return null;
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // inherit in child class
    }

    /**
     * get item count
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mBase == null ? 0 : mBase.size();
    }

    /**
     * update items
     *
     * @param mBase
     */
    public void updateAdapter(List<E> mBase)
    {
        // set new data
        this.mBase = mBase;
        // update adapter
        notifyDataSetChanged();
    }
}