package com.sdcote.sdp;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import java.util.List;

/**
 * Models a Workstation in ServiceDesk Plus.
 */
public class Workstation {
    private DataFrame dataFrame;

    public Workstation() {
        this.dataFrame = new DataFrame();
    }

    public Workstation(DataFrame frame) {
        this.dataFrame = frame != null ? frame : new DataFrame();
    }

    public Workstation(String json) {
        if (json != null) {
            try {
                List<DataFrame> frames = JSONMarshaler.marshal(json);
                if (!frames.isEmpty()) {
                    this.dataFrame = frames.get(0);
                }
            } catch (Exception e) {
                this.dataFrame = new DataFrame();
            }
        }
        if (this.dataFrame == null) {
            this.dataFrame = new DataFrame();
        }
    }

    public String getId() {
        return dataFrame.getAsString("id");
    }

    public void setId(String id) {
        dataFrame.put("id", id);
    }

    public String getName() {
        return dataFrame.getAsString("name");
    }

    public void setName(String name) {
        dataFrame.put("name", name);
    }

    public String getBarcode() {
        return dataFrame.getAsString("barcode");
    }

    public void setBarcode(String barcode) {
        dataFrame.put("barcode", barcode);
    }

    public String getLastLoggedUser() {
        return dataFrame.getAsString("last_logged_user");
    }

    public void setLastLoggedUser(String user) {
        dataFrame.put("last_logged_user", user);
    }

    public boolean isServer() {
        try {
            return dataFrame.getAsBoolean("is_server");
        } catch (Exception e) {
            return false;
        }
    }

    public void setServer(boolean isServer) {
        dataFrame.put("is_server", isServer);
    }

    public User getLastUpdatedBy() {
        Object obj = dataFrame.getObject("last_updated_by");
        if (obj instanceof DataFrame) {
            return new User((DataFrame) obj);
        }
        return null;
    }

    public void setLastUpdatedBy(User user) {
        if (user != null) {
            dataFrame.put("last_updated_by", user.getDataFrame());
        } else {
            dataFrame.remove("last_updated_by");
        }
    }

    public User getCreatedBy() {
        Object obj = dataFrame.getObject("created_by");
        if (obj instanceof DataFrame) {
            return new User((DataFrame) obj);
        }
        return null;
    }

    public void setCreatedBy(User user) {
        if (user != null) {
            dataFrame.put("created_by", user.getDataFrame());
        } else {
            dataFrame.remove("created_by");
        }
    }

    public String getServiceTag() {
        Object cs = dataFrame.getObject("computer_system");
        if (cs instanceof DataFrame) {
            return ((DataFrame) cs).getAsString("service_tag");
        }
        return null;
    }

    public String getModel() {
        Object cs = dataFrame.getObject("computer_system");
        if (cs instanceof DataFrame) {
            return ((DataFrame) cs).getAsString("model");
        }
        return null;
    }

    public String getAssetTag() {
        return dataFrame.getAsString("asset_tag");
    }

    public void setAssetTag(String assetTag) {
        dataFrame.put("asset_tag", assetTag);
    }

    public DataFrame getDataFrame() {
        return dataFrame;
    }

    @Override
    public String toString() {
        return JSONMarshaler.marshal(dataFrame);
    }

    public String getStateName() {
        Object obj = dataFrame.getObject("state");
        if (obj instanceof DataFrame) {
            return ((DataFrame) obj).getAsString("name");
        }
        return null;
    }
}
