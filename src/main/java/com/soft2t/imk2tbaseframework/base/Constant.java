package com.soft2t.imk2tbaseframework.base;

import android.text.TextUtils;

import com.soft2t.imk2tbaseframework.util.LogUtil;
import com.soft2t.imk2tbaseframework.util.file.FileUtil;

import java.io.File;

public class Constant {
	/** 游客 UserId */
	public final static String GUEST_ID = "-1";

	/** 正 斜杠(/) */
	public final static String SLASH = File.separator;

	/** 网络相关 */
	public static class Net {
		/** 协议 */
		public final static String SCHEMA = "http://";

		/** 协议 */
		public final static String SCHEMA_SECURE = "https://";

		/** 地址, 这个 需要在 子项目中 实现 */
		public static String TOMCAT_IP;

		/** 端口, 这个 需要在 子项目中 实现 */
		public static String TOMCAT_PORT;

		/** 项目名称 */
		public static String PROJECT_NAME;

		private static String CONNECT_HOST;
        private static String CONNECT_HOST_SECURE;

		/** 基础路径, 调用接口用这个 */
		public static String getConnectHost() {
			if (TextUtils.isEmpty(CONNECT_HOST)) {
				CONNECT_HOST = SCHEMA + TOMCAT_IP + ":" + TOMCAT_PORT + SLASH + PROJECT_NAME;
			}

			return CONNECT_HOST;
		}

		/** 加密路径, 调用接口用这个 */
		public static String getConnectHostSecure() {
			if (TextUtils.isEmpty(CONNECT_HOST_SECURE)) {
				CONNECT_HOST_SECURE = SCHEMA_SECURE + TOMCAT_IP + ":" + TOMCAT_PORT + SLASH + PROJECT_NAME;
			}

			return CONNECT_HOST_SECURE;
		}
	}

	/** 调试选项开关 */
	public static class Debug {
		/** 是否 是 开发模式, 是的话, 使用 {@link LogUtil} 中的方法 会打印 log 等, 这个 需要在 子项目中 实现 */
		public static boolean SHOW_DEVELOP_LOG;// XXX 发布的时候, 注意写成 false

		/** 是否 开启 代码质量 严格检查 模式, 这个 需要在 子项目中 实现 */
		public static boolean ENABLE_STRICT_MODE;// XXX 发布的时候, 注意写成 false
	}

	/** 日志相关 */
	public static class Log {
		/** 崩溃保存的路径 */
		public static String CRASH_LOG_PATH;

		public static String getCrashLogPath() {
			if (TextUtils.isEmpty(CRASH_LOG_PATH)) {
				CRASH_LOG_PATH = MyFile.getBasePath() + "CrashLog" + SLASH;
			}

			return CRASH_LOG_PATH;
		}
	}

	/** 文件相关 */
	public static class MyFile {
		/** 公司前缀 */
		public static final String FILE_PREFIX = "Soft2T";

		private static String BASE_PATH;

		/**
		 * 基础文件夹 <br />
		 * 存在内置存储卡: /SdCardX/Soft2T/com.soft2t.imk2tbaseframework/<br />
		 * 或者<br />
		 * 没有内置存储卡: /data/data/com.soft2t.imk2tbaseframework/files/<br />
		 */
		public static String getBasePath() {
			if (TextUtils.isEmpty(BASE_PATH)) {

				String baseSavePath = FileUtil.getBaseSavePath(BaseApplication.mApplicationContext);

				if (FileUtil.isSDCardEnable()) {
					BASE_PATH = baseSavePath + SLASH + //
							MyFile.FILE_PREFIX + SLASH + App.PACKAGE_NAME /* BaseApplication.mApplicationContext.getPackageName() */ + SLASH;
				} else {
					BASE_PATH = baseSavePath;
				}
			}

			File dir = new File(BASE_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			return BASE_PATH;
		}
	}

	/** 数据相关 */
	public static class Db {
		/** 数据库 名字, 这个 需要在 子项目中 实现 */
		public static String DB_NAME;

        /**
         * 当前数据库版本, 这个 需要在 子项目中 实现
         */
        public static int DB_VERSION;

		/** 用来检测 是否存在数据库的 表名, 这个 需要在 子项目中 实现 */
		public static String EXISTS_FOR_CHECKING;

		/** 用来检测 是否存在数据库的 表名, 默认名称, 需要 在数据库 创建 这一张表 */
		public static String EXISTS_FOR_CHECKING_DEFAULT = "ZZ_TEST_FOR_CHECK_ONLY";

		/** 录音保存的路径 */
		public static String RECORD_PATH = MyFile.getBasePath() + "ChatRecord" + SLASH;

		/** 本程序升级下载路径 */
		public static String SELF_UPDATE_DOWNLOAD_PATH = MyFile.getBasePath() + "UpdateDownload" + SLASH;
	}

	/** APP 整体相关 */
	public static class App {
		/** 引导页 等待时间 */
		public static final int HOW_MANY_MILLISECOND_FOR_SPLASH = 1500;

		/** 双击退出时 的 间隔等待时间 */
		public static final int HOW_MANY_MILLISECOND_BETWEEN_DOUBLE_CLICK_FOR_EXIT = 2000;

		/** 这个 需要在 子项目中 实现 */
		public static String PACKAGE_NAME;
	}
}