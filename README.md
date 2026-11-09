# BloodLink – Emergency Blood Availability Network

Java Swing + JDBC + MySQL desktop app. Design: `BloodLink_Technical_Documentation.md`.

## Setup
1. JDK 17+ and MySQL 8.0.16+ running.
2. `mysql -u root -p < database/schema.sql` (optional demo data: `database/sample_data.sql`, synthetic only).
3. Connector/J is in `lib/mysql-connector-j.jar`.
4. Configure (environment variables):
   - `BLOODLINK_DB_PASSWORD` (default empty), `BLOODLINK_DB_USER` (default `root`),
     `BLOODLINK_DB_URL` (default `jdbc:mysql://localhost:3306/bloodlink_db`)
   - `BLOODLINK_ADMIN_PASSWORD` – optional; otherwise a random admin password is printed to the console on first run.
5. `./run.sh`

First run creates `admin@bloodlink.local`. Public registration only creates Seeker or Donor accounts.

## Notes
- Passwords: salted PBKDF2-HMAC-SHA256 (JDK only, no extra library); the design doc suggests Argon2id/bcrypt, which can be swapped into `PasswordHasher`.
- `blood_inventory` has an extra `source` column so stock is always shown with its source and update time.
- `blood_requests` has a `contact_phone` column (FR-05 contact details).
- Authorization is enforced in the service layer. Donor search is limited to seekers/admins.
- Results are potential donors only; no clinical compatibility decisions are made.
