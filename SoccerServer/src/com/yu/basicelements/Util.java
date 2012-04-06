package com.yu.basicelements;

public final class Util {
	/**
	 * 计算两点间距
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return 距离
	 */
	public static double calDistance(double x1, double y1, double x2, double y2) {
		return Math.pow((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2), 0.5);
	}

	/**
	 * 从（x0，y0）到（x1，y1）的角度，PI制
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @return 角度
	 */
	public static double calAngle(double x0, double y0, double x1, double y1) {
		double l = Util.calDistance(x0, y0, x1, y1);
		double dx = x1 - x0;
		double dy = y1 - y0;
		double acos = Math.acos(dx / l);
		double asin = Math.asin(dy / l);

		/*
		 * 逆时针角度
		 */
		double angle = 0;
		if (acos < Math.PI / 2) {
			angle = asin;
		} else {
			angle = Math.PI - asin;
		}
		return angle;
	}

	/**
	 * 判断两个方向夹角时候小于90度，若是则认为同向
	 * @param s1x
	 * @param s1y
	 * @param s2x
	 * @param s2y
	 * @return
	 */
	public static boolean isDirectionSame(double s1x, double s1y, double s2x, double s2y) {
		double s = Math.pow((s1x * s1x + s1y * s1y), 0.5);
		s1x = s1x / s * 1;
		s1y = s1y / s * 1;
		s = Math.pow((s2x * s2x + s2y * s2y), 0.5);
		s2x = s2x / s * 1;
		s2y = s2y / s * 1;
		double l = calDistance(s1x, s1y, s2x,s2y);
		if(l > 1.414)
			return false;
		else 
			return true;
	}
}
