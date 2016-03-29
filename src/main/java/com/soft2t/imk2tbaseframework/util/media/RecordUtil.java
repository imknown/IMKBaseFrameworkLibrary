package com.soft2t.imk2tbaseframework.util.media;

import java.io.IOException;

import com.soft2t.imk2tbaseframework.base.Constant.Db;
import com.soft2t.imk2tbaseframework.util.LogUtil;
import com.soft2t.imk2tbaseframework.util.media.MusicMediaPlayerUtil.IMediaPlayerPlayCallback;

import android.media.MediaRecorder;

public class RecordUtil {
	/** 语音文件保存路径 */
	private String fileFullPathWithName = null;

	/** 语音操作对象 */
	// private MediaPlayer mPlayer = null;

	private MediaRecorder mRecorder = null;

	// private Context context;

	private MusicMediaPlayerUtil mediaUtil = new MusicMediaPlayerUtil();

	// public RecordUtil(Context context) {
	// this.context = context;
	// }

	public void setFileFullPathWithName(String fileNameOnly) {
		// fileFullPathWithName = MyFile.getBasePath() + "/" + fileNameOnly + ".3gp";
		fileFullPathWithName = Db.RECORD_PATH + fileNameOnly + ".3gp";
	}

	/** 开始录音 */
	public void startRecord() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(fileFullPathWithName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			LogUtil.e("prepare() failed");
		}

		mRecorder.start();
	}

	/** 停止录音 */
	public void stopRecord() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	/** 播放录音 */
	public void startPlayRecord(IMediaPlayerPlayCallback iMediaPlayerPlayCallback) {
		mediaUtil.setiMediaPlayerCallback(iMediaPlayerPlayCallback);
		mediaUtil.startMediaPlayer(fileFullPathWithName);
	}

	/** 停止播放录音 */
	public void stopPlayRecord() {
		mediaUtil.stopMediaPlayer();
	}

	/** 退出时销毁mediaplayer */
	public void destoryRecord() {
		mediaUtil.destoryMediaPlayer();
	}
}
