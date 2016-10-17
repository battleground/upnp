package demo.abooc.com.upnp.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportState;

import demo.abooc.com.upnp.R;

/**
 * Created by author:李瑞宇
 * email:allnet@live.cn
 * on 16/7/14.
 */
public class VideoPlayerView {


    TextView mTitleText;
    ImageView mPlayButton;
    ImageView mPauseButton;
    ImageView mStopButton;
    ImageView mVolumeMute;

    TextView mProgressTime;
    TextView mDurationTime;
    TextView mVolumeValue;
    SeekBar mProgressBar;
    SeekBar mSeekBarVolume;

    private View mView;

    public VideoPlayerView(View view) {
        mView = view;
        initPlayerView();
    }

    private View findViewById(int id) {
        return mView.findViewById(id);
    }


    private void initPlayerView() {
        mTitleText = (TextView) findViewById(R.id.title);
        mPlayButton = (ImageView) findViewById(R.id.Play);
        mPauseButton = (ImageView) findViewById(R.id.Pause);
        mStopButton = (ImageView) findViewById(R.id.Stop);
        mVolumeMute = (ImageView) findViewById(R.id.VolumeMute);
        mVolumeValue = (TextView) findViewById(R.id.VolumeCurrentValue);

        mProgressTime = (TextView) findViewById(R.id.ProgressText);
        mDurationTime = (TextView) findViewById(R.id.DurationText);
        mProgressBar = (SeekBar) findViewById(R.id.Seek);
        mSeekBarVolume = (SeekBar) findViewById(R.id.SeekVolume);

    }

    public void setTitle(String title) {
        mTitleText.setText(title);
    }

    public void setOnButtonClickListener(View.OnClickListener player) {
        mPlayButton.setOnClickListener(player);
        mPauseButton.setOnClickListener(player);
        mStopButton.setOnClickListener(player);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener changeListener) {
        mProgressBar.setOnSeekBarChangeListener(changeListener);
    }

    public void attachRouterEvent(View.OnClickListener listener, SeekBar.OnSeekBarChangeListener changeListener) {
        mVolumeMute.setOnClickListener(listener);
        mSeekBarVolume.setOnSeekBarChangeListener(changeListener);
    }

    public void setProgressTime(String time) {
        mProgressTime.setText(time);
    }

    public void setDurationTime(String time) {
        mDurationTime.setText(time);
    }

    public void setPositionInfo(PositionInfo positionInfo) {
        long duration = positionInfo.getTrackDurationSeconds();
        long progress = positionInfo.getTrackElapsedSeconds();
        mProgressBar.setMax((int) duration);
        mProgressBar.setProgress((int) progress);

        String durationStr = positionInfo.getTrackDuration();
        String progressStr = positionInfo.getRelTime();
        mDurationTime.setText(durationStr);
        mProgressTime.setText(progressStr);
    }

    public void setState(TransportState state) {
        switch (state) {
            case PLAYING:
                mPauseButton.setVisibility(View.VISIBLE);
                mPlayButton.setVisibility(View.GONE);
                break;
            case PAUSED_PLAYBACK:
                mPauseButton.setVisibility(View.GONE);
                mPlayButton.setVisibility(View.VISIBLE);
                break;
            case STOPPED:
                mPauseButton.setVisibility(View.GONE);
                mPlayButton.setVisibility(View.VISIBLE);
                mProgressTime.setText("00:00:00");
                mDurationTime.setText("00:00:00");
                mProgressBar.setProgress(0);
                break;

        }
    }

    public void setMute(boolean mute) {
        mVolumeMute.setImageResource(mute ? R.drawable.ic_action_volume_muted : R.drawable.ic_action_volume_on);
    }

    public void setVolume(long volume) {
        mVolumeValue.setText("" + volume);
    }

    public void seekVolume(long volume) {
        mSeekBarVolume.setProgress((int) volume);
    }
}
