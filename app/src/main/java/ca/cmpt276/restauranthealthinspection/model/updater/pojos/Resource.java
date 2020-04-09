package ca.cmpt276.restauranthealthinspection.model.updater.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Represents the JSON element for resource
 */
public class Resource {
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("last_modified")
    @Expose
    private Date lastModified;
    @SerializedName("position")
    @Expose
    private Integer position;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}


