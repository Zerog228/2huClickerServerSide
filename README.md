# These three commands should be used to setup roles in the db: </br>
INSERT INTO roles(name) VALUES('ROLE_USER'); </br>
INSERT INTO roles(name) VALUES('ROLE_MODERATOR'); </br>
INSERT INTO roles(name) VALUES('ROLE_ADMIN'); </br>

# You will also need postgres DB with name 'tohodb'

# This is a server side of 2Hu Clicker!

### You can register users when server is enabled
On consequent login attempts, password and user name will be provided automatically. Email is needed only for registration
<img width="382" height="509" alt="image" src="https://github.com/user-attachments/assets/6c8713d0-ebcc-48fa-885b-d70348907b6b" />
<img width="386" height="561" alt="image" src="https://github.com/user-attachments/assets/b055c210-ba1d-463e-bee4-3ce5a4f078ae" />

### Enemies are generated based on seed
Because of that everyting is determinable, so only seed, current location level and login info is stored in user entry
<img width="1444" height="202" alt="image" src="https://github.com/user-attachments/assets/de40d300-b1ca-488e-8377-4a3a47e1ef25" />

### Also there are actions
Actions are every possible things that user can do. All action types are: "INIT", "UPGRADE", "KILL_BOSS"
<img width="475" height="164" alt="image" src="https://github.com/user-attachments/assets/9a7ee20b-c91e-439c-9dbc-04f5a2665ba2" />
<img width="1198" height="141" alt="image" src="https://github.com/user-attachments/assets/8b654299-e042-480f-8044-abdfd0b0db4f" />
Actions are related to each individual user. Knowing timestamps, seed and action, it's possible to calculate if user cheated or not: </br>
Is it mathematically possible to achieve certain amount of money/exp, make certain upgrades on certain location, defeat certain boss in given time.

### User report can be made
It's possible to make a selected user report: Every action will be shown and cheat rate will be calculated. Actions, that are currently detectable: </br>
Manipulations on timestamps, seed, money, location level, upgrade level. </br>
Possible but not yet implemented (At least not fully): </br>
Autoclicker detection. </br>
### Also there is a protection against password Brute-Force attacks</br>

### There are 2 separate mechanisms for saving progress
When playing localy, progress will be saved localy. </br> 
When online mode is used, DB connection is required on login. But if DB connection is lost during play, changes will also be saved localy until connection is restored.
After that everything will be synchronized with server </br>
<img width="389" height="345" alt="image" src="https://github.com/user-attachments/assets/9787ce93-0428-440b-ae70-c58ff7a80a40" />
