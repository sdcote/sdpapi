package com.sdcote.sdp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a full User object in ServiceDesk Plus Cloud.
 * Handles extensive details like VIP status, technician status, and contact info.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SDPUser {

    // --- Identity ---
    private String id;
    private String name;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String userScope; // e.g., "internal_user"

    // --- Contact Information ---
    private String emailId;
    private String phone;
    private String mobile;
    private String smsMail;
    private String smsMailId;

    // --- Role & Status ---
    private boolean isTechnician;
    private boolean isVipUser;
    private String jobTitle;
    private Double costPerHour; // Use Wrapper 'Double' as it can be null

    // --- Visuals ---
    private String photoUrl;

    // --- Nested References ---
    // Reusing the SDPReference class for these lookups
    private SDPReference site;
    private SDPReference department;

    // ==========================
    // Getters and Setters
    // ==========================

    @JsonProperty("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("first_name")
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @JsonProperty("last_name")
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    @JsonProperty("employee_id")
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    @JsonProperty("user_scope")
    public String getUserScope() { return userScope; }
    public void setUserScope(String userScope) { this.userScope = userScope; }

    @JsonProperty("email_id")
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    @JsonProperty("phone")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @JsonProperty("mobile")
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    @JsonProperty("sms_mail")
    public String getSmsMail() { return smsMail; }
    public void setSmsMail(String smsMail) { this.smsMail = smsMail; }

    @JsonProperty("sms_mail_id")
    public String getSmsMailId() { return smsMailId; }
    public void setSmsMailId(String smsMailId) { this.smsMailId = smsMailId; }

    @JsonProperty("is_technician")
    public boolean isTechnician() { return isTechnician; }
    public void setTechnician(boolean technician) { isTechnician = technician; }

    @JsonProperty("is_vip_user")
    public boolean isVipUser() { return isVipUser; }
    public void setVipUser(boolean vipUser) { isVipUser = vipUser; }

    @JsonProperty("job_title")
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    @JsonProperty("cost_per_hour")
    public Double getCostPerHour() { return costPerHour; }
    public void setCostPerHour(Double costPerHour) { this.costPerHour = costPerHour; }

    @JsonProperty("photo_url")
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @JsonProperty("site")
    public SDPReference getSite() { return site; }
    public void setSite(SDPReference site) { this.site = site; }

    @JsonProperty("department")
    public SDPReference getDepartment() { return department; }
    public void setDepartment(SDPReference department) { this.department = department; }

    @Override
    public String toString() {
        return "SDPUserReference{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", emailId='" + emailId + '\'' +
                '}';
    }
}