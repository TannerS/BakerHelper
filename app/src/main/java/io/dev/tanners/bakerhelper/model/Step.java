package io.dev.tanners.bakerhelper.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Step {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int stepId;
    private String shortDescription;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    @Ignore
    public Step() {
        // needed for parser
    }

    public Step(int id, int stepId, String shortDescription, String description, String videoUrl, String thumbnailUrl) {
        this.id = id;
        this.stepId = stepId;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    @Ignore
    public Step(int stepId, String shortDescription, String description, String videoUrl, String thumbnailUrl) {
        this.stepId = stepId;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    @JsonProperty("id")
    public int getStepId() {
        return stepId;
    }

    @JsonProperty("id")
    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @JsonProperty("videoURL")
    public String getVideoUrl() {
        return videoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    @JsonProperty("thumbnailURL")
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
