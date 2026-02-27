# Login Screen UI Document

## Purpose
First screen that introduces users to program. Allows them to authenticate with a username and password pair, find
a lost password, save their login information, or create a new account if they are a new user.

## Components

### Fields
| Field | Type | Placeholder | Icon |
|-------|------|-------------|------|
| Username | Text input | "Username" | Person icon |
| Password | Password input | "Password" | Lock icon |

### Controls
- **Remember Me** — Checkbox
- **Submit** — Primary button 
- **Forgot Password** — Checkbox + label

### Navigation
- "New User?" label
- **Create New Account** — Secondary button (gold/yellow outline)

## Notes
- Expect a pop up message if either username or password is incorrect.
- Username and password field will auto populate if user is returning with remember me checked.