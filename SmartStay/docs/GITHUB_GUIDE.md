# Upload SmartStay to GitHub

1. Create a new **empty** repository on github.com named `SmartStay` (no README, no .gitignore).
2. Open a terminal in the `SmartStay` project folder and run:

```bash
git init
git add .
git commit -m "SmartStay: hotel reservation system (CodeAlpha Task 4)"
git branch -M main
git remote add origin https://github.com/<your-username>/SmartStay.git
git push -u origin main
```

3. On the repository page: add a description ("Console-based hotel reservation system in Java 21 with OOP and File I/O") and topics: `java`, `oop`, `file-io`, `hotel-management`, `console-application`.
4. Replace the GitHub and LinkedIn placeholders in the README Author section, then commit and push again.
5. Optional: add a screenshot or demo video link at the top of the README.

Note: the `data/` files in the repo contain only sample data. Delete the booking lines before pushing if you do not want test bookings in the repo.
