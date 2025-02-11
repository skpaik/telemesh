package com.w3engineers.unicef.telemesh.data.local.usertable;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class UserDao extends BaseDao<UserEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long writeUser(@NonNull UserEntity userEntity);

    @Query("DELETE FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract int deleteUser(@NonNull String meshId);

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract Flowable<UserEntity> getUserById(@NonNull String meshId);

    @Nullable
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract UserEntity getSingleUserById(@NonNull String meshId);


    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.OFFLINE
            + " WHERE " + ColumnNames.COLUMN_USER_IS_ONLINE + " != " + Constants.UserStatus.OFFLINE)
    abstract int updateUserOffline();

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    public abstract Flowable<UserEntity> getLastInsertedUser();

    // Relatively faster then direct sub query, still expecting some more performance improvement of this query
    // Should be exactly minimum value

    /**
     * SELECT * FROM user LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE message_status WHEN 1 THEN 1 ELSE 0 END)
     * AS hasUnreadMessage, MAX(id) AS MAXID FROM messages GROUP BY friends_id) AS M INNER JOIN messages
     * AS MSG ON MSG.friends_id = M.friends_id WHERE MSG.id = M.MAXID) AS MESS ON user.mesh_id = MESS.friends_id
     * ORDER BY IFNULL(message_status,2) ASC, is_online DESC, user.user_name COLLATE NOCASE ASC
     *
     * @return
     */

    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.USERS + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN 1 ELSE 0 END) AS hasUnreadMessage, MAX(" + ColumnNames.ID + ") AS MAXID FROM "
            + TableNames.MESSAGE + " GROUP BY " + ColumnNames.COLUMN_FRIENDS_ID + ") AS M INNER JOIN "
            + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_FRIENDS_ID + " = M."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_MESH_ID + " = MESS."
            + ColumnNames.COLUMN_FRIENDS_ID + " ORDER BY IFNULL(" + ColumnNames.COLUMN_MESSAGE_STATUS + ","
            + Constants.MessageStatus.STATUS_READ + ") ASC, " + ColumnNames.COLUMN_USER_IS_ONLINE
            + " DESC, " + TableNames.USERS + "." + ColumnNames.COLUMN_USER_NAME + " COLLATE NOCASE ASC")
    abstract Flowable<List<UserEntity>> getAllUsers();


    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_IS_ONLINE + " = "
            + Constants.UserStatus.WIFI_ONLINE + " OR " + ColumnNames.COLUMN_USER_IS_ONLINE + " = "
            + Constants.UserStatus.BLE_ONLINE)
    @NonNull
    public abstract List<UserEntity> getLivePeers();

    @Query("SELECT " + ColumnNames.COLUMN_USER_MESH_ID + ", " + ColumnNames.COLUMN_USER_REGISTRATION_TIME
            + " FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_IS_SYNCED + " = " + 0)
    public abstract List<UserEntity.NewMeshUserCount> getUnSyncedUsers();

    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_SYNCED + " = " + 1
            + " WHERE " + ColumnNames.COLUMN_USER_IS_SYNCED + " = " + 0)
    abstract int updateUserToSynced();

    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_ONLINE + " = :activityStatus"
            + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract int updateUserStatus(String meshId, int activityStatus);

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.INTERNET_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE)
    abstract LiveData<List<UserEntity>> getActiveUser();

    @Query("SELECT COUNT(*) FROM " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE)
    abstract int getLocalUserCount();
}