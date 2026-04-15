package com.sdcote.sdp;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import java.util.List;

/**
 * Models a User in ServiceDesk Plus.
 */
public class User {
    private DataFrame dataFrame;

    public User() {
        this.dataFrame = new DataFrame();
    }

    public User(DataFrame frame) {
        this.dataFrame = frame != null ? frame : new DataFrame();
    }

    public User(String json) {
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

    public String getFirstName() {
        return dataFrame.getAsString("first_name");
    }

    public void setFirstName(String firstName) {
        dataFrame.put("first_name", firstName);
    }

    public String getLastName() {
        return dataFrame.getAsString("last_name");
    }

    public void setLastName(String lastName) {
        dataFrame.put("last_name", lastName);
    }

    public String getEmailId() {
        return dataFrame.getAsString("email_id");
    }

    public void setEmailId(String emailId) {
        dataFrame.put("email_id", emailId);
    }

    public boolean isTechnician() {
        try {
            return dataFrame.getAsBoolean("is_technician");
        } catch (Exception e) {
            return false;
        }
    }

    public void setTechnician(boolean isTechnician) {
        dataFrame.put("is_technician", isTechnician);
    }

    public String getEmployeeId() {
        return dataFrame.getAsString("employee_id");
    }

    public void setEmployeeId(String employeeId) {
        dataFrame.put("employee_id", employeeId);
    }

    public String getJobTitle() {
        return dataFrame.getAsString("job_title");
    }

    public void setJobTitle(String jobTitle) {
        dataFrame.put("job_title", jobTitle);
    }

    public String getUserScope() {
        return dataFrame.getAsString("user_scope");
    }

    public void setUserScope(String userScope) {
        dataFrame.put("user_scope", userScope);
    }

    public DataFrame getDataFrame() {
        return dataFrame;
    }

    @Override
    public String toString() {
        return JSONMarshaler.marshal(dataFrame);
    }
}
