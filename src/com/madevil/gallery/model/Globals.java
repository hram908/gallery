package com.madevil.gallery.model;

public class Globals {
	public static final String HOST = "http://nitui.tk:8000";
	public static class Url {
		public static String getIndexByNew(int page_id) {
			return HOST + "/index/new/" + page_id;
		}
		public static String getIndexByHot(int page_id) {
			return HOST + "/index/hot/" + page_id;
		}
		public static String getUser(String user, int page_id) {
			return HOST + "/me/" + page_id;
		}
		public static String getPictureDetail(String picture_id) {
			return HOST + "/pic/" + picture_id + "/info";
		}
	}

}
