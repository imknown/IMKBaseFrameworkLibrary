package com.soft2t.imk2tbaseframework.util.math;

public class MathUtil {
	/**
	 * 计算角度的余切
	 * 
	 * @param degrees
	 *            角度(°)
	 * @return 余切值
	 */
	public static double cot(double degrees) {
		double cotFigure = 1 / Math.tan(deg2rad(degrees));

		return cotFigure;
	}

	/**
	 * 计算角度的正割
	 * 
	 * @param degrees
	 *            角度(°)
	 * @return 正割值
	 */
	public static double sec(double degrees) {
		double cotFigure = 1 / Math.cos(deg2rad(degrees));

		return cotFigure;
	}

	/**
	 * 计算角度的余割
	 * 
	 * @param degrees
	 *            角度(°)
	 * @return 余割值
	 */
	public static double csc(double degrees) {
		double cotFigure = 1 / Math.sin(deg2rad(degrees));

		return cotFigure;
	}

	/**
	 * 将角度转换为弧度
	 * 
	 * @param degree
	 *            度
	 * @return 弧度
	 */
	public static double deg2rad(double degree) {
		return degree / 180 * Math.PI;
	}

	/**
	 * 将弧度转换为角度
	 * 
	 * @param radian
	 *            弧度
	 * @return 度
	 */
	public static double rad2deg(double radian) {
		return radian * 180 / Math.PI;
	}
}
