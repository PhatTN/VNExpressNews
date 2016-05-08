package com.phattn.vnexpressnews.media;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.media.MediaItemStatus;

import com.phattn.vnexpressnews.model.Video;

/**
 * MediaItem helps keep track of the current status of an media item.
 */
public final class MediaItem implements Parcelable {
    // immutable states
    private final String mItemId;
    private final Uri mUri;
    // changeable states
    private int mPlaybackState = MediaItemStatus.PLAYBACK_STATE_PENDING;
    private long mContentPosition;
    private long mContentDuration;
    private long mTimestamp;

    public MediaItem(String iid, Uri uri) {
        mItemId = iid;
        mUri = uri;

    }

    public MediaItem(Video video) {
        mItemId = String.valueOf(video.getVideoId());
        mUri = Uri.parse(video.getUrl());
    }

    protected MediaItem(Parcel in) {
        mItemId = in.readString();
        mUri = Uri.parse(in.readString());
        mPlaybackState = in.readInt();
        mContentPosition = in.readLong();
        mContentDuration = in.readLong();
        mTimestamp = in.readLong();
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    public void setState(int state) {
        mPlaybackState = state;
    }

    public void setPosition(long pos) {
        mContentPosition = pos;
    }

    public void setTimestamp(long ts) {
        mTimestamp = ts;
    }

    public void setDuration(long duration) {
        mContentDuration = duration;
    }

    public String getItemId() {
        return mItemId;
    }

    public Uri getUri() {
        return mUri;
    }

    public int getState() {
        return mPlaybackState;
    }

    public long getPosition() {
        return mContentPosition;
    }

    public long getDuration() {
        return mContentDuration;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public MediaItemStatus getStatus() {
        return new MediaItemStatus.Builder(mPlaybackState)
                .setContentPosition(mContentPosition)
                .setContentDuration(mContentDuration)
                .setTimestamp(mTimestamp)
                .build();
    }

    @Override
    public String toString() {
        String state[] = {
            "PENDING",
            "PLAYING",
            "PAUSED",
            "BUFFERING",
            "FINISHED",
            "CANCELED",
            "INVALIDATED",
            "ERROR"
        };
        return "[" + mItemId + "|"
                + "|" + state[mPlaybackState] + "] " + mUri.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mItemId);
        dest.writeString(mUri.toString());
        dest.writeInt(mPlaybackState);
        dest.writeLong(mContentPosition);
        dest.writeLong(mContentDuration);
        dest.writeLong(mTimestamp);
    }
}
