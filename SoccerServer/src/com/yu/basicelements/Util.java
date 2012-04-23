package com.yu.basicelements;

import com.yu.overallsth.Player;

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
	 * 计算两个球员之间的距离
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double calDistance(Player p1, Player p2) {
		return Math.pow((p1.getPosition().getX() - p2.getPosition().getX()) * (p1.getPosition().getX() - p2.getPosition().getX()) + (p1.getPosition().getY() - p2.getPosition().getY()) * (p1.getPosition().getY() - p2.getPosition().getY()), 0.5);
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
	 * 判断两个方向夹角是否小于90度，若是则认为同向
	 * 
	 * @param s1x
	 * @param s1y
	 * @param s2x
	 * @param s2y
	 * @return
	 */
	public static boolean isDirectionSame(double s1x, double s1y, double s2x,
			double s2y) {
		double s = Math.pow((s1x * s1x + s1y * s1y), 0.5);
		s1x = s1x / s * 1;
		s1y = s1y / s * 1;
		s = Math.pow((s2x * s2x + s2y * s2y), 0.5);
		s2x = s2x / s * 1;
		s2y = s2y / s * 1;
		double l = calDistance(s1x, s1y, s2x, s2y);
		if (l > 1.414)
			return false;
		else
			return true;
	}

	/**
	 * 计算点到直线距离
	 * 
	 * @param x
	 * @param y
	 * @param A
	 * @param B
	 * @param C
	 * @return
	 */
	public static double calDistanceFromPointToLine(double x, double y,
			double A, double B, double C) {
		return Math.abs(A * x + B * y + C) / Math.pow(A * A + B * B, 0.5);
	}

	/**
	 * 计算点到直线的垂点坐标
	 * 
	 * @param x
	 * @param y
	 * @param A
	 * @param B
	 * @param C
	 * @return
	 */
	public static double[] getIntersectionFromPotToLine(double x, double y,
			double A, double B, double C) {
		double[] result = new double[2];
		result[0] = (B * B * x - A * B * y - A * C) / (A * A + B * B);
		result[1] = (A * A * y - A * B * x - B * C) / (A * A + B * B);
		return result;
	}

	/**
	 * 判断一个数是否夹在两个数中间
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isANumBetweenTwoNums(double x, double a, double b) {
		if (a >= b) {
			if (a >= x && x >= b)
				return true;
		} else {
			if (a <= x && x <= b)
				return true;
		}
		return false;
	}
}
