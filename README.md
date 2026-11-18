# 💰 Personal Finance Manager 

An offline-first personal finance app for Android built with **Kotlin** and **Jetpack Compose**.
Track income, expenses and transfers across multiple accounts, organise spending with categories, set budgets and see where your money goes, all stored locally on the device.

> Built as a learning-to-production project using **MVVM + Clean Architecture** (data / domain / presentation).

---

## 📸 Screenshots

| Home | Add Expense | Analytics | Budget |
|:---:|:---:|:---:|:---:|
| ![Home](docs/screenshots/home.png) | ![Add Expense](docs/screenshots/add_expense.png) | ![Analytics](docs/screenshots/analytics.png) | ![Budget](docs/screenshots/budget.png) |

---

## ✨ Features

- **Add income, expense and transfer** entries with amount, note, date and payment method
- **Multiple accounts** (Cash, Bank, Credit Card, Debit Card, UPI, Wallet) with individual balances
- **Account sync**: linked accounts stay in sync when a transaction is added
- **Categories**
  - 8 built-in master categories (Food, Travel, Shopping, Bills, Health, Finance, Entertainment, Others)
  - **Select Popular**: pick up to 12 quick-access sub-categories (Swiggy, Petrol, Netflix, ...) with search and filter chips
  - **Create Custom** categories of your own
- **Add expense in one click** (Speed Expense) for the things you spend on every day
- **Charts and monthly summary** to see spending by category and by month
- **Budget** tracking per month
- **Premium UI** (pricing and feature screen, demo only, no real payment is connected)

---

## 🧭 App Structure

Bottom navigation with 4 tabs inside a main screen:

| Tab | Purpose |
|---|---|
| **Home** | Balance, accounts and quick add |
| **Details** | Full transaction list |
| **Analytics** | Charts and monthly summary |
| **Budget** | Monthly budgets |

Other screens (opened outside the bottom bar): Add Expense / Income / Transfer, Select Popular Categories, Create Custom Category, Add Account.

---

## 🏗️ Architecture

Clean Architecture with MVVM, split into three layers:

```
app/
├── data/            # Room database, entities, DAOs, repository implementations, seed data
├── domain/          # Models, repository interfaces, use cases (pure Kotlin)
└── presentation/    # Compose screens, ViewModels, navigation, theme
```

**Data flow:** `Composable → ViewModel → UseCase → Repository → Room (DAO)`, with `Flow` / `StateFlow` pushing updates back to the UI.

---

## 🛠️ Tech Stack

| Area | Tech |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, Clean Architecture |
| Dependency Injection | Hilt |
| Local Database | Room (only local storage, no backend) |
| Async | Coroutines, Flow, StateFlow |
| Navigation | Navigation Compose |

---

## 🚀 Getting Started

1. Clone the repo
   ```bash
   git clone https://github.com/sujals-android-world/<Expence_Tracker2>.git
   ```
2. Open it in **Android Studio** (latest stable version)
3. Let Gradle sync, then run on an emulator or a real device (min SDK as set in `build.gradle.kts`)

### 📦 Download APK
Get the latest APK from the [**Releases**](../../releases) section and install it on your phone
(allow "Install from unknown sources" when asked).

---

## 🔮 Upcoming

- [ ] 🎙️ Voice entry: add expenses by speaking
- [ ] 📷 Camera: scan bills and receipts
- [ ] ☁️ Supabase integration for cloud backup and sync
- [ ] 📄 Export to spreadsheet (Excel / CSV download)

---

## 👨‍💻 Author

**Sujalbhai Popatbhai**
Android Developer (Kotlin, Jetpack Compose)

- GitHub: [sujals-android-world](https://github.com/sujals-android-world)
- Email: sujalbhaibhatiya123@gmail.com

---

⭐ If you like this project, give it a star!
