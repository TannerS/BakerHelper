package io.dev.tanners.bakerhelper.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import java.util.List;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.model.Step;

public class StepsExpandableAdapter extends ExpandableRecyclerViewAdapter<StepsExpandableAdapter.StepHeaderViewHolder, StepsExpandableAdapter.StepBodyViewHolder> {
    private Context mContext;

    public StepsExpandableAdapter(Context mContext, List<? extends ExpandableGroup> groups) {
        super(groups);

        this.mContext = mContext;
    }

    @Override
    public StepHeaderViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_header, parent, false);

        return new StepHeaderViewHolder(view);
    }

    @Override
    public StepBodyViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_body, parent, false);

        return new StepBodyViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(StepBodyViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Step mStep = ((StepWrapper) group).getItems().get(childIndex);

        holder.mDescription.setText(mStep.getDescription());

//        ImageDisplay.loadImage(
//                (this.mContext),
//                // glide can handle images AND videos (to be processed as a thumbnail)
//                // else TODO check on file type
//                mStep.getThumbnailUrl(),
//                holder.mThumbnail
//        );

        // todo handle video here
    }

    @Override
    public void onBindGroupViewHolder(StepHeaderViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setTitle(group);
    }

    public class StepHeaderViewHolder extends GroupViewHolder {

        private TextView mShortDescription;

        public StepHeaderViewHolder(View itemView) {
            super(itemView);
            mShortDescription = itemView.findViewById(R.id.recipe_step_short_desc);
        }

        public void setTitle(ExpandableGroup group) {
            mShortDescription.setText(group.getTitle());
        }
    }

    public class StepBodyViewHolder extends ChildViewHolder {

        private TextView mDescription;
//        private TextView mVideoUrl;
        private ImageView mThumbnail;

        public StepBodyViewHolder(View itemView) {
            super(itemView);

            mDescription = itemView.findViewById(R.id.recipe_step_desc);
//            mVideoUrl = itemView.findViewById(R.id.recipe_step_video_url);
//            mThumbnail = itemView.findViewById(R.id.recipe_step_thumbnail_url);
        }

        public void onBind(Step mStep) {
            mDescription.setText(mStep.getDescription());
//            mVideoUrl.setText(mStep.getVideoUrl());
//            mThumbnail.setText(mStep.getThumbnailUrl());
        }
    }
}
