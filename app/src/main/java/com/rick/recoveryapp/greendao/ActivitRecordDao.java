package com.rick.recoveryapp.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.rick.recoveryapp.greendao.entity.ActivitRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACTIVIT_RECORD".
*/
public class ActivitRecordDao extends AbstractDao<ActivitRecord, Long> {

    public static final String TABLENAME = "ACTIVIT_RECORD";

    /**
     * Properties of entity ActivitRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ID = new Property(0, Long.class, "ID", true, "_id");
        public final static Property UserName = new Property(1, String.class, "UserName", false, "USER_NAME");
        public final static Property UserNumber = new Property(2, String.class, "UserNumber", false, "USER_NUMBER");
        public final static Property ActivtType = new Property(3, String.class, "ActivtType", false, "ACTIVT_TYPE");
        public final static Property RecordTime = new Property(4, String.class, "RecordTime", false, "RECORD_TIME");
        public final static Property LongTime = new Property(5, String.class, "LongTime", false, "LONG_TIME");
        public final static Property Aduration = new Property(6, String.class, "Aduration", false, "ADURATION");
        public final static Property Pduration = new Property(7, String.class, "Pduration", false, "PDURATION");
        public final static Property Total_mileage = new Property(8, String.class, "total_mileage", false, "TOTAL_MILEAGE");
        public final static Property Calories = new Property(9, String.class, "Calories", false, "CALORIES");
        public final static Property Remark = new Property(10, String.class, "Remark", false, "REMARK");
        public final static Property B_Diastole_Shrink = new Property(11, String.class, "B_Diastole_Shrink", false, "B__DIASTOLE__SHRINK");
        public final static Property L_Diastole_Shrink = new Property(12, String.class, "L_Diastole_Shrink", false, "L__DIASTOLE__SHRINK");
        public final static Property SpasmCount = new Property(13, String.class, "spasmCount", false, "SPASM_COUNT");
    }


    public ActivitRecordDao(DaoConfig config) {
        super(config);
    }
    
    public ActivitRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACTIVIT_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: ID
                "\"USER_NAME\" TEXT," + // 1: UserName
                "\"USER_NUMBER\" TEXT," + // 2: UserNumber
                "\"ACTIVT_TYPE\" TEXT," + // 3: ActivtType
                "\"RECORD_TIME\" TEXT," + // 4: RecordTime
                "\"LONG_TIME\" TEXT," + // 5: LongTime
                "\"ADURATION\" TEXT," + // 6: Aduration
                "\"PDURATION\" TEXT," + // 7: Pduration
                "\"TOTAL_MILEAGE\" TEXT," + // 8: total_mileage
                "\"CALORIES\" TEXT," + // 9: Calories
                "\"REMARK\" TEXT," + // 10: Remark
                "\"B__DIASTOLE__SHRINK\" TEXT," + // 11: B_Diastole_Shrink
                "\"L__DIASTOLE__SHRINK\" TEXT," + // 12: L_Diastole_Shrink
                "\"SPASM_COUNT\" TEXT);"); // 13: spasmCount
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACTIVIT_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ActivitRecord entity) {
        stmt.clearBindings();
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(1, ID);
        }
 
        String UserName = entity.getUserName();
        if (UserName != null) {
            stmt.bindString(2, UserName);
        }
 
        String UserNumber = entity.getUserNumber();
        if (UserNumber != null) {
            stmt.bindString(3, UserNumber);
        }
 
        String ActivtType = entity.getActivtType();
        if (ActivtType != null) {
            stmt.bindString(4, ActivtType);
        }
 
        String RecordTime = entity.getRecordTime();
        if (RecordTime != null) {
            stmt.bindString(5, RecordTime);
        }
 
        String LongTime = entity.getLongTime();
        if (LongTime != null) {
            stmt.bindString(6, LongTime);
        }
 
        String Aduration = entity.getAduration();
        if (Aduration != null) {
            stmt.bindString(7, Aduration);
        }
 
        String Pduration = entity.getPduration();
        if (Pduration != null) {
            stmt.bindString(8, Pduration);
        }
 
        String total_mileage = entity.getTotal_mileage();
        if (total_mileage != null) {
            stmt.bindString(9, total_mileage);
        }
 
        String Calories = entity.getCalories();
        if (Calories != null) {
            stmt.bindString(10, Calories);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(11, Remark);
        }
 
        String B_Diastole_Shrink = entity.getB_Diastole_Shrink();
        if (B_Diastole_Shrink != null) {
            stmt.bindString(12, B_Diastole_Shrink);
        }
 
        String L_Diastole_Shrink = entity.getL_Diastole_Shrink();
        if (L_Diastole_Shrink != null) {
            stmt.bindString(13, L_Diastole_Shrink);
        }
 
        String spasmCount = entity.getSpasmCount();
        if (spasmCount != null) {
            stmt.bindString(14, spasmCount);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ActivitRecord entity) {
        stmt.clearBindings();
 
        Long ID = entity.getID();
        if (ID != null) {
            stmt.bindLong(1, ID);
        }
 
        String UserName = entity.getUserName();
        if (UserName != null) {
            stmt.bindString(2, UserName);
        }
 
        String UserNumber = entity.getUserNumber();
        if (UserNumber != null) {
            stmt.bindString(3, UserNumber);
        }
 
        String ActivtType = entity.getActivtType();
        if (ActivtType != null) {
            stmt.bindString(4, ActivtType);
        }
 
        String RecordTime = entity.getRecordTime();
        if (RecordTime != null) {
            stmt.bindString(5, RecordTime);
        }
 
        String LongTime = entity.getLongTime();
        if (LongTime != null) {
            stmt.bindString(6, LongTime);
        }
 
        String Aduration = entity.getAduration();
        if (Aduration != null) {
            stmt.bindString(7, Aduration);
        }
 
        String Pduration = entity.getPduration();
        if (Pduration != null) {
            stmt.bindString(8, Pduration);
        }
 
        String total_mileage = entity.getTotal_mileage();
        if (total_mileage != null) {
            stmt.bindString(9, total_mileage);
        }
 
        String Calories = entity.getCalories();
        if (Calories != null) {
            stmt.bindString(10, Calories);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(11, Remark);
        }
 
        String B_Diastole_Shrink = entity.getB_Diastole_Shrink();
        if (B_Diastole_Shrink != null) {
            stmt.bindString(12, B_Diastole_Shrink);
        }
 
        String L_Diastole_Shrink = entity.getL_Diastole_Shrink();
        if (L_Diastole_Shrink != null) {
            stmt.bindString(13, L_Diastole_Shrink);
        }
 
        String spasmCount = entity.getSpasmCount();
        if (spasmCount != null) {
            stmt.bindString(14, spasmCount);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ActivitRecord readEntity(Cursor cursor, int offset) {
        ActivitRecord entity = new ActivitRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // ID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // UserName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // UserNumber
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ActivtType
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // RecordTime
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // LongTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // Aduration
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // Pduration
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // total_mileage
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // Calories
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // Remark
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // B_Diastole_Shrink
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // L_Diastole_Shrink
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // spasmCount
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ActivitRecord entity, int offset) {
        entity.setID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserNumber(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setActivtType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRecordTime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLongTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAduration(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPduration(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTotal_mileage(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCalories(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setRemark(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setB_Diastole_Shrink(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setL_Diastole_Shrink(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSpasmCount(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ActivitRecord entity, long rowId) {
        entity.setID(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ActivitRecord entity) {
        if(entity != null) {
            return entity.getID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ActivitRecord entity) {
        return entity.getID() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
