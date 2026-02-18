# Use Cases

Focusing on three pillars of value: **Operational Efficiency** (doing more with less), **Data Integrity** (trusting your CMDB), and **Mean Time to Resolution** (fixing problems faster).



#### Automated Onboarding & Offboarding (Zero-Touch Provisioning)

Instead of technicians manually clicking through forms, an HR system (like Workday or BambooHR) triggers your API script.

- **The Workflow:** HR marks an employee as "Hired" --> Script calls SDP API to create a "New Hire" Service Request --> Script allocates a laptop from the "In Stock" pool to the user --> Script adds the user to AD groups.
- **Value:** Eliminates "first day" delays for new employees and ensures immediate security revocation for terminated employees.

#### The "Self-Healing" CMDB

Asset data in the ServiceDesk is often stale. we can write a "reconciliation bot" that runs nightly.

- **The Workflow:** Your script queries your endpoint management tool (like Microsoft Intune, Jamf, or SCCM) to get the *real* last login time and OS version. It then loops through SDP via API to update the corresponding Asset records.
- **Value:** procurement decisions are based on accurate data. we stop buying licenses for "ghost" assets that no longer exist.

#### Monitoring-to-Ticket Integration

Connect your monitoring tools (Nagios, SolarWinds, Datadog) to SDP without relying on generic email parsers.

- **The Workflow:** Server CPU hits 99% --> Monitoring tool calls your API --> API checks if an open ticket already exists for this server (deduplication) --> If not, it creates a P1 Incident and assigns it to the Server Team.
- **Value:** Reduces "alert fatigue" and ensures critical outages are tracked with SLAs immediately.

#### Custom "Executive View" Dashboards

ServiceDesk built-in reporting can be rigid.

- **The Workflow:** A scheduled script fetches all P1 incidents and SLA breaches for the week, summarizes them, and pushes a clean JSON payload to a PowerBI dataset or a Slack channel for leadership.
- **Value:** Management gets visibility into IT performance without needing to log in to the tool.



### Advanced Integration & Automation Use Cases

These scenarios focus on connecting SDP to the rest of your IT ecosystem.

- **"Zero-Touch" Employee Lifecycle Management:**

  - **Onboarding:** Triggered by an HR system (e.g., Workday), the API automatically creates a "New Hire" request, assigns a laptop from inventory, creates AD accounts, and adds the user to the correct Department/Site in SDP.
  - **Offboarding:** Automatically retrieves all assets assigned to a terminated user, changes their state to "In Store" or "Pending Return," and locks the user's SDP login.

- **Security Incident Response (SOAR):**

  - If your SIEM (e.g., Splunk) detects a malware infection on a specific workstation, it calls the SDP API to find that asset, change its state to "Quarantined," and log a High Priority incident assigned to the Security Team with the asset attached.

- **DevOps "Change" Sync:**

  - When a developer pushes code to production via Jenkins or GitHub Actions, your pipeline script automatically creates a "Standard Change" record in SDP, logs the build number, attaches the commit log, and closes the change record upon success. This automates audit compliance (e.g., SOC2) without slowing down developers.

- **Procurement Reconciliation:**

  - Ingest a CSV/JSON invoice from a vendor (e.g., Dell, CDW). The API loops through the line items, checks if the serial numbers exist in SDP, updates the "Purchase Order" fields, and flags any serial numbers that were paid for but never received (never scanned into inventory).

  

### Reports Impossible (or Difficult) in the Web UI

The native reporting module is powerful but limited to SQL-like logic on existing tables. The API allows you to perform **processing** and **cross-referencing** that the UI cannot do.

- **The "Ghost Asset" Report:**
  - **Logic:** Fetch all assets from SDP. Query your Active Directory or Endpoint Manager (SCCM/Intune) for the "Last Login Timestamp."
  - **Result:** A list of assets that exist in SDP with status "In Use" but have not been seen on the network in 90+ days. These are lost assets costing you money.
- **Technician "True Load" Analysis:**
  - **Logic:** Native reports show "Tickets Closed." The API allows you to calculate "Time Spent vs. Shift Hours." You can fetch worklogs for a technician, sum the minutes, and compare it to their shift schedule (fetched from a separate HR API) to spot burnout or underutilization.
- **Repeat Offender (Lemon) Analysis:**
  - **Logic:** Native reports count tickets per asset. The API can identify assets that have had **3+ hardware failures** (e.g., "Hard Drive Replaced") within a 6-month window. This justifies replacing the model entirely rather than fixing it again.
