package com.soft2t.imk2tbaseframework.util.media;

import com.soft2t.imk2tbaseframework.util.LogUtil;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class MusicMediaPlayerUtil {

	public interface IMediaPlayerPlayCallback {
		public void onFinish();

		public void onPlayError();

		public void onCallMethodError();

		public void whenMediaPlayerIsNull();

		public void whenMediaPlayerIsNotNull();
	}

	private MediaPlayer player;

	private IMediaPlayerPlayCallback iMediaPlayerPlayCallback;

	public void setiMediaPlayerCallback(IMediaPlayerPlayCallback iMediaPlayerPlayCallback) {
		this.iMediaPlayerPlayCallback = iMediaPlayerPlayCallback;
	}

	/** 播放 */
	public void startMediaPlayer(String path) {

		if (player != null) {
			if (iMediaPlayerPlayCallback != null) {
				iMediaPlayerPlayCallback.whenMediaPlayerIsNotNull();
			}

			try {
				if (player.isPlaying()) {
					//
				} else {
					player.setDataSource(path);
					player.prepare();
					player.start();

					// 播放完毕时的操作
					player.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {

							if (iMediaPlayerPlayCallback != null) {
								iMediaPlayerPlayCallback.onFinish();
							}

							player.release();
						}
					});

					// 播放错误时的操作
					player.setOnErrorListener(new OnErrorListener() {
						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {
							if (iMediaPlayerPlayCallback != null) {
								iMediaPlayerPlayCallback.onPlayError();
							}

							player.release();
							return false;
						}
					});
				}
			} catch (Exception e) {
				if (iMediaPlayerPlayCallback != null) {
					iMediaPlayerPlayCallback.onCallMethodError();
				}

				e.printStackTrace();
				player = null;
				player = new MediaPlayer();
			}
		} else {
			if (iMediaPlayerPlayCallback != null) {
				iMediaPlayerPlayCallback.whenMediaPlayerIsNull();
			}

			player = new MediaPlayer();

			if (player.isPlaying()) {
				//
			} else {
				try {
					player.setDataSource(path);
					player.prepare();
				} catch (Exception e) {
					if (iMediaPlayerPlayCallback != null) {
						iMediaPlayerPlayCallback.onCallMethodError();
					}

					e.printStackTrace();
				}
				player.start();

				// 播放完毕时的操作
				player.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						if (iMediaPlayerPlayCallback != null) {
							iMediaPlayerPlayCallback.onFinish();
						}

						player.release();
					}
				});

				// 播放错误时的操作
				player.setOnErrorListener(new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						if (iMediaPlayerPlayCallback != null) {
							iMediaPlayerPlayCallback.onPlayError();
						}

						player.release();
						return false;
					}
				});
			}
		}
	}

	/** 停止 */
	public void stopMediaPlayer() {
		if (player != null) {
			try {
				if (player.isPlaying()) {
					player.stop();
					player.release();
				}
			} catch (IllegalStateException e) {
				player = null;
				player = new MediaPlayer();
			}
		}
	}

	/** 退出销毁mediaplayer */
	public void destoryMediaPlayer() {
		if (player != null) {
			try {
				if (player.isPlaying()) {
					player.stop();
					player.release();
				} else {
					player.release();
				}
			} catch (IllegalStateException e) {
				player = null;
				player = new MediaPlayer();
			}
		}
	}

	public static void playViaSoundPool(Context context, final int resId) {
		// AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
		// attrBuilder.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);
		// attrBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT);
		// attrBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
		//
		// SoundPool.Builder spBuilder = new SoundPool.Builder();
		// spBuilder.setMaxStreams(2);
		// spBuilder.setAudioAttributes(attrBuilder.build());
		//
		// SoundPool sp21 = spBuilder.build();

		int maxStreams = 5, streamType = AudioManager.STREAM_NOTIFICATION, srcQuality = 0;
		@SuppressWarnings("deprecation")
		final SoundPool sp20 = new SoundPool(maxStreams, streamType, srcQuality);

		int priority = 1;
		final int soundId = sp20.load(context, resId, priority);

		// soundID : a soundID returned by the load() function
		// leftVolume : left volume value (range = 0.0 to 1.0), 左声道
		// rightVolume : right volume value (range = 0.0 to 1.0), 右声道
		// priority : stream priority (0 = lowest priority), 优先级
		// loop : loop mode (0 = no loop, -1 = loop forever), 循环与否
		// rate : playback rate (1.0 = normal playback, range 0.5 to 2.0), 播放返回的速度
		final float leftVolume = 1, rightVolume = 1;
		final int playPriority = 0, loop = 0;
		final float rate = 1.0F;

		sp20.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				int streamID = sp20.play(soundId, leftVolume, rightVolume, playPriority, loop, rate);

				if (streamID == 0 || status != 0) {
					LogUtil.e("Play sound " + resId + " fail.");
				}
			}
		});
	}
}
