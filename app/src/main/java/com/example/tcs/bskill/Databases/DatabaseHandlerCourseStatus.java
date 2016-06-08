package com.example.tcs.bskill.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tcs.bskill.Beans.CourseDetailsBean;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerCourseStatus extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bizSkill";
    private static final String TABLE_COURSE_DETAILS = "courseDetails";

    private static final String KEY_COURSE_ID = "id";
    private static final String KEY_AUDIO_STATUS = "audio";
    private static final String KEY_VIDEO_STATUS = "video";
    private static final String KEY_QUIZ_STATUS = "quiz";
    private static final String KEY_COURSE_PROGRESS = "progress";

    private static final String TABLE_OVERALL_PROGRESS = "progressDetails";
    private static final String KEY_OVERALL_PROGRESS_ID = "overallProgressid";
    private static final String KEY_OVERALL_PROGRESS = "overallProgress";

    public DatabaseHandlerCourseStatus(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSE_DETAILS_TABLE = "CREATE TABLE "
                + TABLE_COURSE_DETAILS
                + "("
                + KEY_COURSE_ID + " TEXT,"
                + KEY_AUDIO_STATUS + " TEXT,"
                + KEY_VIDEO_STATUS + " TEXT,"
                + KEY_QUIZ_STATUS + " TEXT,"
                + KEY_COURSE_PROGRESS + " TEXT"
                + ")";
        db.execSQL(CREATE_COURSE_DETAILS_TABLE);

        String CREATE_USER_DETAILS_TABLE = "CREATE TABLE "
                + TABLE_OVERALL_PROGRESS
                + "("
                + KEY_OVERALL_PROGRESS_ID + " TEXT,"
                + KEY_OVERALL_PROGRESS + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OVERALL_PROGRESS);
        onCreate(db);
    }

    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS info");
        this.onCreate(db);
    }

    public void addCourseDetails(CourseDetailsBean courseDetailsBean) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_ID, courseDetailsBean.getCourseID());
        values.put(KEY_AUDIO_STATUS, courseDetailsBean.getAudioStatus());
        values.put(KEY_VIDEO_STATUS, courseDetailsBean.getVideoStatus());
        values.put(KEY_QUIZ_STATUS, courseDetailsBean.getQuizStatus());
        values.put(KEY_COURSE_PROGRESS, courseDetailsBean.getCourseProgress());

        Log.d("CourseIDAdded", courseDetailsBean.getCourseID());

        db.insert(TABLE_COURSE_DETAILS, null, values);
        db.close();
    }

    public void addOverallProgress(String progress) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("OverallProgress", "Added");

        ContentValues values = new ContentValues();
        values.put(KEY_OVERALL_PROGRESS_ID, "1");
        values.put(KEY_OVERALL_PROGRESS, progress);

        db.insert(TABLE_OVERALL_PROGRESS, null, values);
        db.close();
    }

    public CourseDetailsBean getCourseDetailsByID(String courseID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("CourseIDFetched", courseID);

        Cursor cursor = db.query(TABLE_COURSE_DETAILS,
                new String[]
                        {
                                KEY_COURSE_ID,
                                KEY_AUDIO_STATUS,
                                KEY_VIDEO_STATUS,
                                KEY_QUIZ_STATUS,
                                KEY_COURSE_PROGRESS
                        },
                KEY_COURSE_ID + " = ?",
                new String[]{courseID},
                null,
                null,
                null,
                null);

        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        return new CourseDetailsBean(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
    }

    public String getOverallProgress() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OVERALL_PROGRESS,
                new String[]
                        {
                                KEY_OVERALL_PROGRESS
                        },
                KEY_OVERALL_PROGRESS_ID + " = ?",
                new String[]{"1"},
                null,
                null,
                null,
                null);

        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        return cursor.getString(0);
    }


    public List<CourseDetailsBean> getAllCoursesDetails() {
        List<CourseDetailsBean> courseDetailsBeanList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_COURSE_DETAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
                courseDetailsBean.setCourseID(cursor.getString(0));
                courseDetailsBean.setAudioStatus(cursor.getString(1));
                courseDetailsBean.setVideoStatus(cursor.getString(2));
                courseDetailsBean.setQuizStatus(cursor.getString(3));
                courseDetailsBean.setCourseProgress(cursor.getString(4));

                courseDetailsBeanList.add(courseDetailsBean);
            } while (cursor.moveToNext());
        }
        return courseDetailsBeanList;
    }

    public int updateCourseDetails(CourseDetailsBean courseDetailsBean) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_ID, courseDetailsBean.getCourseID());
        values.put(KEY_AUDIO_STATUS, courseDetailsBean.getAudioStatus());

        Log.d("OverallProgressInserted",courseDetailsBean.getAudioStatus());
        values.put(KEY_VIDEO_STATUS, courseDetailsBean.getVideoStatus());
        values.put(KEY_QUIZ_STATUS, courseDetailsBean.getQuizStatus());
        values.put(KEY_COURSE_PROGRESS, courseDetailsBean.getCourseProgress());

        return db.update(TABLE_COURSE_DETAILS, values, KEY_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseDetailsBean.getCourseID())});
    }

    public int updateOverallProgress(String progress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OVERALL_PROGRESS, progress);

        Log.d("OverallProgress", "Updated");

        return db.update(TABLE_OVERALL_PROGRESS, values, KEY_OVERALL_PROGRESS_ID + " = ?", new String[]{"1"});
    }

    public void deleteCourseDetails(CourseDetailsBean courseDetailsBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSE_DETAILS, KEY_COURSE_ID + " = ?", new String[]{String.valueOf(courseDetailsBean.getCourseID())});
        db.close();
    }

    public int getCourseDetailsCountByID(String courseID) {
        String countQuery = "SELECT * FROM " + TABLE_COURSE_DETAILS + " where id = " + courseID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int getCourseDetailsCount() {
        String countQuery = "SELECT * FROM " + TABLE_COURSE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int getOverallProgressCount() {
        String countQuery = "SELECT * FROM " + TABLE_OVERALL_PROGRESS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int getAudioCount() {
        String countQuery = "SELECT * FROM " + TABLE_COURSE_DETAILS + " where audio = '1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        Log.d("AudioCount", String.valueOf(cursor.getCount()));

        return cursor.getCount();
    }

    public int getVideoCount() {
        String countQuery = "SELECT * FROM " + TABLE_COURSE_DETAILS + " where video = '1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int getQuizCount() {
        String countQuery = "SELECT * FROM " + TABLE_COURSE_DETAILS + " where quiz = '1'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}