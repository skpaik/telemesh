
package com.w3engineers.unicef.telemesh.data.local.feed;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("messageId")
    @Expose
    private String messageId;

    @SerializedName("geoLocation")
    @Expose
    private GeoLocation geoLocation;
    @SerializedName("connectedClients")
    @Expose
    private String connectedClients;

    @Nullable
    public String getMessageId() {
        return messageId;
    }

    @Nullable
    public Payload setMessageId(@Nullable String messageId) {
        this.messageId = messageId;
        return this;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(String connectedClients) {
        this.connectedClients = connectedClients;
    }

}