- **SLA Breach "Wait Time" Histogram:**
  - **Logic:** Native reports tell you *if* an SLA was breached. The API lets you analyze *where* the ticket sat the longest. You can fetch the full history of a ticket and calculate the exact duration it spent in each "Status" (e.g., 4 hours in "Open", 20 hours in "Pending Vendor"). This pinpoints exactly which external team is causing your breaches.



### Audits & Data Integrity Checks

These are "sanitation" scripts that keep your CMDB trustworthy.

- **License Compliance Audit:**
  - Compare the "Software Installed" list (scanned from assets) against the "Licenses Purchased" count. The API can highlight deltas instantly: "We are using 50 copies of Visio but only own 40."
- **User Location Audit:**
  - Compare the "Site" field of a User in SDP against their "Office Location" in Active Directory. If they don't match, the API updates SDP. This ensures that when a user logs a ticket, a technician is dispatched to the *correct* building.
- **Duplicate Serial Number Check:**
  - Scan the entire asset database for duplicate Serial Numbers or MAC addresses (which can happen if an asset is manually entered twice with slightly different names). The UI makes this hard to find; the API makes it a simple script.



### Custom KPIs & Metrics

These metrics require calculation logic that doesn't exist in standard reporting tools.

- **Mean Time to "First Human Response" (MTTHR):**
  - SDP tracks "First Response," but that often includes auto-replies. Via API, you can filter through the "Conversation" history to find the first reply sent by an actual technician that is *not* a template.
- **Ticket "Bounce" Rate:**
  - Count how many times a ticket was reassigned between groups (e.g., Level 1 -> Network -> Server -> Level 1). A high bounce rate indicates poor triage or lack of knowledge base articles.
- **Cost Per Ticket (Financial View):**
  - Combine "Technician Hours" (from worklogs) * "Hourly Rate" (from a secure config or HR API). This gives management a dollar value for every support request (e.g., "Password resets cost us $15 each; automation would save $15k/year").
- **Asset Utilization Ratio:**
  - (Assets in "In Use" state) / (Total Assets Purchased). If this ratio is below 80%, you are buying hardware too fast or not retiring old stock efficiently.





## Proposed Development Roadmap

Since we have already built the **Authentication** and **GET Assets** foundation, we should evolve the API wrapper in this order to unlock the use cases above incrementally.

### **Quick Wins (Low Effort, High Visibility):**

- **Ghost Asset Report:** Easy to write (read-only), identifies waste immediately.
- **User Location Audit:** Cleans data and helps technicians find people.

**Mid-Term Value (Medium Effort, High Impact):**

- **Onboarding Automation:** Saves hours of manual work, deeply embedded in business processes.
- **Lemon Analysis:** Justifies hardware refresh budgets.

**Long-Term Transformation (High Effort, Strategic):**

- **Self-Healing CMDB:** Continuous reconciliation with Intune/SCCM.
- **DevOps/Change Integration:** Bridges the gap between developers and IT Ops.



#### Phase 1: The "Inventory Auditor" (Current Path)

- **Goal:** Read-only visibility into infrastructure.
- **New Endpoints to Wrap:**
  - `GET /requests` (Fetch incidents).
  - `GET /site` (Fetch locations).
- **Deliverable:** A console app that exports a "Stale Asset Report" (assets not updated in >90 days) or a "High Volume User Report" (users with >10 tickets this month).
- **Management Win:** "I can now give you reports that ServiceDesk natively cannot run."

#### Phase 2: The "Data Fixer" (Write Capability)

- **Goal:** Update and clean data programmatically.
- **New Endpoints to Wrap:**
  - `PUT /assets/{id}` (Update asset details).
  - `POST /assets` (Create new assets).
- **Deliverable:** A script that bulk-updates the "State" of 500 assets from "In Store" to "Retired" based on a CSV file from the disposal vendor.
- **Management Win:** "We just saved 20 hours of manual data entry for the inventory audit."

#### Phase 3: The "Ticket Automator" (Service Delivery)

- **Goal:** Create and manage work.
- **New Endpoints to Wrap:**
  - `POST /requests` (Create tickets).
  - `PUT /requests/{id}/notes` (Add work logs).
  - `PUT /requests/{id}/pickup` (Assign to technician).
- **Deliverable:** A "Password Reset" tool where a user verifies their identity via a separate portal, and the script automatically creates and closes a ticket in SDP for audit purposes.
- **Management Win:** "We automated 30% of our Level 1 helpdesk volume."

#### Phase 4: The "Event Reactor" (Advanced)

- **Goal:** Real-time reaction to changes.
- **Capability:** Implementation of **Webhooks** or Long Polling.
- **Deliverable:** When a ticket is marked "Critical," a script immediately sends an SMS to the on-call engineer via Twilio API.